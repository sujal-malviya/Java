import { useEffect, useState } from "react";
import axios from "axios";

import {
  fetchLoggedUser,
  fetchUserRepos,
  fetchCommits,
  uploadFile,
  fetchPRHeatmap,
  fetchMergeHeatmap,
} from "./api/githubApi";

import { fetchTimeline } from "./api/jiraApi";
import MonthYearPicker from "./components/MonthYearPicker";

import "./App.css";

function App() {
  // ------------------ STATE ------------------
  const [owner, setOwner] = useState("");
  const [repos, setRepos] = useState([]);
  const [selectedRepo, setSelectedRepo] = useState("");

  const [jiraKey, setJiraKey] = useState("");
  const [timeline, setTimeline] = useState(null);
  const [pipeline, setPipeline] = useState(null);
  const [loadingTimeline, setLoadingTimeline] = useState(false);

  const [commitHeatmap, setCommitHeatmap] = useState({});
  const [prHeatmap, setPrHeatmap] = useState({});
  const [mergeHeatmap, setMergeHeatmap] = useState({});
  const [commitList, setCommitList] = useState([]);

  const [selectedFile, setSelectedFile] = useState(null);
  const [uploadMessage, setUploadMessage] = useState("Upload via dashboard");

  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [userCreatedAt, setUserCreatedAt] = useState("");

  const [actionType, setActionType] = useState("commits");
  const [darkMode, setDarkMode] = useState(true);

  // ------------------ DARK / LIGHT MODE ------------------
  useEffect(() => {
    document.body.style.backgroundColor = darkMode ? "#020617" : "#F8FAFC";
    document.body.style.color = darkMode ? "#E5E7EB" : "#020617";
  }, [darkMode]);

  // ------------------ HANDLE LOGIN TOKEN ------------------
  useEffect(() => {
    const token = new URLSearchParams(window.location.search).get("token");
    if (token) {
      localStorage.setItem("token", token);
      window.history.replaceState({}, document.title, "/");
    }
  }, []);

  // ------------------ LOAD USER + REPOS ------------------
  useEffect(() => {
    if (!localStorage.getItem("token")) return;

    fetchLoggedUser().then((res) => {
      setOwner(res.data.login);
      setUserCreatedAt(res.data.created_at);
    });

    fetchUserRepos().then((res) => {
      setRepos(res.data);
      if (res.data.length > 0) setSelectedRepo(res.data[0].name);
    });
  }, []);

  // ------------------ TIMELINE LOADER ------------------
  const loadTimeline = async () => {
    if (!jiraKey) return alert("Enter Jira Key (SAM-23)");

    setLoadingTimeline(true);
    try {
      const res = await fetchTimeline({ key: jiraKey });
      setTimeline(res.data);
    } catch {
      alert("Could not load timeline!");
    }
    setLoadingTimeline(false);
  };

  // ------------------ PIPELINE LOADER ------------------
  const loadPipeline = async () => {
    if (!jiraKey) return alert("Enter Jira Key");

    try {
      const res = await axios.get(
        `http://localhost:8080/api/jira/pipeline/${jiraKey}`,
        { headers: { Authorization: "Bearer " + localStorage.getItem("token") } }
      );

      setPipeline(res.data);
    } catch (err) {
      alert("Pipeline not found for this issue.");
    }
  };

  // ------------------ HEATMAP: COMMITS ------------------
  const buildCommitHeatmap = async (payload) => {
    const commitRes = await fetchCommits(payload);

    const map = {};
    const list = [];

    commitRes.data.forEach((commit) => {
      const date = commit.commit.committer.date.substring(0, 10);
      map[date] = (map[date] || 0) + 1;

      list.push({
        message: commit.commit.message,
        author: commit.commit.author.name,
        time: commit.commit.author.date,
        url: commit.html_url,
      });
    });

    setCommitHeatmap(map);
    setCommitList(list.reverse());
  };

  // ------------------ HEATMAP: PRS ------------------
  const buildPRHeatmap = async (payload) => {
    const res = await fetchPRHeatmap(payload);
    setPrHeatmap(res.data);
  };

  // ------------------ HEATMAP: MERGED PRS ------------------
  const buildMergeHeatmap = async (payload) => {
    const res = await fetchMergeHeatmap(payload);
    setMergeHeatmap(res.data);
  };

  // ------------------ ANALYZE BUTTON ------------------
  const fetchData = async () => {
    if (!owner || !selectedRepo) return alert("Owner or Repo missing!");

    const payload = { owner, repo: selectedRepo, startDate, endDate };

    if (actionType === "commits") await buildCommitHeatmap(payload);
    if (actionType === "prs") await buildPRHeatmap(payload);
    if (actionType === "merge") await buildMergeHeatmap(payload);

    if (actionType === "clone") {
      window.open(`https://github.com/${owner}/${selectedRepo}.git`, "_blank");
    }
  };

  // ------------------ UPLOAD FILE ------------------
  const handleUpload = async () => {
    if (!selectedFile) return alert("Select a file first");

    const reader = new FileReader();
    reader.onload = async () => {
      await uploadFile({
        owner,
        repo: selectedRepo,
        path: "uploads/" + selectedFile.name,
        content: reader.result.split(",")[1],
        message: uploadMessage,
      });

      alert("Uploaded successfully!");
      fetchData();
    };

    reader.readAsDataURL(selectedFile);
  };

  // ------------------ HEATMAP RENDER ------------------
  const renderHeatmap = () => {
    const data =
      actionType === "commits"
        ? commitHeatmap
        : actionType === "prs"
        ? prHeatmap
        : actionType === "merge"
        ? mergeHeatmap
        : {};

    const days = Object.keys(data).sort();

    return (
      <>
        <div className="heatmap-grid">
          {days.map((date) => {
            const level = Math.min(data[date], 4);
            return (
              <div key={date} className={`heat-box level-${level}`}>
                <span className="tooltip">
                  {date} : {data[date]}
                </span>
              </div>
            );
          })}
        </div>

        <div className="heatmap-legend">
          <span>Low</span>
          <div className="heat-box level-0"></div>
          <div className="heat-box level-1"></div>
          <div className="heat-box level-2"></div>
          <div className="heat-box level-3"></div>
          <div className="heat-box level-4"></div>
          <span>High</span>
        </div>
      </>
    );
  };

  // ------------------ UI ------------------
  return (
    <div className="container">
      {/* NAVBAR */}
      <div className="topbar flex-between">
        <h2>🚀 GitHub Analytics</h2>

        <div className="flex" style={{ gap: 10 }}>
          <button onClick={() => setDarkMode(!darkMode)}>
            {darkMode ? "☀ Light Mode" : "🌙 Dark Mode"}
          </button>

          <button
            onClick={() =>
              window.open(`https://github.com/${owner}/${selectedRepo}.git`)
            }
          >
            ⬇ Clone Repo
          </button>

          <button onClick={handleUpload}>⬆ Upload</button>
        </div>
      </div>

      {/* LOGIN BUTTON */}
      {!owner && (
        <button
          onClick={() =>
            (window.location.href = "http://localhost:8080/auth/github")
          }
        >
          Login with GitHub
        </button>
      )}

      {/* MAIN CONTENT */}
      {owner && (
        <>
          {/* FORM AREA */}
          <div className="form-area">
            <select onChange={(e) => setSelectedRepo(e.target.value)}>
              {repos.map((repo) => (
                <option key={repo.id} value={repo.name}>
                  {repo.name}
                </option>
              ))}
            </select>

            <MonthYearPicker
              userCreatedAt={userCreatedAt}
              onChange={({ startDate, endDate }) => {
                setStartDate(startDate);
                setEndDate(endDate);
              }}
            />

            <select onChange={(e) => setActionType(e.target.value)}>
              <option value="commits">Commits</option>
              <option value="prs">Pull Requests</option>
              <option value="merge">Merged PRs</option>
              <option value="clone">Clone Repo</option>
            </select>

            <button onClick={fetchData}>Analyze</button>
          </div>

          {/* HEATMAP */}
          <div className="heatmap-container card">
            <h3 className="center">Heatmap</h3>
            {renderHeatmap()}
          </div>

          {/* TIMELINE FORM */}
          <div className="timeline-form">
            <input
              type="text"
              placeholder="Enter Jira Key (SAM-23)"
              value={jiraKey}
              onChange={(e) => setJiraKey(e.target.value)}
            />
            <button onClick={loadTimeline}>Load Timeline</button>
            <button onClick={loadPipeline}>Load Pipeline</button>
          </div>

          {/* TIMELINE BOX */}
          {timeline && (
            <div className="timeline-box card">
              <h3>📌 Jira + GitHub PR Timeline</h3>

              <p>
                <b>Issue:</b> {timeline.issue.key} — {timeline.issue.summary}
              </p>

              <p>
                <b>PR:</b> #{timeline.pr.number} — {timeline.pr.title} (
                <a href={timeline.pr.html_url}>Open PR</a>)
              </p>

              <h4>🔽 Timeline Events</h4>

              {timeline.timeline.map((ev, idx) => (
                <div key={idx} className="timeline-event">
                  <p>
                    <b>{ev.type.toUpperCase()}</b> — {ev.timestamp}
                    <br />
                    👤 {ev.actor}
                    <br />
                    📝 {ev.message}
                  </p>

                  {ev.time_to_next_review_readable && (
                    <p>
                      ⏳ Next Review: {ev.time_to_next_review_readable}
                    </p>
                  )}

                  {ev.time_to_next_commit_readable && (
                    <p>
                      ⏳ Next Commit: {ev.time_to_next_commit_readable}
                    </p>
                  )}
                </div>
              ))}

              <h4>🔁 Rework Cycles: {timeline.reworkCycles}</h4>
            </div>
          )}

          {/* PIPELINE BOX (AFTER TIMELINE) */}
          {pipeline && (
            <div className="pipeline-box card">
              <h3>🚀 Deployment Pipeline</h3>

              {pipeline.pipeline.map((stage, idx) => (
                <div key={idx} className="pipeline-stage">
                  <b>{stage.stage}</b> — {stage.timestamp}
                  <br />
                  👤 {stage.actor}
                  <br />
                  📝 {stage.details}
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default App;
