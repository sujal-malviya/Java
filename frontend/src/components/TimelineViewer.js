// src/components/TimelineViewer.js
import React from "react";
import TimelineChart from "./TimelineChart";

/**
 * Props:
 *  - timeline: object returned from backend: { issue, pr, timeline: [...], reworkCycles }
 */
export default function TimelineViewer({ timeline }) {
  if (!timeline) return null;

  const events = timeline.timeline || [];

  return (
    <div style={styles.box}>
      <div style={styles.header}>
        <div>
          <h3 style={{ margin: 0 }}>{timeline.issue?.key} — {timeline.issue?.summary}</h3>
          <div style={{ fontSize: 13, opacity: 0.8 }}>
            PR #{timeline.pr?.number} • {timeline.pr?.title} • {timeline.pr?.state}
            {timeline.pr?.html_url && (
              <a href={timeline.pr.html_url} target="_blank" rel="noreferrer" style={{ marginLeft: 12 }}>
                Open PR ↗
              </a>
            )}
          </div>
        </div>

        <div style={{ textAlign: "right" }}>
          <div style={{ fontSize: 12, opacity: 0.8 }}>Rework cycles: <b>{timeline.reworkCycles ?? 0}</b></div>
          <div style={{ fontSize: 12, opacity: 0.8 }}>Events: <b>{events.length}</b></div>
        </div>
      </div>

      <TimelineChart events={events} />

      <div style={{ marginTop: 14 }}>
        <h4 style={{ margin: "8px 0" }}>Event details</h4>
        <div style={styles.grid}>
          {events.map((ev, i) => (
            <div key={i} style={styles.card}>
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                <div>
                  <b style={{ textTransform: "capitalize" }}>{ev.type}</b>
                  <div style={{ fontSize: 12, opacity: 0.8 }}>{ev.actor}</div>
                </div>
                <div style={{ textAlign: "right" }}>
                  <div style={{ fontSize: 12 }}>{new Date(ev.timestamp).toLocaleString()}</div>
                  {ev.time_to_next_review_readable && <div style={{ fontSize: 11, opacity: 0.8 }}>to next review: {ev.time_to_next_review_readable}</div>}
                  {ev.time_to_next_commit_readable && <div style={{ fontSize: 11, opacity: 0.8 }}>to next commit: {ev.time_to_next_commit_readable}</div>}
                </div>
              </div>

              <div style={{ marginTop: 8, fontSize: 13, color: "#e6edf3" }}>
                {ev.message ? ev.message : <i style={{ opacity: 0.7 }}>No message</i>}
              </div>

              {ev.url && (
                <a href={ev.url} target="_blank" rel="noreferrer" style={{ marginTop: 8, display: "inline-block" }}>
                  View on GitHub ↗
                </a>
              )}
            </div>
          ))}
        </div>
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
