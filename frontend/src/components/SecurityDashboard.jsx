import { useState } from "react";
import Icon from "./Icons";
import SecuritySuggestions from "./SecuritySuggestions";
import SecurityFactors from "./SecurityFactors";
import TrustedDevices from "./TrustedDevices";
import TrustedLocations from "./TrustedLocations";
import TrustedLoginTimes from "./TrustedLoginTimes";
import AuditLogs from "./AuditLogs";

const NAV_ITEMS = [
  ["grid", "Dashboard", "#dashboard"],
  ["shield", "Security", "#security"],
  ["device", "Devices", "#devices"],
  ["pin", "Locations", "#locations"],
  ["clock", "Login Times", "#login-times"],
  ["list", "Audit Logs", "#audit-logs"],
];

function SectionTitle({ label, title }) {
  return (
    <div className="section-title">
      <div>
        <span className="card-label">{label}</span>
        <h2>{title}</h2>
      </div>
    </div>
  );
}

function SecurityDashboard({
  loginResponse,
  loginContext,
  onLogout
}) {

  // Bumped whenever a factor is remembered so the three lists reload.
  const [refreshKey, setRefreshKey] =
    useState(0);

  // Suggestion types the user has already acted on.
  const [handled, setHandled] =
    useState([]);

  function handleFactorChanged(type) {

    setRefreshKey((key) => key + 1);

    setHandled((current) => [...current, type]);
  }

  const openSuggestions =
    (loginResponse?.securitySuggestions ?? [])
      .filter((s) => !handled.includes(s.type));

  const riskLevel =
    loginResponse?.riskLevel || "N/A";

  const riskClass =
    riskLevel.toLowerCase();

  return (
    <div className="app-shell">

      {/* SIDEBAR */}

      <aside className="sidebar">

        <div className="brand">
          <div className="brand-icon">
            <Icon name="lock" size={20} />
          </div>

          <div>
            <h2>AdaptiveMFA</h2>
            <span>ACS Security</span>
          </div>
        </div>

        <nav className="sidebar-nav" aria-label="Primary">
          {NAV_ITEMS.map(([icon, label, href], index) => (
            <a
              key={label}
              href={href}
              className={`nav-item${index === 0 ? " active" : ""}`}
            >
              <Icon name={icon} size={18} />
              <span>{label}</span>
            </a>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="security-status">
            <span className="status-dot"></span>

            <div>
              <strong>System Secure</strong>
              <small>Protection active</small>
            </div>
          </div>
        </div>

      </aside>


      {/* MAIN CONTENT */}

      <main className="dashboard-main" id="dashboard">

        {/* HEADER */}

        <header className="dashboard-header">

          <div>
            <p className="eyebrow">SECURITY CENTER</p>

            <h1>Adaptive Security Dashboard</h1>

            <p className="header-description">
              Monitor authentication risk and manage
              your security factors.
            </p>
          </div>

          <div className="user-status">
            <div className="avatar">
              {loginContext?.deviceId ? "U" : "?"}
            </div>

            <div>
              <strong>Authenticated</strong>
              <span>Secure session active</span>
            </div>

            {onLogout && (
              <button
                type="button"
                className="btn btn-outline"
                onClick={onLogout}
              >
                <Icon name="logout" size={16} /> Sign out
              </button>
            )}
          </div>

        </header>


        {/* TOP RISK SUMMARY */}

        <section className="dashboard-grid" id="security">

          <div className={`dashboard-card risk-card ${riskClass}`}>

            <div className="card-heading">
              <div>
                <span className="card-label">CURRENT RISK</span>
                <h2>Authentication Risk</h2>
              </div>

              <div className={`risk-badge ${riskClass}`}>
                {riskLevel}
              </div>
            </div>

            <div className="risk-content">

              <div className={`risk-circle ${riskClass}`}>
                <span>{loginResponse?.ruleScore ?? 0}</span>
                <small>Risk score</small>
              </div>

              <div className="risk-details">

                <div className="risk-detail">
                  <span>Rule Engine</span>
                  <strong>{loginResponse?.ruleScore ?? "N/A"}</strong>
                </div>

                <div className="risk-detail">
                  <span>AI Assessment</span>
                  <strong>{loginResponse?.aiRisk || "N/A"}</strong>
                </div>

                <div className="risk-detail">
                  <span>Status</span>
                  <strong className="success-text">Protected</strong>
                </div>

              </div>

            </div>

          </div>


          {/* AI CARD */}

          <div className="dashboard-card">

            <div className="card-heading">
              <div>
                <span className="card-label">ARTIFICIAL INTELLIGENCE</span>
                <h2>AI Risk Analysis</h2>
              </div>

              <div className="ai-icon">
                <Icon name="sparkle" size={18} />
              </div>
            </div>

            <div className="ai-risk-value">
              {loginResponse?.aiRisk || "N/A"}
            </div>

            {loginResponse?.aiProbabilities && (

              <div className="probability-list">

                {Object.entries(
                  loginResponse.aiProbabilities
                ).map(
                  ([risk, probability]) => (

                    <div className="probability-row" key={risk}>

                      <div className="probability-label">
                        <span>{risk}</span>
                        <strong>{(probability * 100).toFixed(1)}%</strong>
                      </div>

                      <div className="progress-track">
                        <div
                          className={`progress-bar ${risk.toLowerCase()}`}
                          style={{ width: `${probability * 100}%` }}
                        />
                      </div>

                    </div>

                  )
                )}

              </div>

            )}

          </div>


          {/* SESSION CARD */}

          <div className="dashboard-card session-card">

            <div className="card-heading">
              <div>
                <span className="card-label">SESSION</span>
                <h2>Authentication</h2>
              </div>

              <span className="live-badge">● LIVE</span>
            </div>

            <div className="session-info">

              <div>
                <span>Status</span>
                <strong>AUTHENTICATED</strong>
              </div>

              <div>
                <span>Login Hour</span>
                <strong>{loginContext?.loginHour ?? "N/A"}:00</strong>
              </div>

              <div>
                <span>Device</span>
                <strong>{loginContext?.deviceId ? "Detected" : "Unknown"}</strong>
              </div>

            </div>

          </div>

        </section>


        {/* SECURITY SUGGESTIONS */}

        <SectionTitle label="RECOMMENDATIONS" title="Security Suggestions" />

        <div className="dashboard-card">
          <SecuritySuggestions
            suggestions={openSuggestions}
          />
        </div>


        {/* FACTORS */}

        <SectionTitle label="SECURITY CONTROLS" title="Trusted Security Factors" />

        <div className="dashboard-card">
          <SecurityFactors
            loginContext={loginContext}
            onChanged={handleFactorChanged}
          />
        </div>


        {/* SAVED SECURITY FACTORS */}

        <div className="factor-grid">

          <div className="dashboard-card" id="devices">
            <TrustedDevices refreshKey={refreshKey} />
          </div>

          <div className="dashboard-card" id="locations">
            <TrustedLocations refreshKey={refreshKey} />
          </div>

          <div className="dashboard-card" id="login-times">
            <TrustedLoginTimes refreshKey={refreshKey} />
          </div>

        </div>

        {/* MY AUDIT LOGS */}

        <SectionTitle label="ACTIVITY" title="My Sign-in History" />

        <div className="dashboard-card" id="audit-logs">
          <AuditLogs />
        </div>


        {/* FOOTER */}

        <footer className="dashboard-footer">
          <span>AdaptiveMFA-ACS</span>
          <span>Adaptive authentication protection active</span>
        </footer>

      </main>

    </div>
  );
}

export default SecurityDashboard;
