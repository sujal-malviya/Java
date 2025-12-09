import React from "react";

export default function TimelineChart({ events = [] }) {
  if (!events || events.length === 0) {
    return <div style={{ textAlign: "center", padding: 20 }}>No timeline events to show</div>;
  }

  const parsed = events
    .map(e => ({ ...e, epoch: new Date(e.timestamp).getTime() }))
    .filter(e => !Number.isNaN(e.epoch))
    .sort((a, b) => a.epoch - b.epoch);

  const start = parsed[0].epoch;
  const end = parsed[parsed.length - 1].epoch;
  const span = Math.max(1, end - start);

  const colorForType = (t) => {
    if (t === "commit") return "#16A34A";
    if (t === "review" || t === "reviewed") return "#F59E0B";
    if (t === "merge") return "#0EA5E9";
    if (t === "pr_created") return "#A78BFA";
    return "#94A3B8";
  };

  return (
    <div style={{ padding: 12 }}>
      <div style={{ height: 72, position: "relative", marginBottom: 12 }}>
        <div style={{
          position: "absolute",
          left: 16,
          right: 16,
          top: "50%",
          height: 2,
          background: "rgba(255,255,255,0.06)",
          transform: "translateY(-50%)"
        }} />

        {parsed.map((evt, idx) => {
          const leftPct = ((evt.epoch - start) / span) * 100;
          return (
            <div
              key={idx}
              style={{
                position: "absolute",
                left: leftPct + "%",
                top: "50%",
                transform: "translate(-50%, -50%)",
                width: 12,
                height: 12,
                borderRadius: "50%",
                background: colorForType(evt.type),
                cursor: "pointer",
              }}
              title={`${evt.type}: ${evt.actor} - ${evt.message}`}
            />
          );
        })}
      </div>

      <div style={{ display: "flex", justifyContent: "center", gap: 12, marginTop: 6 }}>
        <LegendDot color="#A78BFA" label="PR created" />
        <LegendDot color="#16A34A" label="Commit" />
        <LegendDot color="#F59E0B" label="Review" />
        <LegendDot color="#0EA5E9" label="Merge" />
      </div>
    </div>
  );
}

function LegendDot({ color, label }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
      <div style={{
        width: 12, height: 12, borderRadius: 6, background: color
      }} />
      <small style={{ opacity: 0.9 }}>{label}</small>
    </div>
  );
}
