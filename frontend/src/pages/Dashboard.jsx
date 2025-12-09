import { useState } from "react";
import PRForm from "../components/PRForm";
import PRChart from "../components/PRChart";
import FileTable from "../components/FileTable";
import { fetchPRData } from "../api/prApi";

export default function Dashboard() {
  const [result, setResult] = useState(null);

  const handleSubmit = async (payload) => {
    try {
      const response = await fetchPRData(payload);
      setResult(response.data);
    } catch (error) {
      alert("Error fetching PR data from backend");
    }
  };

  return (
    <div className="dashboard">
      <PRForm onSubmit={handleSubmit} />

      {result && (
        <>
          <div className="stats">
            <div>Total PRs: {result.totalPRs}</div>
            <div>Open PRs: {result.openPRs}</div>
            <div>Closed PRs: {result.closedPRs}</div>
            <div>Merged PRs: {result.mergedPRs}</div>
          </div>

          <PRChart data={result.prsPerDay} />
          <FileTable files={result.prFiles} />
        </>
      )}
    </div>
  );
}
