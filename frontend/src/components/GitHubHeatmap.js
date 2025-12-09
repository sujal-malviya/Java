import React from "react";
import CalendarHeatmap from "react-calendar-heatmap";
import "react-calendar-heatmap/dist/styles.css";

export default function GitHubHeatmap({ data, title }) {
  return (
    <div style={{ marginTop: 40 }}>
      <h3>{title}</h3>

      <CalendarHeatmap
        startDate={new Date(data[0]?.date || new Date())}
        endDate={new Date()}
        values={data}
        classForValue={(value) => {
          if (!value || value.count === 0) return "color-empty";
          if (value.count < 2) return "color-github-1";
          if (value.count < 5) return "color-github-2";
          if (value.count < 10) return "color-github-3";
          return "color-github-4";
        }}
      />
    </div>
  );
}
