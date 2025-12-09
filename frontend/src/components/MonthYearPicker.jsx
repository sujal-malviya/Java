import { useState, useEffect } from "react";

const MONTHS = [
  { label: "January", value: "01" },
  { label: "February", value: "02" },
  { label: "March", value: "03" },
  { label: "April", value: "04" },
  { label: "May", value: "05" },
  { label: "June", value: "06" },
  { label: "July", value: "07" },
  { label: "August", value: "08" },
  { label: "September", value: "09" },
  { label: "October", value: "10" },
  { label: "November", value: "11" },
  { label: "December", value: "12" }
];

export default function MonthYearPicker({ userCreatedAt, onChange }) {
  const [month, setMonth] = useState("01");
  const [year, setYear] = useState(new Date().getFullYear());
  const [years, setYears] = useState([]);

  // ✅ Build year list from GitHub account creation → current year
  useEffect(() => {
    if (!userCreatedAt) return;

    const startYear = new Date(userCreatedAt).getFullYear();
    const currentYear = new Date().getFullYear();

    const list = [];
    for (let y = startYear; y <= currentYear; y++) {
      list.push(y);
    }
    setYears(list);
  }, [userCreatedAt]);

  // ✅ Whenever month/year changes → compute startDate & endDate
  useEffect(() => {
    const startDate = `${year}-${month}-01`;

    // last day of month
    const endDate = new Date(year, Number(month), 0)
      .toISOString()
      .slice(0, 10);

    onChange({ startDate, endDate, month, year });
  }, [month, year, onChange]);

  return (
    <div style={styles.container}>
      <select value={month} onChange={(e) => setMonth(e.target.value)}>
        {MONTHS.map((m) => (
          <option key={m.value} value={m.value}>
            {m.label}
          </option>
        ))}
      </select>

      <select value={year} onChange={(e) => setYear(e.target.value)}>
        {years.map((y) => (
          <option key={y} value={y}>
            {y}
          </option>
        ))}
      </select>
    </div>
  );
}

const styles = {
  container: {
    display: "flex",
    gap: "12px"
  }
};
