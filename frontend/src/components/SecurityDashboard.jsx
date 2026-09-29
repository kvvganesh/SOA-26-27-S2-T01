import RiskSummary from "./RiskSummary";
import SecuritySuggestions from "./SecuritySuggestions";
import SecurityFactors from "./SecurityFactors";
import TrustedDevices from "./TrustedDevices";
import TrustedLocations from "./TrustedLocations";
import TrustedLoginTimes from "./TrustedLoginTimes";

function SecurityDashboard({
  loginResponse,
  loginContext
}) {

  const riskLevel =
    loginResponse?.riskLevel || "N/A";

  return (
    <div className="app-shell">

      {/* SIDEBAR */}

      <aside className="sidebar">

        <div className="brand">
          <div className="brand-icon">
            🔐
          </div>

          <div>
            <h2>AdaptiveMFA</h2>
            <span>ACS Security</span>
          </div>
        </div>

        <nav className="sidebar-nav">

          <a className="nav-item active">
            <span>▣</span>
            Dashboard
          </a>

          <a className="nav-item">
            <span>🛡️</span>
            Security
          </a>

          <a className="nav-item">
            <span>📱</span>
            Devices
          </a>

          <a className="nav-item">
            <span>📍</span>
            Locations
          </a>

          <a className="nav-item">
            <span>🕐</span>
            Login Times
          </a>

          <a className="nav-item">
            <span>📋</span>
            Audit Logs
          </a>

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

      <main className="dashboard-main">

        {/* HEADER */}

        <header className="dashboard-header">

          <div>

            <p className="eyebrow">
              SECURITY CENTER
            </p>

            <h1>
              Adaptive Security Dashboard
            </h1>

            <p className="header-description">
              Monitor authentication risk and manage
              your security factors.
            </p>

          </div>

          <div className="user-status">

            <div className="avatar">
              {loginContext?.deviceId
                ? "U"
                : "?"}
            </div>

            <div>
              <strong>
                Authenticated
              </strong>

              <span>
                Secure session active
              </span>
            </div>

          </div>

        </header>


        {/* TOP RISK SUMMARY */}

        <section className="dashboard-grid">

          <div className="dashboard-card risk-card">

            <div className="card-heading">
              <div>
                <span className="card-label">
                  CURRENT RISK
                </span>

                <h2>
                  Authentication Risk
                </h2>
              </div>

              <div
                className={`risk-badge ${riskLevel.toLowerCase()}`}
              >
                {riskLevel}
              </div>
            </div>

            <div className="risk-content">

              <div className="risk-circle">
                <span>
                  {loginResponse?.ruleScore ?? 0}
                </span>

                <small>
                  Risk Score
                </small>
              </div>

              <div className="risk-details">

                <div className="risk-detail">
                  <span>Rule Engine</span>
                  <strong>
                    {loginResponse?.ruleScore ?? "N/A"}
                  </strong>
                </div>

                <div className="risk-detail">
                  <span>AI Assessment</span>
                  <strong>
                    {loginResponse?.aiRisk || "N/A"}
                  </strong>
                </div>

                <div className="risk-detail">
                  <span>Status</span>
                  <strong className="success-text">
                    Protected
                  </strong>
                </div>

              </div>

            </div>

          </div>


          {/* AI CARD */}

          <div className="dashboard-card">

            <div className="card-heading">

              <div>
                <span className="card-label">
                  ARTIFICIAL INTELLIGENCE
                </span>

                <h2>
                  AI Risk Analysis
                </h2>
              </div>

              <div className="ai-icon">
                ✦
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

                    <div
                      className="probability-row"
                      key={risk}
                    >

                      <div className="probability-label">
                        <span>{risk}</span>

                        <strong>
                          {(probability * 100).toFixed(1)}%
                        </strong>
                      </div>

                      <div className="progress-track">

                        <div
                          className={`progress-bar ${risk.toLowerCase()}`}
                          style={{
                            width:
                              `${probability * 100}%`
                          }}
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
                <span className="card-label">
                  SESSION
                </span>

                <h2>
                  Authentication
                </h2>
              </div>

              <span className="live-badge">
                ● LIVE
              </span>

            </div>

            <div className="session-info">

              <div>
                <span>Status</span>
                <strong>
                  AUTHENTICATED
                </strong>
              </div>

              <div>
                <span>Login Hour</span>
                <strong>
                  {loginContext?.loginHour ?? "N/A"}:00
                </strong>
              </div>

              <div>
                <span>Device</span>
                <strong>
                  {loginContext?.deviceId
                    ? "Detected"
                    : "Unknown"}
                </strong>
              </div>

            </div>

          </div>

        </section>


        {/* SECURITY SUGGESTIONS */}

        <div className="section-title">

          <div>
            <span className="card-label">
              RECOMMENDATIONS
            </span>

            <h2>
              Security Suggestions
            </h2>
          </div>

        </div>

        <div className="dashboard-card">

          <SecuritySuggestions
            suggestions={
              loginResponse?.securitySuggestions
            }
          />

        </div>


        {/* FACTORS */}

        <div className="section-title">

          <div>
            <span className="card-label">
              SECURITY CONTROLS
            </span>

            <h2>
              Trusted Security Factors
            </h2>

          </div>

        </div>

        <div className="dashboard-card">

          <SecurityFactors
            loginContext={loginContext}
          />

        </div>


        {/* SAVED SECURITY FACTORS */}

        <div className="factor-grid">

          <div className="dashboard-card">

            <TrustedDevices />

          </div>

          <div className="dashboard-card">

            <TrustedLocations />

          </div>

          <div className="dashboard-card">

            <TrustedLoginTimes />

          </div>

        </div>


        {/* FOOTER */}

        <footer className="dashboard-footer">

          <span>
            🔐 AdaptiveMFA-ACS
          </span>

          <span>
            Adaptive authentication protection active
          </span>

        </footer>

      </main>

    </div>
  );
}

export default SecurityDashboard;