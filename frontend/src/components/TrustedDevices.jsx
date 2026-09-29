import { useEffect, useState } from "react";

const API_BASE_URL =
  "http://localhost:8081";

function TrustedDevices() {

  const [devices, setDevices] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  async function loadDevices() {

    try {

      const response =
        await fetch(
          `${API_BASE_URL}/security/devices`,
          {
            method: "GET",
            credentials: "include"
          }
        );

      if (!response.ok) {
        throw new Error(
          "Unable to load trusted devices."
        );
      }

      const data =
        await response.json();

      setDevices(data);

    } catch (error) {

      console.error(error);

      setError(
        "Unable to load trusted devices."
      );

    } finally {

      setLoading(false);
    }
  }


  async function removeDevice(deviceId) {

    try {

      const response =
        await fetch(
          `${API_BASE_URL}/security/devices/${encodeURIComponent(deviceId)}`,
          {
            method: "DELETE",
            credentials: "include"
          }
        );

      if (!response.ok) {
        throw new Error(
          "Unable to remove device."
        );
      }

      setDevices(
        devices.filter(
          device =>
            device.deviceId !== deviceId
        )
      );

    } catch (error) {

      console.error(error);

      setError(
        "Unable to remove device."
      );
    }
  }


  useEffect(() => {
    loadDevices();
  }, []);


  if (loading) {
    return (
      <section>
        <h2>Trusted Devices</h2>
        <p>Loading...</p>
      </section>
    );
  }


  return (
    <section>

      <h2>Trusted Devices</h2>

      {error && (
        <p>{error}</p>
      )}

      {devices.length === 0 ? (

        <p>
          No trusted devices found.
        </p>

      ) : (

        devices.map(device => (

          <div key={device.id}>

            <h3>
              {device.deviceName}
            </h3>

            <p>
              Device ID: {device.deviceId}
            </p>

            <button
              onClick={() =>
                removeDevice(
                  device.deviceId
                )
              }
            >
              Remove
            </button>

          </div>

        ))
      )}

    </section>
  );
}

export default TrustedDevices;