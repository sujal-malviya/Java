import axios from "axios";

export const fetchTodayPR = async (repo) => {
  const today = new Date().toISOString().slice(0, 10);

  return await axios.post("http://localhost:8080/api/pr/daily", {
    repository: repo,
    startDate: today,
    endDate: today
  });
};

export const fetchPRByMode = async (repo, mode) => {
  const today = new Date().toISOString().slice(0, 10);

  return await axios.post(`http://localhost:8080/api/pr/${mode}`, {
    repository: repo,
    startDate: today,
    endDate: today
  });
};
