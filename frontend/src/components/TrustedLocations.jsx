import { useEffect, useState } from "react";

const API_BASE_URL =
  "http://localhost:8081";

function TrustedLocations() {

  const [locations, setLocations] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  async function loadLocations() {

    try {

      const response =
        await fetch(
          `${API_BASE_URL}/security/locations`,
          {
            method: "GET",
            credentials: "include"
          }
        );

      if (!response.ok) {
        throw new Error(
          "Unable to load locations."
        );
      }

      const data =
        await response.json();

      setLocations(data);

    } catch (error) {

      console.error(error);

      setError(
        "Unable to load trusted locations."
      );

    } finally {

      setLoading(false);
    }
  }


  async function removeLocation(
    latitude,
    longitude
  ) {

    try {

      const response =
        await fetch(
          `${API_BASE_URL}/security/locations/${latitude}/${longitude}`,
          {
            method: "DELETE",
            credentials: "include"
          }
        );

      if (!response.ok) {
        throw new Error(
          "Unable to remove location."
        );
      }

      setLocations(
        locations.filter(
          location =>
            !(
              location.latitude === latitude &&
              location.longitude === longitude
            )
        )
      );

    } catch (error) {

      console.error(error);

      setError(
        "Unable to remove location."
      );
    }
  }


  useEffect(() => {
    loadLocations();
  }, []);


  if (loading) {

    return (
      <section>

        <h2>Trusted Locations</h2>

        <p>Loading...</p>

      </section>
    );
  }


  return (
    <section>

      <h2>Trusted Locations</h2>

      {error && (
        <p>{error}</p>
      )}

      {locations.length === 0 ? (

        <p>
          No trusted locations found.
        </p>

      ) : (

        locations.map(location => (

          <div key={location.id}>

            <h3>
              {location.label ||
                "Trusted Location"}
            </h3>

            <p>
              Latitude: {location.latitude}
            </p>

            <p>
              Longitude: {location.longitude}
            </p>

            <p>
              Radius: {location.radiusMeters} meters
            </p>

            <button
              onClick={() =>
                removeLocation(
                  location.latitude,
                  location.longitude
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

export default TrustedLocations;