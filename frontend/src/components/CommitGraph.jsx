// src/components/CommitGraph.jsx
import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ResponsiveContainer,
} from "recharts";

export default function CommitGraph({ labels = [], counts = [], total = 0 }) {
  const data = labels.map((label, index) => ({
    date: label,
    commits: counts[index] ?? 0,
  }));

  if (!data.length) {
    return (
      <div className="card">
        <h3>Commits over time</h3>
        <p style={{ padding: "8px 0" }}>No commits in this date range.</p>
      </div>
    );
  }

  return (
    <div className="card" style={{ padding: "16px", marginTop: "16px" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: 8,
        }}
      >
        <h3 style={{ margin: 0 }}>Commits over time</h3>
        <span style={{ fontSize: 12, opacity: 0.8 }}>
          Total commits: <strong>{total}</strong>
        </span>
      </div>

      <div style={{ width: "100%", height: 300 }}>
        <ResponsiveContainer>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis allowDecimals={false} />
            <Tooltip />
            <Line
              type="monotone"
              dataKey="commits"
              stroke="#2da44e" // GitHub green
              strokeWidth={2}
              dot={false}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
