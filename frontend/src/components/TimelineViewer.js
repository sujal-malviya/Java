import React from "react";

export default function TimelineViewer({ timeline }) {
  if (!timeline) return <div>No timeline data</div>;

  const events = timeline.timeline || [];

  return (
    <div style={styles.box}>
      <div style={styles.header}>
        <h3>{timeline.issue?.key}: {timeline.issue?.summary}</h3>
        <a href={timeline.pr?.html_url} target="_blank" rel="noopener noreferrer">
          PR #{timeline.pr?.number}
        </a>
      </div>

      <div style={styles.grid}>
        {events.map((evt, idx) => (
          <div key={idx} style={styles.card}>
            <strong>{evt.type}</strong>
            <p>{evt.message}</p>
            <small>{evt.actor} - {evt.timestamp}</small>
            {evt.time_to_next_review_readable && (
              <p style={{ color: "#F59E0B" }}>⏱️ {evt.time_to_next_review_readable} to review</p>
            )}
            {evt.time_to_next_commit_readable && (
              <p style={{ color: "#16A34A" }}>⏱️ {evt.time_to_next_commit_readable} to next commit</p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

const styles = {
  box: {
    width: "90%",
    margin: "20px auto",
    padding: 18,
    borderRadius: 12,
    background: "rgba(255,255,255,0.04)",
    boxShadow: "0 8px 30px rgba(2,6,23,0.6)",
  },
  header: { display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 8 },
  grid: { display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))", gap: 12, marginTop: 12 },
  card: { padding: 12, borderRadius: 8, background: "rgba(255,255,255,0.02)", border: "1px solid rgba(255,255,255,0.04)" }
};
