import React from "react";

export default function MonthYearPicker({
  startYear,
  setStartYear,
  endYear,
  setEndYear,
  userCreatedYear,
}) {
  const currentYear = new Date().getFullYear();
  const minYear = userCreatedYear || 2015;
  const maxYear = currentYear;

  const years = [];
  for (let y = minYear; y <= maxYear; y++) {
    years.push(y);
  }

  return (
    <div style={styles.container}>
      <div style={styles.group}>
        <label style={styles.label}>From Year</label>
        <select
          value={startYear}
          onChange={(e) => setStartYear(parseInt(e.target.value))}
          style={styles.select}
        >
          {years.map((year) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
      </div>

      <div style={styles.group}>
        <label style={styles.label}>To Year</label>
        <select
          value={endYear}
          onChange={(e) => setEndYear(parseInt(e.target.value))}
          style={styles.select}
        >
          {years.map((year) => (
            <option key={year} value={year}>
              {year}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}

const styles = {
  container: {
    display: "flex",
    gap: 16,
    alignItems: "flex-end",
    flexWrap: "wrap",
  },
  group: {
    display: "flex",
    flexDirection: "column",
    gap: 8,
  },
  label: {
    fontSize: 14,
    fontWeight: 600,
    color: "#374151",
  },
  select: {
    padding: "8px 12px",
    borderRadius: 6,
    border: "1px solid #d1d5db",
    background: "#ffffff",
    color: "#1f2937",
    fontSize: 14,
    cursor: "pointer",
    fontFamily: "inherit",
    transition: "border-color 0.2s",
  },
};
