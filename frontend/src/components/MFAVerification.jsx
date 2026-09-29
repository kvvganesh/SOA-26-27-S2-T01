import { useState } from "react";

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
    <div>

      <h1>Multi-Factor Authentication</h1>

      <p>
        {message}
      </p>

      <p>
        Enter the 6-digit OTP sent to your
        registered contact.
      </p>


      {error && (
        <p>
          {error}
        </p>
      )}


      <form onSubmit={handleSubmit}>

        <input
          type="text"
          inputMode="numeric"
          maxLength="6"
          placeholder="Enter OTP"
          value={otp}
          onChange={(event) => {

            const value =
              event.target.value
                .replace(/\D/g, "");

            setOtp(value);

          }}
        />


        <button
          type="submit"
          disabled={
            loading ||
            otp.length !== 6
          }
        >

          {loading
            ? "Verifying..."
            : "Verify OTP"}

        </button>

      </form>

    </div>
  );
}

export default MFAVerification;