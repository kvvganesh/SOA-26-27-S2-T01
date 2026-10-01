import { useState } from "react";

import AuthLayout from "../components/AuthLayout";
import Icon from "../components/Icons";

import {
  register,
  enrollSecurity
} from "../services/authService";

import {
  getDeviceId,
  getDeviceInfo
} from "../services/deviceService";

import {
  getCurrentLocation
} from "../services/locationService";


function RegistrationPage({ onRegistrationComplete, onBackToLogin }) {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  async function handleRegistration(event) {

    event.preventDefault();

    setError("");
    setLoading(true);


    try {

      // =========================================
      // VALIDATION
      // =========================================

      if (username.trim() === "") {
        setError("Username is required");
        return;
      }

      if (password.trim() === "") {
        setError("Password is required");
        return;
      }

      // One-time passwords are e-mailed to this address, so it must be real.
      // (The backend validates it again - never rely on the browser alone.)
      if (!/^\S+@\S+\.\S+$/.test(email.trim())) {
        setError("Enter a valid email address");
        return;
      }


      // =========================================
      // REGISTER USER
      // =========================================

      const registrationResult =
        await register({
          username: username,
          password: password,
          email: email.trim()
        });


      console.log(
        "Registration response:",
        registrationResult
      );


      if (
        registrationResult.statusCode !== 201
      ) {

        setError(
          registrationResult.data?.message ||
          "Registration failed"
        );

        return;
      }


      // =========================================
      // GET DEVICE INFORMATION
      // =========================================

      const deviceId =
        getDeviceId();

      const deviceInfo =
        getDeviceInfo();


      // =========================================
      // GET LOCATION
      // =========================================

      // Location is optional: without it the device and login time are
      // still enrolled and the location can be remembered after sign-in.
      let location = null;

      try {

        location =
          await getCurrentLocation();

      } catch (locationProblem) {

        console.warn(
          "Location unavailable:",
          locationProblem
        );
      }


      // =========================================
      // CURRENT LOGIN HOUR
      // =========================================

      const loginHour =
        new Date().getHours();


      // =========================================
      // SECURITY ENROLLMENT
      // =========================================

      const securityData = {

        username: username,

        password: password,

        deviceId: deviceId,

        deviceName:
          `${deviceInfo.platform} Browser`,

        latitude:
          location?.latitude ?? null,

        longitude:
          location?.longitude ?? null,

        accuracy:
          location?.accuracy ?? null,

        loginHour:
          loginHour

      };


      console.log(
        "Security enrollment data:",
        securityData
      );


      const enrollmentResult =
        await enrollSecurity(
          securityData
        );


      console.log(
        "Security enrollment response:",
        enrollmentResult
      );


      if (
        enrollmentResult.statusCode !== 200
      ) {

        setError(
          enrollmentResult.data?.message
            ? `Account created, but security enrollment failed: ${enrollmentResult.data.message}`
            : "Account created, but security enrollment failed. Please contact support."
        );

        return;
      }


      // =========================================
      // COMPLETE
      // =========================================

      setSuccess(
        enrollmentResult.data?.message ||
        "Registration and security setup completed successfully."
      );

      if (onRegistrationComplete) {

        setTimeout(onRegistrationComplete, location ? 1500 : 3500);

      }

    }

    catch (error) {

      console.error(
        "Registration failed:",
        error
      );


      setError(
        "Unable to reach the server. Please check that the backend is running."
      );

    }

    finally {

      setLoading(false);

    }

  }


  return (

    <AuthLayout
      title="Create your account"
      subtitle="Your device, location and login-time information will be securely enrolled for adaptive authentication."
      footer={
        onBackToLogin && (
          <>
            Already registered?{" "}
            <button
              type="button"
              className="link-btn"
              onClick={onBackToLogin}
            >
              Back to login
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

      {success && (
        <div className="alert success" role="status">
          <Icon name="check" size={16} />
          {success}
        </div>
      )}

      <form className="form" onSubmit={handleRegistration}>

        <div className="field">
          <label htmlFor="reg-username">Username</label>
          <input
            id="reg-username"
            type="text"
            autoComplete="username"
            placeholder="Choose a username"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
          />
        </div>

        <div className="field">
          <label htmlFor="reg-email">Email</label>
          <input
            id="reg-email"
            type="email"
            autoComplete="email"
            placeholder="Where should we send your one-time codes?"
            value={email}
            onChange={(event) =>
              setEmail(event.target.value)
            }
          />
        </div>

        <div className="field">
          <label htmlFor="reg-password">Password</label>
          <input
            id="reg-password"
            type="password"
            autoComplete="new-password"
            placeholder="Create a password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
          />
        </div>

        <button
          className="btn btn-primary btn-block"
          type="submit"
          disabled={loading || !!success}
        >
          {loading
            ? <><span className="spinner" /> Creating account…</>
            : "Create account"}
        </button>

      </form>

      <p className="hint center">
        <Icon name="pin" size={14} /> We'll ask for location permission to
        enroll this device.
      </p>

    </AuthLayout>

  );

}


export default RegistrationPage;
