import React from "react";

export default function Heatmap({ data }) {
  const months = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  ];

  const entries = Object.entries(data).sort();
  const maxValue = Math.max(...Object.values(data).map(v => v || 0), 1);

  return (
    <div style={styles.container}>
      <div style={styles.grid}>
        {entries.map(([key, value]) => {
          const [year, month] = key.split("-");
          const monthNum = parseInt(month);
          const level = Math.ceil((value / maxValue) * 4);
          const monthName = months[monthNum - 1];

          return (
            <div key={key} style={styles.item}>
              <div style={{
                ...styles.box,
                ...getHeatStyle(level)
              }}>
                <span style={styles.tooltip}>{monthName} {year}: {value}</span>
              </div>
              <div style={styles.label}>{monthName}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function getHeatStyle(level) {
  const heatStyles = {
    0: { background: "#e5e7eb" },
    1: { background: "#93c5fd" },
    2: { background: "#60a5fa" },
    3: { background: "#3b82f6" },
    4: { background: "#1e40af" }
  };
  return heatStyles[level] || heatStyles[0];
}

const styles = {
  container: {
    marginTop: 16,
    width: "100%",
  },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(60px, 1fr))",
    gap: 8,
  },
  item: {
    textAlign: "center",
  },
  box: {
    aspectRatio: "1",
    borderRadius: 8,
    cursor: "pointer",
    position: "relative",
    transition: "all 0.2s",
    border: "1px solid #d1d5db",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  label: {
    fontSize: 11,
    marginTop: 6,
    color: "#6b7280",
    fontWeight: 500,
  },
  tooltip: {
    visibility: "hidden",
    position: "absolute",
    zIndex: 100,
    background: "#1f2937",
    color: "white",
    textAlign: "center",
    padding: "6px 8px",
    borderRadius: 4,
    bottom: "125%",
    left: "50%",
    whiteSpace: "nowrap",
    transform: "translateX(-50%)",
    fontSize: 12,
    fontWeight: 500,
  },
};
