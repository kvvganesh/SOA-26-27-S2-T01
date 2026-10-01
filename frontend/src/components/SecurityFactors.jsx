import { useState } from "react";
import Icon from "./Icons";

import {
  rememberDevice,
  rememberLocation,
  rememberLoginTime
} from "../services/authService";

import { getLocationName } from "../services/locationService";


// `onChanged(type)` lets the dashboard reload the trusted lists and hide the
// matching suggestion ("DEVICE" | "LOCATION" | "LOGIN_TIME").
function SecurityFactors({
  loginContext,
  onChanged
}) {

  const [loading, setLoading] =
    useState("");

  const [message, setMessage] =
    useState("");

  const [error, setError] =
    useState("");


  function clearMessages() {
    setMessage("");
    setError("");
  }


  async function handleRememberDevice() {

    clearMessages();
    setLoading("device");

    try {

      const result =
        await rememberDevice(
          loginContext.deviceId,
          "This Browser"
        );

      if (result.statusCode === 200) {

        setMessage("This device has been remembered.");

        onChanged?.("DEVICE");

      } else {

        setError(
          result.data?.message ||
          "Unable to remember this device."
        );
      }

    } catch (err) {

      console.error(err);

      setError(
        "Unable to reach the server. Is the backend running?"
      );

    } finally {

      setLoading("");
    }
  }


  async function handleRememberLocation() {

    clearMessages();
    setLoading("location");

    try {

      // Best effort: a readable label such as "Hyderabad".
      const placeName =
        await getLocationName(
          loginContext.latitude,
          loginContext.longitude
        );

      const result =
        await rememberLocation(
          loginContext.latitude,
          loginContext.longitude,
          100,
          placeName || "Current Location",
          loginContext.accuracy
        );

      if (result.statusCode === 200) {

        setMessage("This location has been remembered.");

        onChanged?.("LOCATION");

      } else {

        setError(
          result.data?.message ||
          "Unable to remember this location."
        );
      }

    } catch (err) {

      console.error(err);

      setError(
        "Unable to reach the server. Is the backend running?"
      );

    } finally {

      setLoading("");
    }
  }


  async function handleRememberLoginTime() {

    clearMessages();
    setLoading("time");

    try {

      const currentHour =
        loginContext.loginHour;

      // +/- 1 hour, wrapping around midnight (0 -> 23, 23 -> 0).
      // The old Math.max/Math.min version produced 0-1 at midnight and
      // 22-23 at 23:00, so the neighbouring hours were never trusted.
      const startHour =
        (currentHour + 23) % 24;

      const endHour =
        (currentHour + 1) % 24;

      const result =
        await rememberLoginTime(
          startHour,
          endHour
        );

      if (result.statusCode === 200) {

        setMessage(
          `Login time remembered: ${startHour}:00 - ${endHour}:59`
        );

        onChanged?.("LOGIN_TIME");

      } else {

        setError(
          result.data?.message ||
          "Unable to remember login time."
        );
      }

    } catch (err) {

      console.error(err);

      setError(
        "Unable to reach the server. Is the backend running?"
      );

    } finally {

      setLoading("");
    }
  }


  if (!loginContext) {
    return (
      <section>
        <p className="empty">
          Security context is unavailable.
        </p>
      </section>
    );
  }


  const hasLocation =
    loginContext.latitude != null &&
    loginContext.longitude != null;


  return (
    <section id="factors">

      <p className="section-copy">
        Remember individual security factors so future sign-ins from
        this context skip extra verification.
      </p>

      {message && (
        <div className="alert success" role="status">
          <Icon name="check" size={16} />{message}
        </div>
      )}

      {error && (
        <div className="alert error" role="alert">
          <Icon name="alert" size={16} />{error}
        </div>
      )}

      {!hasLocation && (
        <div className="alert warning" role="status">
          <Icon name="alert" size={16} />
          {loginContext.locationError ||
            "Location was not available for this sign-in."}
        </div>
      )}

      <div className="factor-tiles">

        <div className="factor-tile">
          <span className="panel-icon"><Icon name="device" size={18} /></span>
          <h3>Device</h3>
          <p>Remember this browser as a trusted device.</p>
          <button
            className="btn btn-outline"
            onClick={handleRememberDevice}
            disabled={loading !== ""}
          >
            {loading === "device" ? "Saving…" : "Remember device"}
          </button>
        </div>

        <div className="factor-tile">
          <span className="panel-icon"><Icon name="pin" size={18} /></span>
          <h3>Location</h3>
          <p>Remember the location detected during login.</p>
          <button
            className="btn btn-outline"
            onClick={handleRememberLocation}
            disabled={loading !== "" || !hasLocation}
          >
            {loading === "location" ? "Saving…" : "Remember location"}
          </button>
        </div>

        <div className="factor-tile">
          <span className="panel-icon"><Icon name="clock" size={18} /></span>
          <h3>Login time</h3>
          <p>Trust the current login hour (±1 hour).</p>
          <button
            className="btn btn-outline"
            onClick={handleRememberLoginTime}
            disabled={loading !== ""}
          >
            {loading === "time" ? "Saving…" : "Remember login time"}
          </button>
        </div>
      </div>

    </section>
  );
}


export default SecurityFactors;
