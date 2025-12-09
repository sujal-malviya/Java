import React from "react";

export default function AnalysisPanel({ analytics }) {
  if (!analytics || analytics.length === 0) {
    return <div style={styles.container}>No analytics data available</div>;
  }

  const stats = analytics[0];

  return (
    <div style={styles.container}>
      <h3 style={styles.title}>📊 Repository Analysis</h3>
      
      <div style={styles.grid}>
        <div style={styles.card}>
          <div style={styles.cardLabel}>Total PRs</div>
          <div style={styles.cardValue}>{stats.total || 0}</div>
        </div>

        <div style={styles.card}>
          <div style={styles.cardLabel}>Merged PRs</div>
          <div style={styles.cardValue}>{stats.merged || 0}</div>
        </div>

        <div style={styles.card}>
          <div style={styles.cardLabel}>Open PRs</div>
          <div style={styles.cardValue}>{stats.open || 0}</div>
        </div>

        <div style={styles.card}>
          <div style={styles.cardLabel}>Merge Rate</div>
          <div style={styles.cardValue}>
            {(stats.merge_rate || 0).toFixed(1)}%
          </div>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    marginTop: 24,
    padding: 20,
    background: "#ffffff",
    borderRadius: 12,
    border: "1px solid #e5e7eb",
    boxShadow: "0 1px 3px rgba(0,0,0,0.08)",
  },
  title: {
    margin: "0 0 16px 0",
    fontSize: 18,
    fontWeight: 600,
    color: "#1f2937",
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(140px, 1fr))",
    gap: 16,
    marginTop: 16,
  },
  card: {
    background: "#f9fafb",
    padding: 16,
    borderRadius: 8,
    textAlign: "center",
    border: "1px solid #e5e7eb",
    transition: "all 0.2s",
  },
  cardLabel: {
    fontSize: 12,
    color: "#6b7280",
    marginBottom: 8,
    fontWeight: 500,
  },
  cardValue: {
    fontSize: 28,
    fontWeight: "700",
    color: "#3b82f6",
  },
};
