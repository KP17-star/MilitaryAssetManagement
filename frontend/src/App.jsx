import { useEffect, useState } from "react";
import "./App.css";

const API = "http://localhost:8080";

function App() {
  const [page, setPage] = useState("dashboard");

  const [dashboard, setDashboard] = useState(null);
  const [assets, setAssets] = useState([]);
  const [purchases, setPurchases] = useState([]);
  const [transfers, setTransfers] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);

  const [bases, setBases] = useState([]);
  const [equipmentTypes, setEquipmentTypes] = useState([]);

  const [date, setDate] = useState("");
  const [baseId, setBaseId] = useState("");
  const [equipmentTypeId, setEquipmentTypeId] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError("");

      let url = `${API}/api/dashboard`;

      const params = new URLSearchParams();

      if (date) {
        params.append("date", date);
      }

      if (baseId) {
        params.append("baseId", baseId);
      }

      if (equipmentTypeId) {
        params.append("equipmentTypeId", equipmentTypeId);
      }

      if (params.toString()) {
        url += `?${params.toString()}`;
      }

      const response = await fetch(url);

      if (!response.ok) {
        throw new Error("Failed to load dashboard");
      }

      const data = await response.json();
      setDashboard(data);
    } catch (err) {
      setError(
        "Unable to load dashboard. Make sure the Spring Boot backend is running on http://localhost:8080"
      );
    } finally {
      setLoading(false);
    }
  };

  const loadData = async () => {
    try {
      setError("");

      const [
        assetsRes,
        purchasesRes,
        transfersRes,
        assignmentsRes,
        auditRes,
        basesRes,
        equipmentRes,
      ] = await Promise.all([
        fetch(`${API}/api/assets`),
        fetch(`${API}/api/purchases`),
        fetch(`${API}/api/transfers`),
        fetch(`${API}/api/assignments`),
        fetch(`${API}/api/audit-logs`),
        fetch(`${API}/api/bases`),
        fetch(`${API}/api/equipment-types`),
      ]);

      if (
        !assetsRes.ok ||
        !purchasesRes.ok ||
        !transfersRes.ok ||
        !assignmentsRes.ok ||
        !auditRes.ok ||
        !basesRes.ok ||
        !equipmentRes.ok
      ) {
        throw new Error("API request failed");
      }

      setAssets(await assetsRes.json());
      setPurchases(await purchasesRes.json());
      setTransfers(await transfersRes.json());
      setAssignments(await assignmentsRes.json());
      setAuditLogs(await auditRes.json());
      setBases(await basesRes.json());
      setEquipmentTypes(await equipmentRes.json());
    } catch (err) {
      setError(
        "Unable to load data. Make sure the Spring Boot backend is running on http://localhost:8080"
      );
    }
  };

  useEffect(() => {
    loadData();
    loadDashboard();
  }, []);

  const applyFilters = () => {
    loadDashboard();
  };

  const formatDate = (value) => {
    if (!value) return "-";

    try {
      return new Date(value).toLocaleDateString();
    } catch {
      return value;
    }
  };

  const renderDashboard = () => {
    if (loading && !dashboard) {
      return <div className="loading">Loading dashboard...</div>;
    }

    if (!dashboard) {
      return null;
    }

    return (
      <>
        <div className="page-title">
          <h2>Dashboard</h2>
          <p>
            Overview of military assets, inventory movements and expenditure
          </p>
        </div>

        <div className="filter-card">
          <div className="filters">
            <div className="field">
              <label>Date</label>
              <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
              />
            </div>

            <div className="field">
              <label>Base</label>
              <select
                value={baseId}
                onChange={(e) => setBaseId(e.target.value)}
              >
                <option value="">All Bases</option>

                {bases.map((base) => (
                  <option key={base.id} value={base.id}>
                    {base.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="field">
              <label>Equipment Type</label>
              <select
                value={equipmentTypeId}
                onChange={(e) => setEquipmentTypeId(e.target.value)}
              >
                <option value="">All Equipment</option>

                {equipmentTypes.map((type) => (
                  <option key={type.id} value={type.id}>
                    {type.name}
                  </option>
                ))}
              </select>
            </div>

            <button className="primary-btn" onClick={applyFilters}>
              Apply Filters
            </button>
          </div>
        </div>

        <div className="metrics">
          <div className="metric-card">
            <h3>Opening Balance</h3>
            <div className="value">
              {dashboard.openingBalance ?? 0}
            </div>
          </div>

          <div className="metric-card">
            <h3>Purchases</h3>
            <div className="value">
              {dashboard.purchases ?? 0}
            </div>
          </div>

          <div className="metric-card">
            <h3>Transfer In</h3>
            <div className="value">
              {dashboard.transferIn ?? 0}
            </div>
          </div>

          <div className="metric-card">
            <h3>Transfer Out</h3>
            <div className="value">
              {dashboard.transferOut ?? 0}
            </div>
          </div>

          <div className="metric-card">
            <h3>Expenditure</h3>
            <div className="value">
              {dashboard.expenditure ?? 0}
            </div>
          </div>

          <div className="metric-card highlight">
            <h3>Closing Balance</h3>
            <div className="value">
              {dashboard.closingBalance ?? 0}
            </div>
          </div>
        </div>

        <div className="summary">
          <h3>Movement Summary</h3>

          <div className="summary-row">
            <span>Opening Balance</span>
            <strong>{dashboard.openingBalance ?? 0}</strong>
          </div>

          <div className="summary-row">
            <span>Purchases</span>
            <strong>+{dashboard.purchases ?? 0}</strong>
          </div>

          <div className="summary-row">
            <span>Transfer In</span>
            <strong>+{dashboard.transferIn ?? 0}</strong>
          </div>

          <div className="summary-row">
            <span>Transfer Out</span>
            <strong>-{dashboard.transferOut ?? 0}</strong>
          </div>

          <div className="summary-row">
            <span>Expenditure</span>
            <strong>-{dashboard.expenditure ?? 0}</strong>
          </div>

          <div className="summary-row">
            <span>Net Movement</span>
            <strong>{dashboard.netMovement ?? 0}</strong>
          </div>

          <div className="summary-row">
            <span>Closing Balance</span>
            <strong>{dashboard.closingBalance ?? 0}</strong>
          </div>
        </div>
      </>
    );
  };

  const renderAssets = () => (
    <>
      <div className="page-title">
        <h2>Assets</h2>
        <p>View all military assets</p>
      </div>

      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Asset Code</th>
              <th>Asset Name</th>
              <th>Equipment Type</th>
              <th>Base</th>
              <th>Quantity</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {assets.map((asset) => (
              <tr key={asset.id}>
                <td>{asset.id}</td>
                <td>{asset.assetCode}</td>
                <td>{asset.assetName}</td>
                <td>{asset.equipmentType?.name}</td>
                <td>{asset.base?.name}</td>
                <td>{asset.quantity}</td>
                <td>{asset.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  const renderPurchases = () => (
    <>
      <div className="page-title">
        <h2>Purchases</h2>
        <p>Purchase history</p>
      </div>

      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Base</th>
              <th>Equipment Type</th>
              <th>Quantity</th>
              <th>Purchase Date</th>
              <th>Created By</th>
            </tr>
          </thead>

          <tbody>
            {purchases.map((purchase) => (
              <tr key={purchase.id}>
                <td>{purchase.id}</td>
                <td>{purchase.base?.name}</td>
                <td>{purchase.equipmentType?.name}</td>
                <td>{purchase.quantity}</td>
                <td>{formatDate(purchase.purchaseDate)}</td>
                <td>{purchase.createdBy}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  const renderTransfers = () => (
    <>
      <div className="page-title">
        <h2>Transfers</h2>
        <p>Asset transfer history</p>
      </div>

      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Asset</th>
              <th>From Base</th>
              <th>To Base</th>
              <th>Quantity</th>
              <th>Date</th>
              <th>Created By</th>
            </tr>
          </thead>

          <tbody>
            {transfers.map((transfer) => (
              <tr key={transfer.id}>
                <td>{transfer.id}</td>
                <td>{transfer.asset?.assetCode}</td>
                <td>{transfer.fromBase?.name}</td>
                <td>{transfer.toBase?.name}</td>
                <td>{transfer.quantity}</td>
                <td>{formatDate(transfer.transferDate)}</td>
                <td>{transfer.createdBy}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  const renderAssignments = () => (
    <>
      <div className="page-title">
        <h2>Assignments</h2>
        <p>Asset assignment and expenditure records</p>
      </div>

      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Asset</th>
              <th>Personnel</th>
              <th>Quantity</th>
              <th>Assigned Date</th>
              <th>Status</th>
              <th>Created By</th>
            </tr>
          </thead>

          <tbody>
            {assignments.map((assignment) => (
              <tr key={assignment.id}>
                <td>{assignment.id}</td>
                <td>{assignment.asset?.assetCode}</td>
                <td>{assignment.personnelName}</td>
                <td>{assignment.quantity}</td>
                <td>{formatDate(assignment.assignedDate)}</td>
                <td>{assignment.status}</td>
                <td>{assignment.createdBy}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  const renderAuditLogs = () => (
    <>
      <div className="page-title">
        <h2>Audit Logs</h2>
        <p>System activity history</p>
      </div>

      <div className="table-card">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>User</th>
              <th>Action</th>
              <th>Entity</th>
              <th>Entity ID</th>
              <th>Description</th>
              <th>Timestamp</th>
            </tr>
          </thead>

          <tbody>
            {auditLogs.map((log) => (
              <tr key={log.id}>
                <td>{log.id}</td>
                <td>{log.username}</td>
                <td>{log.action}</td>
                <td>{log.entityType}</td>
                <td>{log.entityId}</td>
                <td>{log.description}</td>
                <td>{formatDate(log.timestamp)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  const renderPage = () => {
    switch (page) {
      case "assets":
        return renderAssets();

      case "purchases":
        return renderPurchases();

      case "transfers":
        return renderTransfers();

      case "assignments":
        return renderAssignments();

      case "audit":
        return renderAuditLogs();

      default:
        return renderDashboard();
    }
  };

  return (
    <div className="app">

      <header className="header">
        <div>
          <h1>Military Asset Management</h1>
          <p>Asset Management System</p>
        </div>

        <div className="user-area">
          <span>ADMIN</span>
          <div className="avatar">A</div>
        </div>
      </header>

      <nav className="navbar">

        <button
          className={page === "dashboard" ? "active" : ""}
          onClick={() => setPage("dashboard")}
        >
          Dashboard
        </button>

        <button
          className={page === "assets" ? "active" : ""}
          onClick={() => setPage("assets")}
        >
          Assets
        </button>

        <button
          className={page === "purchases" ? "active" : ""}
          onClick={() => setPage("purchases")}
        >
          Purchases
        </button>

        <button
          className={page === "transfers" ? "active" : ""}
          onClick={() => setPage("transfers")}
        >
          Transfers
        </button>

        <button
          className={page === "assignments" ? "active" : ""}
          onClick={() => setPage("assignments")}
        >
          Assignments
        </button>

        <button
          className={page === "audit" ? "active" : ""}
          onClick={() => setPage("audit")}
        >
          Audit Logs
        </button>

      </nav>

      <main className="main">

        {error && (
          <div className="error">
            {error}
          </div>
        )}

        {renderPage()}

      </main>

    </div>
  );
}

export default App;