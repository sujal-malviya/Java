import axios from "axios";

const BASE = "http://localhost:8080/api/jira";

export const fetchTimeline = (payload) => {
  return axios.post(`${BASE}/timeline`, payload, {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
      "Content-Type": "application/json"
    }
  });
};

