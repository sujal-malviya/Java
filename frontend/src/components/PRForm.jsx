export default function PRForm({
  repos,
  selectedRepo,
  setSelectedRepo,
  mode,
  setMode,
  onAnalyze,
  onToday
}) {
  return (
    <div className="form-box">
      <h2>GitHub PR Tracker</h2>

      {/* ✅ Repo Dropdown */}
      <select
        value={selectedRepo}
        onChange={(e) => setSelectedRepo(e.target.value)}
        required
      >
        {repos.map((repo) => (
          <option key={repo.id} value={repo.name}>
            {repo.name}
          </option>
        ))}
      </select>

      {/* ✅ Mode */}
      <select value={mode} onChange={(e) => setMode(e.target.value)}>
        <option value="daily">Daily</option>
        <option value="monthly">Monthly</option>
        <option value="yearly">Yearly</option>
      </select>

      <button onClick={onAnalyze}>Analyze</button>
      <button onClick={onToday}>Today</button>
    </div>
  );
}
