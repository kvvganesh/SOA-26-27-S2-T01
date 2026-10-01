import { useState } from "react";

import LoginForm from "../components/LoginForm";
import MFAVerification from "../components/MFAVerification";
import AuthLayout from "../components/AuthLayout";
import Icon from "../components/Icons";
import SecurityDashboard from "../components/SecurityDashboard";

import {
  login,
  verifyMFA,
  logout
} from "../services/authService";

import { getDeviceId } from "../services/deviceService";
import { getCurrentLocation } from "../services/locationService";


function LoginPage({ title, onCreateAccount }) {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const [mfaRequired, setMfaRequired] =
    useState(false);

  const [authenticated, setAuthenticated] =
    useState(false);

  const [loginResponse, setLoginResponse] =
    useState(null);

  const [securityContext, setSecurityContext] =
    useState(null);


  // =====================================================
  // LOGIN
  // =====================================================

  async function handleLogin() {

    setError("");
    setLoading(true);

    try {

      if (username.trim() === "") {
        setError("Username is required");
        return;
      }

      if (password.trim() === "") {
        setError("Password is required");
        return;
      }


      // -------------------------------------------------
      // DEVICE
      // -------------------------------------------------

      const deviceId =
        getDeviceId();


      // -------------------------------------------------
      // LOCATION
      // -------------------------------------------------

      // Location is optional: if it is denied / unavailable we still
      // sign in (the location is then simply "not trusted") instead of
      // blocking the whole login like before.
      let location = null;
      let locationError = "";

      try {

        location =
          await getCurrentLocation();

      } catch (locationProblem) {

        console.warn(
          "Location unavailable:",
          locationProblem
        );

        locationError =
          locationProblem.message ||
          "Location was not available for this sign-in.";
      }


      // -------------------------------------------------
      // LOGIN TIME
      // -------------------------------------------------

      const loginHour =
        new Date().getHours();


      // -------------------------------------------------
      // SECURITY CONTEXT
      // -------------------------------------------------

      const context = {

        deviceId,

        latitude:
          location?.latitude ?? null,

        longitude:
          location?.longitude ?? null,

        accuracy:
          location?.accuracy ?? null,

        locationError,

        loginHour
      };


      setSecurityContext(context);


      // -------------------------------------------------
      // LOGIN REQUEST
      // -------------------------------------------------

      const loginData = {

        username,
        password,

        deviceId,

        latitude:
          context.latitude,

        longitude:
          context.longitude,

        accuracy:
          context.accuracy,

        loginHour
      };


      console.log(
        "Sending automatic security context:",
        loginData
      );


      const result =
        await login(loginData);


      console.log(
        "Backend login response:",
        result
      );


      const data =
        result.data || {};

      setLoginResponse(
        data
      );


      // -------------------------------------------------
      // MFA REQUIRED
      // -------------------------------------------------

      if (
        data.status ===
        "MFA_REQUIRED"
      ) {

        setMfaRequired(true);

        return;
      }


      // -------------------------------------------------
      // DIRECT AUTHENTICATION
      // -------------------------------------------------

      if (
        data.status ===
        "AUTHENTICATED"
      ) {

        setAuthenticated(true);

        return;
      }


      // -------------------------------------------------
      // LOGIN FAILED
      // -------------------------------------------------

      setError(
        data.message ||
        "Login failed"
      );

    } catch (error) {

      console.error(
        "Login failed:",
        error
      );

      // Location problems are handled above, so reaching this
      // point means the request itself failed.
      setError(
        "Unable to reach the server. Please check that the backend is running."
      );

    } finally {

      setLoading(false);
    }
  }


  // =====================================================
  // MFA VERIFICATION
  // =====================================================

  async function handleVerifyMFA(otp) {

    setError("");

    try {

      const result =
        await verifyMFA(
          username,
          otp
        );


      console.log(
        "MFA verification response:",
        result
      );


      const data =
        result.data || {};


      // -------------------------------------------------
      // MFA SUCCESS
      // -------------------------------------------------

      if (
        data.status ===
        "AUTHENTICATED"
      ) {

        // The MFA response only carries the suggestions. Keep the risk
        // score / AI result / risk level from the original login so the
        // dashboard doesn't show "0" and "N/A" after a challenge.
        setLoginResponse((previous) => ({
          ...previous,
          status: data.status,
          message: data.message,
          securitySuggestions: data.securitySuggestions
        }));

        setMfaRequired(false);

        setAuthenticated(true);

        return;
      }


      // -------------------------------------------------
      // MFA FAILURE
      // -------------------------------------------------

      setError(
        data.message ||
        "Invalid or expired OTP"
      );

    } catch (error) {

      console.error(
        "MFA verification failed:",
        error
      );

      setError(
        "Unable to verify OTP"
      );
    }
  }


  // =====================================================
  // LOGOUT
  // =====================================================

  async function handleLogout() {

    try {

      await logout();

    } catch (error) {

      // Even if the server can't be reached, leave the dashboard.
      console.error("Logout failed:", error);
    }

    setAuthenticated(false);
    setMfaRequired(false);
    setLoginResponse(null);
    setSecurityContext(null);
    setPassword("");
    setError("");
  }


  // =====================================================
  // SECURITY DASHBOARD
  // =====================================================

  if (authenticated) {

    return (

      <SecurityDashboard

        loginResponse={
          loginResponse
        }

        loginContext={
          securityContext
        }

        onLogout={
          handleLogout
        }

      />

    );
  }


  // =====================================================
  // MFA SCREEN
  // =====================================================

  if (mfaRequired) {

    return (

      <MFAVerification

        username={
          username
        }

        message={
          loginResponse?.message ||
          "Additional authentication required."
        }

        onVerify={
          handleVerifyMFA
        }

      />

    );
  }


  // =====================================================
  // LOGIN SCREEN
  // =====================================================

  return (

    <AuthLayout
      title="Welcome back"
      subtitle={`Sign in to ${title || "your account"} securely.`}
      footer={
        onCreateAccount && (
          <>
            New here?{" "}
            <button
              type="button"
              className="link-btn"
              onClick={onCreateAccount}
            >
              Create an account
            </button>
          </>
        )
      }
    >

      {error && (
        <div className="alert error" role="alert">
          <Icon name="alert" size={16} />
          {error}
        </div>
      )}

      <LoginForm
        username={username}
        setUsername={setUsername}
        password={password}
        setPassword={setPassword}
        onLogin={handleLogin}
        loading={loading}
      />

      <p className="hint center">
        <Icon name="pin" size={14} /> Device, location and login time are
        checked automatically to assess risk.
      </p>

    </AuthLayout>
  );
}


export default LoginPage;
