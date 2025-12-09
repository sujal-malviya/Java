import React from "react";
import "../styles/heatmap.css";

export default function Heatmap({ data }) {
  const days = Object.keys(data).sort();

  return (
    <div className="heatmap-container">
      <div className="heatmap-grid">
        {days.map((day) => {
          const level = Math.min(data[day] || 0, 4);
          return (
            <div key={day} className={`heat-box level-${level}`}>
              <span className="tooltip">{day} : {data[day]}</span>
            </div>
          );
        })}
      </div>

      <div className="heatmap-legend">
        <span>Low</span>
        {[0, 1, 2, 3, 4].map((l) => (
          <div key={l} className={`heat-box level-${l}`} />
        ))}
        <span>High</span>
      </div>
    </div>
  );
}
