package com.githubtracker.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Advanced JiraService:
 *  - fetchIssue(key) -> calls Jira REST API to get issue JSON
 *  - extractPRLink(issueJson) -> finds first GitHub PR URL in description or comments
 *  - buildTimeline(jiraIssueJson, prDetailsJson) -> constructs timeline combining commits, reviews & merge info
 *
 * Notes:
 *  - Requires jira.base-url, jira.email and jira.api-token in application.properties
 *  - Uses RestTemplate for HTTP calls
 *  - The controller expects GitHub PR details to be passed (or you can modify to call GitHub here)
 */
@Service
public class JiraService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    // Jira config from application.properties
    @Value("${jira.base-url}")
    private String jiraBaseUrl;

    @Value("${jira.email}")
    private String jiraEmail;

    @Value("${jira.api-token}")
    private String jiraApiToken;

    // Github Accept header needed sometimes for timeline endpoints
    private static final String GITHUB_ACCEPT = "application/vnd.github+json";

    // GitHub PR url regex - captures standard github PR links
    private static final Pattern GITHUB_PR_PATTERN =
            Pattern.compile("(https?://github\\.com/[^/]+/[^/]+/(?:pull|pull-requests)/\\d+)",
                    Pattern.CASE_INSENSITIVE);

    public JiraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Calls JIRA API to fetch issue JSON.
     * Example endpoint: https://your-domain.atlassian.net/rest/api/3/issue/{issueKey}
     */
    public Map<String, Object> buildPipeline(JsonNode issue, JsonNode prDetails) {

    List<Map<String, Object>> stages = new ArrayList<>();

    // 1️⃣ Commit timestamps
    for (JsonNode commit : prDetails.get("commits")) {
        stages.add(Map.of(
                "stage", "Commit Pushed",
                "timestamp", commit.get("commit").get("author").get("date").asText(),
                "actor", commit.get("commit").get("author").get("name").asText(),
                "details", commit.get("commit").get("message").asText()
        ));
    }

    // 2️⃣ PR Created
    stages.add(Map.of(
            "stage", "PR Created",
            "timestamp", prDetails.get("pr").get("created_at").asText(),
            "actor", prDetails.get("pr").get("user").get("login").asText(),
            "details", prDetails.get("pr").get("title").asText()
    ));

    // 3️⃣ Reviews
    for (JsonNode review : prDetails.get("reviews")) {
        stages.add(Map.of(
                "stage", "Review",
                "timestamp", review.get("submitted_at").asText(),
                "actor", review.get("user").get("login").asText(),
                "details", review.get("state").asText()
        ));
    }

    // 4️⃣ Jira workflow transitions
    JsonNode changelog = issue.get("changelog").get("histories");
    for (JsonNode entry : changelog) {
        for (JsonNode item : entry.get("items")) {
            if ("status".equals(item.get("field").asText())) {
                stages.add(Map.of(
                        "stage", "Jira Status Change",
                        "timestamp", entry.get("created").asText(),
                        "actor", entry.get("author").get("displayName").asText(),
                        "details", item.get("fromString").asText() + " → " + item.get("toString").asText()
                ));
            }
        }
    }

    // 5️⃣ PR merged → Build Success
    if (prDetails.get("pr").hasNonNull("merged_at")) {
        stages.add(Map.of(
                "stage", "Merged",
                "timestamp", prDetails.get("pr").get("merged_at").asText(),
                "actor", prDetails.get("pr").get("merged_by").get("login").asText(),
                "details", "Pull Request merged into main"
        ));
    }

    Map<String, Object> result = new HashMap<>();
    result.put("issueKey", issue.get("key").asText());
    result.put("summary", issue.get("fields").get("summary").asText());
    result.put("pipeline", stages);

    return result;
}

    public JsonNode getIssue(String issueKey) {
        if (issueKey == null || issueKey.isBlank()) {
            throw new IllegalArgumentException("issueKey is required");
        }

        String url = jiraBaseUrl;
        if (!url.endsWith("/")) url += "/";
        url += "rest/api/3/issue/" + issueKey + "?expand=renderedFields,comment";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Basic " + basicAuthHeader(jiraEmail, jiraApiToken));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            return mapper.readTree(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Jira issue JSON", e);
        }
    }

    /**
     * Scan Jira issue JSON (description + comments) and extract the first GitHub PR URL.
     * Returns null when none found.
     */
    public String extractPRLink(JsonNode jiraIssue) {
        if (jiraIssue == null) return null;

        // 1) Check renderedFields.description or fields.description
        String[] candidateFields = new String[] { "renderedFields", "fields" };

        for (String fld : candidateFields) {
            JsonNode rf = jiraIssue.get(fld);
            if (rf == null) continue;

            JsonNode desc = rf.get("description");
            if (desc != null && !desc.isNull()) {
                String txt = desc.asText();
                String match = findFirstGithubPrUrl(txt);
                if (match != null) return normalizePrUrl(match);
            }
        }

        // 2) Check comments (expand=comment used above)
        JsonNode commentsRoot = jiraIssue.path("fields").path("comment");
        if (commentsRoot.isMissingNode() || commentsRoot.isNull()) {
            commentsRoot = jiraIssue.path("comment"); // sometimes expanded there
        }

        if (commentsRoot != null && commentsRoot.has("comments")) {
            for (JsonNode c : commentsRoot.get("comments")) {
                String body = "";
                if (c.has("body")) body = c.get("body").asText();
                else if (c.has("renderedBody")) body = c.get("renderedBody").asText();

                String match = findFirstGithubPrUrl(body);
                if (match != null) return normalizePrUrl(match);
            }
        }

        // 3) Fallback: search whole JSON as text
        try {
            String asText = mapper.writeValueAsString(jiraIssue);
            String match = findFirstGithubPrUrl(asText);
            if (match != null) return normalizePrUrl(match);
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Build advanced timeline.
     * Accepts jiraIssue JSON and prDetails JSON (from GitHub PR API).
     *
     * Timeline events:
     *  - PR created
     *  - commit(s) with timestamps
     *  - review events (APPROVED, CHANGES_REQUESTED, COMMENTED) with submitted_at
     *  - rework (commit after review)
     *  - merge (merged_at)
     *
     * Returns a Map ready for JSON serialization:
     * {
     *   "issue": {key,title,...},
     *   "pr": {number,title,state,merged_at,url},
     *   "timeline": [ {type:"commit"/"review"/"merge"/"pr_created", timestamp:..., actor:..., message:..., durationToNextReview: "2h13m", ...}, ... ]
     * }
     */
    public Map<String, Object> buildTimeline(JsonNode jiraIssue, JsonNode prDetails) {
        Map<String, Object> out = new HashMap<>();

        // Basic issue info
        Map<String, Object> issueInfo = new HashMap<>();
        issueInfo.put("key", jiraIssue.path("key").asText(null));
        issueInfo.put("summary", jiraIssue.path("fields").path("summary").asText(null));
        out.put("issue", issueInfo);

        // PR metadata
        Map<String, Object> prMeta = new HashMap<>();
        if (prDetails != null) {
            prMeta.put("number", prDetails.path("number").asInt(-1));
            prMeta.put("title", prDetails.path("title").asText(null));
            prMeta.put("state", prDetails.path("state").asText(null));
            prMeta.put("created_at", prDetails.path("created_at").asText(null));
            prMeta.put("updated_at", prDetails.path("updated_at").asText(null));
            prMeta.put("merged_at", prDetails.path("merged_at").asText(null));
            prMeta.put("html_url", prDetails.path("html_url").asText(null));
        }
        out.put("pr", prMeta);

        // If prDetails is null, return partial response
        if (prDetails == null) {
            out.put("timeline", Collections.emptyList());
            return out;
        }

        // 1) fetch commits for this PR (commits_url)
        List<JsonNode> commits = fetchGithubList(prDetails.path("commits_url").asText());

        // 2) fetch reviews (PR reviews endpoint)
        String reviewsUrl = prDetails.path("url").asText() + "/reviews";
        List<JsonNode> reviews = fetchGithubList(reviewsUrl);

        // 3) collect commit events (with instantaneous timestamps)
        List<Event> commitEvents = commits.stream().map(c -> {
            String sha = c.path("sha").asText("");
            String msg = c.path("commit").path("message").asText("");
            String actor = c.path("commit").path("committer").path("name").asText(
                    c.path("author").path("login").asText("unknown"));
            String time = c.path("commit").path("committer").path("date").asText(null);
            return new Event("commit", parseInstant(time), actor, msg, time, sha, c.path("html_url").asText(null));
        }).filter(e -> e.timestamp != null).sorted(Comparator.comparing(e -> e.timestamp)).collect(Collectors.toList());

        // 4) collect review events
        List<Event> reviewEvents = reviews.stream().map(r -> {
            String reviewer = r.path("user").path("login").asText("unknown");
            String state = r.path("state").asText("COMMENTED");
            String submitted = r.path("submitted_at").asText(null);
            String body = r.path("body").asText("");
            return new Event("review", parseInstant(submitted), reviewer, state + (body.isEmpty() ? "" : " — " + truncate(body, 140)), submitted, null, null);
        }).filter(e -> e.timestamp != null).sorted(Comparator.comparing(e -> e.timestamp)).collect(Collectors.toList());

        // 5) pr creation event
        Event prCreated = null;
        Instant prCreatedAt = parseInstant(prDetails.path("created_at").asText(null));
        if (prCreatedAt != null) {
            prCreated = new Event("pr_created", prCreatedAt, prDetails.path("user").path("login").asText("unknown"), prDetails.path("title").asText(""), prDetails.path("created_at").asText(null), null, prDetails.path("html_url").asText(null));
        }

        // 6) merge event if any
        Event mergeEvent = null;
        Instant mergedAt = parseInstant(prDetails.path("merged_at").asText(null));
        if (mergedAt != null) {
            mergeEvent = new Event("merge", mergedAt, prDetails.path("merged_by").path("login").asText("unknown"), "Merged", prDetails.path("merged_at").asText(null), null, prDetails.path("html_url").asText(null));
        }

        // 7) Build combined chronological list
        List<Event> all = new ArrayList<>();
        if (prCreated != null) all.add(prCreated);
        all.addAll(commitEvents);
        all.addAll(reviewEvents);
        if (mergeEvent != null) all.add(mergeEvent);

        // sort by timestamp ascending
        all.sort(Comparator.comparing(e -> e.timestamp));

        // 8) Build timeline entries with durations between interesting pairs:
        // For each commit -> find next review after it (if any) and compute duration.
        // For each review -> find next commit after it (if any) and compute duration.
        List<Map<String, Object>> timeline = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            Event e = all.get(i);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", e.type);
            m.put("timestamp", e.iso);
            m.put("actor", e.actor);
            m.put("message", e.message);
            if (e.sha != null) m.put("sha", e.sha);
            if (e.url != null) m.put("url", e.url);

            // durations
            if ("commit".equals(e.type)) {
                Instant nextReview = findNextTimestamp(reviewEvents, e.timestamp);
                if (nextReview != null) {
                    m.put("time_to_next_review_seconds", Duration.between(e.timestamp, nextReview).getSeconds());
                    m.put("time_to_next_review_readable", humanReadable(Duration.between(e.timestamp, nextReview)));
                    m.put("next_review_at", nextReview.toString());
                }
            } else if ("review".equals(e.type)) {
                Instant nextCommit = findNextTimestamp(commitEvents, e.timestamp);
                if (nextCommit != null) {
                    m.put("time_to_next_commit_seconds", Duration.between(e.timestamp, nextCommit).getSeconds());
                    m.put("time_to_next_commit_readable", humanReadable(Duration.between(e.timestamp, nextCommit)));
                    m.put("next_commit_at", nextCommit.toString());
                }
            } else if ("merge".equals(e.type)) {
                // nothing special
            } else if ("pr_created".equals(e.type)) {
                // nothing special
            }

            timeline.add(m);
        }

        // 9) compute aggregated cycle summary (optional): number of reworks (=reviews that had commits after)
        int reworkCycles = 0;
        for (Event review : reviewEvents) {
            Instant nextCommit = findNextTimestamp(commitEvents, review.timestamp);
            if (nextCommit != null && nextCommit.isAfter(review.timestamp)) reworkCycles++;
        }

        out.put("reworkCycles", reworkCycles);
        out.put("timeline", timeline);

        return out;
    }

    // ---------------- Helper methods ----------------

    /**
     * Fetch a list resource from GitHub given a url (commits_url, reviews_url, etc).
     * Uses GitHub Accept header; no authentication here — consumer should call with tokens if needed.
     * If the provided prDetails contains "url" and you want to include auth you can modify this method
     * to accept token or header from caller.
     */
    private List<JsonNode> fetchGithubList(String url) {
        if (url == null || url.isBlank()) return Collections.emptyList();
        // some urls contain templated parts like {/sha} — remove them
        url = url.replace("{/sha}", "");
        url = url.replace("{/number}", "");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", GITHUB_ACCEPT);
        // No authorization here; typically caller (controller) called GitHubService.fetchPRDetails with token.
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            JsonNode root = mapper.readTree(resp.getBody());
            if (root.isArray()) {
                List<JsonNode> list = new ArrayList<>();
                root.forEach(list::add);
                return list;
            } else {
                // sometimes -> {"items":[...]} ; handle gracefully
                if (root.has("items") && root.get("items").isArray()) {
                    List<JsonNode> list = new ArrayList<>();
                    root.get("items").forEach(list::add);
                    return list;
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch/parset GitHub list from: " + url, e);
        }
    }

    private static String findFirstGithubPrUrl(String text) {
        if (text == null) return null;
        Matcher m = GITHUB_PR_PATTERN.matcher(text);
        if (m.find()) return m.group(1);
        return null;
    }

    private static String normalizePrUrl(String raw) {
        // Some JIRA links might be markdown or contain HTML; try to normalize to "https://github.com/{owner}/{repo}/pull/{num}"
        if (raw == null) return null;
        // remove trailing punctuation
        raw = raw.trim();
        if (raw.endsWith(")")) raw = raw.substring(0, raw.length()-1);
        return raw;
    }

    private static String basicAuthHeader(String email, String apiToken) {
        String combined = email + ":" + (apiToken == null ? "" : apiToken);
        return Base64.getEncoder().encodeToString(combined.getBytes());
    }

    private static Instant parseInstant(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            return Instant.parse(iso);
        } catch (DateTimeParseException ex) {
            // sometimes jira returns local times; try to parse loosely by removing milliseconds or appending Z
            try {
                if (!iso.endsWith("Z")) iso = iso + "Z";
                return Instant.parse(iso);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static String truncate(String s, int len) {
        if (s == null) return null;
        return s.length() <= len ? s : s.substring(0, len-1) + "…";
    }

    private static Instant findNextTimestamp(List<Event> events, Instant after) {
        return events.stream().map(ev -> ev.timestamp).filter(ts -> ts != null && ts.isAfter(after)).min(Comparator.naturalOrder()).orElse(null);
    }

    private static String humanReadable(Duration d) {
        if (d == null) return null;
        long days = d.toDays();
        long hours = d.minusDays(days).toHours();
        long minutes = d.minusDays(days).minusHours(hours).toMinutes();
        long seconds = d.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds();

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (sb.length() == 0) sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    // Simple Event model used internally
    private static class Event {
        String type;        // commit | review | merge | pr_created
        Instant timestamp;
        String actor;
        String message;
        String iso;
        String sha;
        String url;

        Event(String type, Instant timestamp, String actor, String message, String iso, String sha, String url) {
            this.type = type;
            this.timestamp = timestamp;
            this.actor = actor;
            this.message = message;
            this.iso = iso;
            this.sha = sha;
            this.url = url;
        }
    }
}
