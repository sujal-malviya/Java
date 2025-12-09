import React, { useState, useEffect } from "react";
import "./App.css";
import {
  fetchLoggedUser,
  fetchUserRepos,
  fetchCommitsHeatmap,
  fetchPRHeatmap,
  fetchMergeHeatmap,
  getAnalytics,
} from "./api/githubApi";
import MonthYearPicker from "./components/MonthYearPicker";
import Heatmap from "./components/Heatmap";
import AnalysisPanel from "./components/AnalysisPanel";

export default function App() {
  const [user, setUser] = useState(null);
  const [repos, setRepos] = useState([]);
  const [selectedRepo, setSelectedRepo] = useState(null);
  const [startYear, setStartYear] = useState(new Date().getFullYear());
  const [endYear, setEndYear] = useState(new Date().getFullYear());
  const [commitHeatmap, setCommitHeatmap] = useState({});
  const [prHeatmap, setPRHeatmap] = useState({});
  const [mergeHeatmap, setMergeHeatmap] = useState({});
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [darkMode, setDarkMode] = useState(true);

  useEffect(() => {
    document.body.style.backgroundColor = darkMode ? "#020617" : "#F8FAFC";
    document.body.style.color = darkMode ? "#E5E7EB" : "#020617";
  }, [darkMode]);

  const handleGitHubLogin = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/github/auth/authorize");
      const data = await response.json();
      window.location.href = data.auth_url;
    } catch (error) {
      alert("Failed to start GitHub login: " + error.message);
    }
  };

  const handleFetchRepos = async () => {
    if (!localStorage.getItem("token")) {
      alert("Please login with GitHub first");
      return;
    }

    setLoading(true);
    try {
      const userResponse = await fetchLoggedUser();
      setUser(userResponse.data);
      
      const reposResponse = await fetchUserRepos();
      setRepos(reposResponse.data);
    } catch (error) {
      alert("Error fetching repos: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectRepo = async (repo) => {
    setSelectedRepo(repo);
    setLoading(true);

    try {
      const owner = repo.full_name.split("/")[0];
      const repoName = repo.name;

      const payload = {
        owner,
        repo: repoName,
        startYear,
        endYear,
      };

      const [commits, prs, merges] = await Promise.all([
        fetchCommitsHeatmap(payload),
        fetchPRHeatmap(payload),
        fetchMergeHeatmap(payload),
      ]);

      setCommitHeatmap(commits.data);
      setPRHeatmap(prs.data);
      setMergeHeatmap(merges.data);
      setAnalytics(null); // Reset analytics
    } catch (error) {
      alert("Error loading repository data: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAnalyze = async () => {
    if (!selectedRepo) {
      alert("Please select a repository first");
      return;
    }

    setLoading(true);
    try {
      const owner = selectedRepo.full_name.split("/")[0];
      const repoName = selectedRepo.name;

      const payload = {
        owner,
        repo: repoName,
        startYear,
        endYear,
      };

      const response = await getAnalytics(payload);
      setAnalytics(response.data);
    } catch (error) {
      alert("Error analyzing repository: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleYearChange = async (newStartYear, newEndYear) => {
    setStartYear(newStartYear);
    setEndYear(newEndYear);

    if (selectedRepo) {
      setLoading(true);
      try {
        const owner = selectedRepo.full_name.split("/")[0];
        const repoName = selectedRepo.name;

        const payload = {
          owner,
          repo: repoName,
          startYear: newStartYear,
          endYear: newEndYear,
        };

        const [commits, prs, merges] = await Promise.all([
          fetchCommitsHeatmap(payload),
          fetchPRHeatmap(payload),
          fetchMergeHeatmap(payload),
        ]);

        setCommitHeatmap(commits.data);
        setPRHeatmap(prs.data);
        setMergeHeatmap(merges.data);
      } catch (error) {
        console.error("Error loading data:", error.message);
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <div className={darkMode ? "dark-mode" : ""} style={styles.container}>
      <div style={styles.header}>
        <h1>🐙 GitHub Tracker</h1>
        <button
          onClick={() => setDarkMode(!darkMode)}
          style={styles.modeBtn}
        >
          {darkMode ? "☀️" : "🌙"}
        </button>
      </div>

      {/* GitHub Login */}
      {!user ? (
        <div style={styles.card}>
          <h2>🔗 Connect to GitHub</h2>
          <p>Login to track your commits, PRs, and merges</p>
          <button onClick={handleGitHubLogin} style={styles.btn}>
            Login with GitHub
          </button>
        </div>
      ) : (
        <>
          {/* User Profile */}
          <div style={styles.card}>
            <div style={styles.userProfile}>
              <img src={user.avatar_url} alt={user.login} style={styles.avatar} />
              <div>
                <h3>{user.name || user.login}</h3>
                <p>@{user.login}</p>
                <small>{user.public_repos} public repositories</small>
              </div>
            </div>
          </div>

          {/* Repository Selection */}
          {repos.length > 0 && !selectedRepo && (
            <div style={styles.card}>
              <h2>Select Repository</h2>
              <div style={styles.repoGrid}>
                {repos.map((repo) => (
                  <button
                    key={repo.name}
                    onClick={() => handleSelectRepo(repo)}
                    style={styles.repoBtn}
                  >
                    <div style={styles.repoName}>{repo.name}</div>
                    <div style={styles.repoDesc}>{repo.description || "No description"}</div>
                    <div style={styles.repoMeta}>
                      ⭐ {repo.stargazers_count} • {repo.language || "Unknown"}
                    </div>
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Repository View */}
          {selectedRepo && (
            <>
              <div style={styles.card}>
                <div style={styles.repoHeader}>
                  <div>
                    <h2>{selectedRepo.name}</h2>
                    <p style={styles.repoUrl}>{selectedRepo.html_url}</p>
                  </div>
                  <button
                    onClick={() => {
                      setSelectedRepo(null);
                      setCommitHeatmap({});
                      setPRHeatmap({});
                      setMergeHeatmap({});
                      setAnalytics(null);
                    }}
                    style={styles.backBtn}
                  >
                    ← Back to Repos
                  </button>
                </div>

                <div style={styles.controls}>
                  <MonthYearPicker
                    startYear={startYear}
                    setStartYear={(year) => handleYearChange(year, endYear)}
                    endYear={endYear}
                    setEndYear={(year) => handleYearChange(startYear, year)}
                    userCreatedYear={user.created_at ? new Date(user.created_at).getFullYear() : null}
                  />
                  <button
                    onClick={handleAnalyze}
                    disabled={loading}
                    style={styles.analyzeBtn}
                  >
                    {loading ? "Analyzing..." : "📈 Analyze"}
                  </button>
                </div>
              </div>

              {/* Heatmaps */}
              {Object.keys(commitHeatmap).length > 0 && (
                <div style={styles.card}>
                  <h3>📝 Commits by Month</h3>
                  <Heatmap data={commitHeatmap} />
                </div>
              )}

              {Object.keys(prHeatmap).length > 0 && (
                <div style={styles.card}>
                  <h3>🔄 Pull Requests by Month</h3>
                  <Heatmap data={prHeatmap} />
                </div>
              )}

              {Object.keys(mergeHeatmap).length > 0 && (
                <div style={styles.card}>
                  <h3>✅ Merged by Month</h3>
                  <Heatmap data={mergeHeatmap} />
                </div>
              )}

              {/* Analysis */}
              {analytics && <AnalysisPanel analytics={analytics} />}
            </>
          )}
        </>
      )}
    </div>
  );
}

const styles = {
  container: {
    minHeight: "100vh",
    background: "#f9fafb",
    padding: "20px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 30,
    paddingBottom: 20,
    borderBottom: "1px solid #e5e7eb",
  },
  modeBtn: {
    background: "transparent",
    border: "1px solid #d1d5db",
    padding: "8px 12px",
    borderRadius: 8,
    cursor: "pointer",
    fontSize: 18,
    color: "#6b7280",
  },
  card: {
    background: "white",
    borderRadius: 12,
    padding: 24,
    marginBottom: 20,
    boxShadow: "0 1px 3px rgba(0,0,0,0.08)",
    border: "1px solid #e5e7eb",
  },
  userProfile: {
    display: "flex",
    gap: 16,
    alignItems: "center",
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: "50%",
    border: "3px solid #e5e7eb",
    backgroundColor: "#f3f4f6",
  },
  btn: {
    padding: "10px 20px",
    background: "#3b82f6",
    color: "white",
    border: "none",
    borderRadius: 8,
    cursor: "pointer",
    fontSize: 15,
    fontWeight: 600,
    transition: "all 0.2s",
  },
  repoGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))",
    gap: 16,
    marginTop: 16,
  },
  repoBtn: {
    background: "#ffffff",
    border: "1px solid #e5e7eb",
    borderRadius: 8,
    padding: 16,
    textAlign: "left",
    cursor: "pointer",
    color: "#1f2937",
    transition: "all 0.2s",
  },
  repoName: {
    fontSize: 16,
    fontWeight: 600,
    marginBottom: 8,
    color: "#1e40af",
  },
  repoDesc: {
    fontSize: 13,
    color: "#6b7280",
    marginBottom: 8,
    minHeight: 40,
  },
  repoMeta: {
    fontSize: 12,
    color: "#9ca3af",
  },
  repoHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
  },
  repoUrl: {
    fontSize: 13,
    color: "#3b82f6",
    marginTop: 4,
    textDecoration: "none",
  },
  backBtn: {
    background: "#f3f4f6",
    border: "1px solid #d1d5db",
    color: "#1f2937",
    padding: "8px 16px",
    borderRadius: 6,
    cursor: "pointer",
    fontSize: 14,
    fontWeight: 600,
  },
  controls: {
    display: "flex",
    gap: 16,
    alignItems: "flex-end",
    marginTop: 16,
    paddingTop: 16,
    borderTop: "1px solid #e5e7eb",
    flexWrap: "wrap",
  },
  analyzeBtn: {
    background: "#10b981",
    color: "white",
    border: "none",
    padding: "10px 20px",
    borderRadius: 6,
    cursor: "pointer",
    fontWeight: 600,
    whiteSpace: "nowrap",
    transition: "all 0.2s",
  },
};
