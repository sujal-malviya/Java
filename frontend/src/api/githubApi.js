// src/api/githubApi.js
import axios from "axios";

const BASE = "http://localhost:8080/api/github";

const getAuthHeader = () => ({
  headers: {
    Authorization: "Bearer " + localStorage.getItem("token"),
    "Content-Type": "application/json",
  },
});

// ---------------- GitHub APIs ----------------

export const fetchLoggedUser = () =>
  axios.get(`${BASE}/me`, getAuthHeader());

export const fetchUserRepos = () =>
  axios.get(`${BASE}/repos`, getAuthHeader());

export const fetchCommits = (payload) =>
  axios.post(`${BASE}/commits`, payload, getAuthHeader());

export const uploadFile = (payload) =>
  axios.post(`${BASE}/upload`, payload, getAuthHeader());

// PR Status Analytics
export const fetchPRStatus = (payload) =>
  axios.post(`${BASE}/prs/status`, payload, getAuthHeader());

// PR Heatmap
export const fetchPRHeatmap = (payload) =>
  axios.post(`${BASE}/prs/heatmap`, payload, getAuthHeader());

// Merge Heatmap
export const fetchMergeHeatmap = (payload) =>
  axios.post(`${BASE}/merge/heatmap`, payload, getAuthHeader());

// Live commits
export const fetchLiveCommits = (payload) =>
  axios.post(`${BASE}/live/commits`, payload, getAuthHeader());
