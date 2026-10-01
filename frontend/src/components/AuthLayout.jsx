import Icon from "./Icons";

const HIGHLIGHTS = [
  ["shield", "Risk-based authentication", "Every sign-in is scored by rules and AI in real time."],
  ["pin", "Context-aware trust", "Devices, locations and login hours you trust skip friction."],
  ["key", "Step-up when it matters", "Suspicious attempts are challenged with a one-time code."],
];

function AuthLayout({ title, subtitle, children, footer }) {
  return (
    <div className="auth-shell">
      <aside className="auth-aside">
        <div className="brand">
          <div className="brand-icon"><Icon name="lock" size={20} /></div>
          <div>
            <h2>AdaptiveMFA</h2>
            <span>ACS Security</span>
          </div>
        </div>

        <div className="auth-pitch">
          <h1>Security that adapts to every sign-in.</h1>
          <ul>
            {HIGHLIGHTS.map(([icon, head, body]) => (
              <li key={head}>
                <span className="pitch-icon"><Icon name={icon} size={18} /></span>
                <div><strong>{head}</strong><p>{body}</p></div>
              </li>
            ))}
          </ul>
        </div>

        <small className="auth-copy">© AdaptiveMFA-ACS · Adaptive authentication</small>
      </aside>

      <main className="auth-main">
        <div className="auth-card">
          <header>
            <h2>{title}</h2>
            {subtitle && <p>{subtitle}</p>}
          </header>
          {children}
          {footer && <div className="auth-footer">{footer}</div>}
        </div>
      </main>
    </div>
  );
}

export default AuthLayout;
