import { Bar } from "react-chartjs-2";

export default function PRCharts({ labels, counts, mode }) {
  const data = {
    labels,
    datasets: [
      {
        label: `${mode.toUpperCase()} Pull Requests`,
        data: counts,
        backgroundColor: "#4F46E5"
      }
    ]
  };

  return <Bar data={data} />;
}
