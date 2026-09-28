import { useEffect, useState } from "react";
import "./App.css";

const API = "http://localhost:8080";

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [user, setUser] = useState(null);
  const [loginError, setLoginError] = useState("");

  const [page, setPage] = useState("dashboard");

  const [dashboard, setDashboard] = useState({
    openingBalance: 0,
    purchases: 0,
    transferIn: 0,
    transferOut: 0,
    expenditure: 0,
    closingBalance: 0,
  });

  const [assets, setAssets] = useState([]);
  const [purchases, setPurchases] = useState([]);
  const [transfers, setTransfers] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);

  const [error, setError] = useState("");

  const login = async (e) => {
    e.preventDefault();
    setLoginError("");

    try {
      const response = await fetch(`${API}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username,
          password,
        }),
      });

      if (!response.ok) {
        throw new Error("Invalid username or password");
      }

      const data = await response.json();

      setUser(data);
      setLoggedIn(true);
    } catch (err) {
      setLoginError(err.message);
    }
  };

  const loadData = async () => {
    try {
      setError("");

      const [
        dashboardResponse,
        assetsResponse,
        purchasesResponse,
        transfersResponse,
        assignmentsResponse,
        auditResponse,
      ] = await Promise.all([
        fetch(`${API}/api/dashboard`),
        fetch(`${API}/api/assets`),
        fetch(`${API}/api/purchases`),
        fetch(`${API}/api/transfers`),
        fetch(`${API}/api/assignments`),
        fetch(`${API}/api/audit-logs`),
      ]);

      if (
        !dashboardResponse.ok ||
        !assetsResponse.ok ||
        !purchasesResponse.ok ||
        !transfersResponse.ok ||
        !assignmentsResponse.ok ||
        !auditResponse.ok
      ) {
        throw new Error("Failed to load data");
      }

      setDashboard(await dashboardResponse.json());
      setAssets(await assetsResponse.json());
      setPurchases(await purchasesResponse.json());
      setTransfers(await transfersResponse.json());
      setAssignments(await assignmentsResponse.json());
      setAuditLogs(await auditResponse.json());
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    if (loggedIn) {
      loadData();
    }
  }, [loggedIn]);

  if (!loggedIn) {
    return (
      <div className="login-page">
        <div className="login-card">
          <h1>Military Asset Management</h1>

          <p>Asset Management System</p>

          <h2>Login</h2>

          <form onSubmit={login}>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />

            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            {loginError && (
              <div className="error">
                {loginError}
              </div>
            )}

            <button type="submit">
              Login
            </button>
          </form>

          <div className="login-help">
            <strong>Demo accounts</strong>

            <p>admin / admin123</p>
            <p>commander / commander123</p>
            <p>logistics / logistics123</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Military Asset Management</h1>
          <p>Asset Management System</p>
        </div>

        <div className="user-info">
          <strong>{user?.username?.toUpperCase()}</strong>
          <span>
            {user?.role}
          </span>

          <button
            onClick={() => {
              setLoggedIn(false);
              setUser(null);
              setUsername("");
              setPassword("");
            }}
          >
            Logout
          </button>
        </div>
      </header>

      <nav className="nav">
        {[
          ["dashboard", "Dashboard"],
          ["assets", "Assets"],
          ["purchases", "Purchases"],
          ["transfers", "Transfers"],
          ["assignments", "Assignments"],
          ["audit", "Audit Logs"],
        ].map(([key, label]) => (
          <button
            key={key}
            className={page === key ? "active" : ""}
            onClick={() => setPage(key)}
          >
            {label}
          </button>
        ))}
      </nav>

      <main className="content">

        {error && (
          <div className="error-box">
            {error}
          </div>
        )}

        {page === "dashboard" && (
          <>
            <h2>Dashboard</h2>

            <p className="subtitle">
              Overview of military assets, inventory movements and expenditure
            </p>

            <div className="filter-card">
              <label>Date</label>
              <input type="date" />

              <label>Base</label>
              <select>
                <option>All Bases</option>
              </select>

              <label>Equipment Type</label>
              <select>
                <option>All Equipment</option>
              </select>

              <button onClick={loadData}>
                Apply Filters
              </button>
            </div>

            <div className="cards">

              <div className="card">
                <h3>Opening Balance</h3>
                <strong>{dashboard.openingBalance}</strong>
              </div>

              <div className="card">
                <h3>Purchases</h3>
                <strong>{dashboard.purchases}</strong>
              </div>

              <div className="card">
                <h3>Transfer In</h3>
                <strong>{dashboard.transferIn}</strong>
              </div>

              <div className="card">
                <h3>Transfer Out</h3>
                <strong>{dashboard.transferOut}</strong>
              </div>

              <div className="card">
                <h3>Expenditure</h3>
                <strong>{dashboard.expenditure}</strong>
              </div>

              <div className="card closing">
                <h3>Closing Balance</h3>
                <strong>{dashboard.closingBalance}</strong>
              </div>

            </div>
          </>
        )}

        {page === "assets" && (
          <>
            <h2>Assets</h2>

            <p className="subtitle">
              View all military assets
            </p>

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
          </>
        )}

        {page === "purchases" && (
          <>
            <h2>Purchases</h2>

            <p className="subtitle">
              Purchase history
            </p>

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
                    <td>{purchase.purchaseDate}</td>
                    <td>{purchase.createdBy}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}

        {page === "transfers" && (
          <>
            <h2>Transfers</h2>

            <p className="subtitle">
              Asset transfer history
            </p>

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
                    <td>
                      {transfer.transferDate
                        ? transfer.transferDate.substring(0, 10)
                        : ""}
                    </td>
                    <td>{transfer.createdBy}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}

        {page === "assignments" && (
          <>
            <h2>Assignments</h2>

            <p className="subtitle">
              Asset assignment and expenditure history
            </p>

            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Asset</th>
                  <th>Personnel</th>
                  <th>Quantity</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>

              <tbody>
                {assignments.map((assignment) => (
                  <tr key={assignment.id}>
                    <td>{assignment.id}</td>
                    <td>{assignment.asset?.assetCode}</td>
                    <td>{assignment.personnelName}</td>
                    <td>{assignment.quantity}</td>
                    <td>{assignment.status}</td>
                    <td>
                      {assignment.assignedDate
                        ? assignment.assignedDate.substring(0, 10)
                        : ""}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}

        {page === "audit" && (
          <>
            <h2>Audit Logs</h2>

            <p className="subtitle">
              System activity history
            </p>

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
                    <td>
                      {log.timestamp
                        ? log.timestamp.replace("T", " ")
                        : ""}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        )}

      </main>
    </div>
  );
}

export default App;