import { useState } from "react";
import AuthLayout from "./AuthLayout";
import Icon from "./Icons";

function MFAVerification({
  username,
  message,
  onVerify
}) {

  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event) {

    event.preventDefault();

    setError("");

    if (otp.length !== 6) {
      setError("OTP must contain 6 digits");
      return;
    }

    setLoading(true);

    try {

      await onVerify(otp);

    } catch (error) {

      console.error(
        "MFA verification failed:",
        error
      );

      setError(
        "Unable to verify OTP"
      );

    } finally {

      setLoading(false);

    }
  }


  return (
    <AuthLayout
      title="Verify it's you"
      subtitle={message}
    >
      <div className="mfa-user">
        <Icon name="user" size={16} />
        <span>{username}</span>
      </div>

      <p className="hint">
        Enter the 6-digit code we sent you. It expires in 2 minutes
        and can be used once. Check your spam folder if it doesn't arrive.
      </p>

      {error && (
        <div className="alert error" role="alert">
          <Icon name="alert" size={16} />{error}
        </div>
      )}

      <form className="form" onSubmit={handleSubmit}>

        <div className="field">
          <label htmlFor="otp">One-time password</label>
          <input
            id="otp"
            className="otp-input"
            type="text"
            inputMode="numeric"
            autoComplete="one-time-code"
            autoFocus
            maxLength="6"
            placeholder="••••••"
            value={otp}
            onChange={(event) => {

              const value =
                event.target.value
                  .replace(/\D/g, "");

              setOtp(value);

            }}
          />
        </div>

        <button
          className="btn btn-primary btn-block"
          type="submit"
          disabled={
            loading ||
            otp.length !== 6
          }
        >
          {loading
            ? <><span className="spinner" /> Verifying…</>
            : "Verify OTP"}
        </button>

      </form>
    </AuthLayout>
  );
}

export default MFAVerification;
