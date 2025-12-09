import axios from "axios";

const BASE = "http://localhost:8080/api/github";

const getAuthHeader = () => ({
  headers: {
    Authorization: "Bearer " + localStorage.getItem("token"),
    "Content-Type": "application/json",
  },
});

// GitHub APIs
export const fetchLoggedUser = () =>
  axios.get(`${BASE}/me`, getAuthHeader());

export const fetchUserRepos = () =>
  axios.get(`${BASE}/repos`, getAuthHeader());

export const fetchCommitsHeatmap = (payload) =>
  axios.post(`${BASE}/commits/heatmap`, payload, getAuthHeader());

export const fetchPRHeatmap = (payload) =>
  axios.post(`${BASE}/prs/heatmap`, payload, getAuthHeader());

export const fetchMergeHeatmap = (payload) =>
  axios.post(`${BASE}/merge/heatmap`, payload, getAuthHeader());

export const getRepoInfo = (owner, repo) =>
  axios.get(`${BASE}/repo-info/${owner}/${repo}`, getAuthHeader());

export const getAnalytics = (payload) =>
  axios.post(`${BASE}/analyze`, payload, getAuthHeader());
