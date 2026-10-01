import { useEffect, useState } from "react";
import Icon from "./Icons";

import { listMyAuditLogs } from "../services/authService";


// Which colour badge (the existing low / medium / high styles) each
// backend decision should get.
const DECISION_TONE = {
  "AUTHENTICATED": "low",
  "MFA_AUTHENTICATED": "low",
  "ACCOUNT_UNLOCKED": "low",
  "MFA_REQUIRED": "medium",
  "MFA_FAILED": "high",
  "ACCESS DENIED": "high"
};

// Friendlier text than the raw enum-like strings.
const DECISION_LABEL = {
  "AUTHENTICATED": "Signed in",
  "MFA_AUTHENTICATED": "Signed in (OTP)",
  "MFA_REQUIRED": "OTP requested",
  "MFA_FAILED": "OTP failed",
  "ACCESS DENIED": "Denied",
  "ACCOUNT_UNLOCKED": "Account unlocked"
};


// Spring normally sends "2026-09-30T10:15:30", but some Jackson set-ups send
// [2026, 9, 30, 10, 15, 30]. Handle both so the table never shows "Invalid Date".
function formatTime(timestamp) {

  if (!timestamp) return "—";

  const date = Array.isArray(timestamp)
    ? new Date(timestamp[0], timestamp[1] - 1, timestamp[2],
               timestamp[3] ?? 0, timestamp[4] ?? 0, timestamp[5] ?? 0)
    : new Date(timestamp);

  return Number.isNaN(date.getTime())
    ? "—"
    : date.toLocaleString();
}


function AuditLogs() {

  const [logs, setLogs] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  // Bump to reload the table (the "Refresh" button).
  const [reloadKey, setReloadKey] =
    useState(0);


  useEffect(() => {

    let cancelled = false;

    listMyAuditLogs(50)
      .then((result) => {

        if (cancelled) return;

        if (!result.ok) {
          throw new Error(
            result.data?.message ||
            "Unable to load your sign-in history."
          );
        }

        setLogs(result.data ?? []);
        setError("");
      })
      .catch((err) => {

        if (cancelled) return;

        console.error(err);

        setError(
          err.message || "Unable to load your sign-in history."
        );
      })
      .finally(() => {

        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };

  }, [reloadKey]);


  return (
    <section className="panel">

      <div className="panel-head">
        <span className="panel-icon"><Icon name="list" size={18} /></span>
        <h2>Audit Logs</h2>
        {!loading && <span className="count">{logs.length}</span>}
      </div>

      <div className="log-toolbar">
        <p>Your last sign-in attempts, newest first (up to 50).</p>
        <button
          type="button"
          className="btn btn-outline"
          onClick={() => setReloadKey((key) => key + 1)}
        >
          Refresh
        </button>
      </div>

      {error && <div className="alert error" role="alert">{error}</div>}

      {loading ? (
        <p className="empty">Loading…</p>
      ) : logs.length === 0 && !error ? (
        <p className="empty">No sign-in activity recorded yet.</p>
      ) : (
        <div className="log-wrap">
          <table className="log-table">
            <thead>
              <tr>
                <th>Time</th>
                <th>Result</th>
                <th>Risk</th>
                <th>Score</th>
                <th>Failed</th>
                <th>Device</th>
                <th>Location</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td>{formatTime(log.timestamp)}</td>
                  <td>
                    <span className={`risk-badge ${DECISION_TONE[log.decision] || "medium"}`}>
                      {DECISION_LABEL[log.decision] || log.decision}
                    </span>
                  </td>
                  <td>{log.riskLevel || "—"}</td>
                  <td>{log.riskScore}</td>
                  <td>{log.failedAttempts}</td>
                  <td title={log.deviceId}>
                    {log.deviceId ? `${log.deviceId.slice(0, 8)}…` : "—"}
                  </td>
                  <td>
                    {log.latitude != null && log.longitude != null
                      ? `${Number(log.latitude).toFixed(3)}, ${Number(log.longitude).toFixed(3)}`
                      : "—"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

    </section>
  );
}


export default AuditLogs;
