import { useState } from "react";

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


function RegistrationPage({ onRegistrationComplete }) {

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

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


      // =========================================
      // REGISTER USER
      // =========================================

      const registrationResult =
        await register({
          username: username,
          password: password
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

      const location =
        await getCurrentLocation();


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
          location.latitude,

        longitude:
          location.longitude,

        accuracy:
          location.accuracy,

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
          "Account created, but security enrollment failed. Please contact support."
        );

        return;
      }


      // =========================================
      // COMPLETE
      // =========================================

      alert(
        "Registration and security setup completed successfully."
      );


      if (onRegistrationComplete) {

        onRegistrationComplete();

      }

    }

    catch (error) {

      console.error(
        "Registration failed:",
        error
      );


      setError(
        "Unable to complete registration. Please check location permission and backend connection."
      );

    }

    finally {

      setLoading(false);

    }

  }


  return (

    <div>

      <h1>Create Account</h1>

      <p>
        Your device, location and login-time
        information will be securely enrolled
        for adaptive authentication.
      </p>


      {error && (

        <p>
          {error}
        </p>

      )}


      <form onSubmit={handleRegistration}>

        <div>

          <label>
            Username
          </label>

          <input
            type="text"
            placeholder="Enter username"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
          />

        </div>


        <div>

          <label>
            Password
          </label>

          <input
            type="password"
            placeholder="Enter password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
          />

        </div>


        <button
          type="submit"
          disabled={loading}
        >

          {loading
            ? "Creating account..."
            : "Create Account"}

        </button>

      </form>

    </div>

  );

}


export default RegistrationPage;