export default function FileTable({ files }) {
  return (
    <div className="table-box">
      <h3>Changed Files</h3>
      <table>
        <thead>
          <tr>
            <th>File Name</th>
            <th>Status</th>
            <th>Additions</th>
            <th>Deletions</th>
          </tr>
        </thead>
        <tbody>
          {files.map((file, index) => (
            <tr key={index}>
              <td>{file.filename}</td>
              <td>{file.status}</td>
              <td>{file.additions}</td>
              <td>{file.deletions}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
