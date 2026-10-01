import { useEffect, useState } from "react";
import Icon from "./Icons";

import { listDevices, removeDevice } from "../services/authService";


// `refreshKey` changes whenever a device is remembered elsewhere on the page,
// which makes the list reload (before, it only loaded once on mount so a newly
// remembered device never showed up until a page refresh).
function TrustedDevices({ refreshKey = 0 }) {

  const [devices, setDevices] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  useEffect(() => {

    let cancelled = false;

    listDevices()
      .then((result) => {

        if (cancelled) return;

        if (!result.ok) {
          throw new Error(result.data?.message);
        }

        setDevices(result.data ?? []);
        setError("");
      })
      .catch((err) => {

        if (cancelled) return;

        console.error(err);

        setError(
          err.message || "Unable to load trusted devices."
        );
      })
      .finally(() => {

        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };

  }, [refreshKey]);


  async function handleRemove(deviceId) {

    setError("");

    try {

      const result = await removeDevice(deviceId);

      if (!result.ok) {
        throw new Error(result.data?.message);
      }

      setDevices(
        (current) => current.filter(
          device => device.deviceId !== deviceId
        )
      );

    } catch (err) {

      console.error(err);

      setError(err.message || "Unable to remove device.");
    }
  }


  return (
    <section className="panel">

      <div className="panel-head">
        <span className="panel-icon"><Icon name="device" size={18} /></span>
        <h2>Trusted Devices</h2>
        {!loading && <span className="count">{devices.length}</span>}
      </div>

      {error && <div className="alert error" role="alert">{error}</div>}

      {loading ? (
        <p className="empty">Loading…</p>
      ) : devices.length === 0 ? (
        <p className="empty">No trusted devices found.</p>
      ) : (
        <ul className="item-list">
          {devices.map(device => (
            <li className="item" key={device.id}>
              <div className="item-body">
                <strong>{device.deviceName}</strong>
                <small title={device.deviceId}>ID · {device.deviceId}</small>
              </div>
              <button
                className="btn btn-ghost btn-danger"
                aria-label="Remove device"
                onClick={() => handleRemove(device.deviceId)}
              >
                <Icon name="trash" size={16} />
              </button>
            </li>
          ))}
        </ul>
      )}

    </section>
  );
}


export default TrustedDevices;
