import { useState } from "react";
import {
  rememberDevice,
  rememberLocation,
  rememberLoginTime
} from "../services/authService";

function SecuritySettings({ loginContext }) {

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState("");

  async function handleRememberDevice() {
    setMessage("");
    setError("");
    setLoading("device");

    try {
      const result = await rememberDevice(
        loginContext.deviceId,
        "This Browser"
      );

      if (result.statusCode === 200) {
        setMessage("This device has been remembered.");
      } else {
        setError(
          result.data?.message ||
          "Unable to remember this device."
        );
      }

    } catch (error) {
      console.error(error);
      setError("Unable to remember this device.");
    } finally {
      setLoading("");
    }
  }


  async function handleRememberLocation() {
    setMessage("");
    setError("");
    setLoading("location");

    try {
      const result = await rememberLocation(
        loginContext.latitude,
        loginContext.longitude,
        100,
        "Current Location"
      );

      if (result.statusCode === 200) {
        setMessage("This location has been remembered.");
      } else {
        setError(
          result.data?.message ||
          "Unable to remember this location."
        );
      }

    } catch (error) {
      console.error(error);
      setError("Unable to remember this location.");
    } finally {
      setLoading("");
    }
  }


  async function handleRememberLoginTime() {
    setMessage("");
    setError("");
    setLoading("time");

    try {
      const currentHour =
        loginContext.loginHour;

      const startHour =
        Math.max(0, currentHour - 1);

      const endHour =
        Math.min(23, currentHour + 1);

      const result = await rememberLoginTime(
        startHour,
        endHour
      );

      if (result.statusCode === 200) {
        setMessage(
          `Login time remembered: ${startHour}:00 - ${endHour}:00`
        );
      } else {
        setError(
          result.data?.message ||
          "Unable to remember login time."
        );
      }

    } catch (error) {
      console.error(error);
      setError("Unable to remember login time.");
    } finally {
      setLoading("");
    }
  }


  if (!loginContext) {
    return (
      <p>
        Security information is unavailable.
      </p>
    );
  }


  return (
    <div>

      <h2>Security Settings</h2>

      <p>
        Your authentication was successful.
        You can optionally remember security
        factors for future logins.
      </p>

      {message && (
        <p>{message}</p>
      )}

      {error && (
        <p>{error}</p>
      )}


      <div>
        <h3>📱 Device</h3>

        <p>
          Remember this browser as a trusted device.
        </p>

        <button
          onClick={handleRememberDevice}
          disabled={loading !== ""}
        >
          {loading === "device"
            ? "Saving..."
            : "Remember Device"}
        </button>
      </div>


      <div>
        <h3>📍 Location</h3>

        <p>
          Remember the location detected during login.
        </p>

        <button
          onClick={handleRememberLocation}
          disabled={loading !== ""}
        >
          {loading === "location"
            ? "Saving..."
            : "Remember Location"}
        </button>
      </div>


      <div>
        <h3>🕐 Login Time</h3>

        <p>
          Remember the login time detected during login.
        </p>

        <button
          onClick={handleRememberLoginTime}
          disabled={loading !== ""}
        >
          {loading === "time"
            ? "Saving..."
            : "Remember Login Time"}
        </button>
      </div>

    </div>
  );
}

export default SecuritySettings;