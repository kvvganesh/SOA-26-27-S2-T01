import { useState } from "react";

import LoginForm from "../components/LoginForm";
import MFAVerification from "../components/MFAVerification";
import SecurityDashboard from "../components/SecurityDashboard";

import {
  login,
  verifyMFA
} from "../services/authService";

import { getDeviceId } from "../services/deviceService";
import { getCurrentLocation } from "../services/locationService";


function LoginPage({ title }) {

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

      const location =
        await getCurrentLocation();


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
          location.latitude,

        longitude:
          location.longitude,

        accuracy:
          location.accuracy,

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
          location.latitude,

        longitude:
          location.longitude,

        accuracy:
          location.accuracy,

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


      setLoginResponse(
        result.data
      );


      // -------------------------------------------------
      // MFA REQUIRED
      // -------------------------------------------------

      if (
        result.data.status ===
        "MFA_REQUIRED"
      ) {

        setMfaRequired(true);

        return;
      }


      // -------------------------------------------------
      // DIRECT AUTHENTICATION
      // -------------------------------------------------

      if (
        result.data.status ===
        "AUTHENTICATED"
      ) {

        setAuthenticated(true);

        return;
      }


      // -------------------------------------------------
      // LOGIN FAILED
      // -------------------------------------------------

      setError(
        result.data.message ||
        "Login failed"
      );

    } catch (error) {

      console.error(
        "Login failed:",
        error
      );

      setError(
        "Unable to detect location or connect to backend."
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


      setLoginResponse(
        result.data
      );


      // -------------------------------------------------
      // MFA SUCCESS
      // -------------------------------------------------

      if (
        result.data.status ===
        "AUTHENTICATED"
      ) {

        setMfaRequired(false);

        setAuthenticated(true);

        return;
      }


      // -------------------------------------------------
      // MFA FAILURE
      // -------------------------------------------------

      setError(
        result.data.message ||
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

    <div>

      <h1>
        {title}
      </h1>

      <h2>
        Secure Login
      </h2>


      {error && (
        <p>
          {error}
        </p>
      )}


      <LoginForm

        username={
          username
        }

        setUsername={
          setUsername
        }

        password={
          password
        }

        setPassword={
          setPassword
        }

        onLogin={
          handleLogin
        }

      />


      {loading && (

        <p>
          Detecting security information...
        </p>

      )}

    </div>
  );
}


export default LoginPage;