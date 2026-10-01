function LoginForm({ username, setUsername, password, setPassword, onLogin, loading = false }) {

  function handleSubmit(event) {
    event.preventDefault();
    onLogin();
  }

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="field">
        <label htmlFor="login-username">Username</label>
        <input
          id="login-username"
          type="text"
          autoComplete="username"
          placeholder="Enter your username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
        />
      </div>

      <div className="field">
        <label htmlFor="login-password">Password</label>
        <input
          id="login-password"
          type="password"
          autoComplete="current-password"
          placeholder="Enter your password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
        />
      </div>

      <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
        {loading ? <><span className="spinner" /> Verifying context…</> : "Sign in"}
      </button>
    </form>
  );
}

export default LoginForm;
