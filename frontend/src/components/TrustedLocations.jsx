import { useEffect, useState } from "react";
import Icon from "./Icons";

import { listLocations, removeLocation } from "../services/authService";


function TrustedLocations({ refreshKey = 0 }) {

  const [locations, setLocations] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  useEffect(() => {

    let cancelled = false;

    listLocations()
      .then((result) => {

        if (cancelled) return;

        if (!result.ok) {
          throw new Error(result.data?.message);
        }

        setLocations(result.data ?? []);
        setError("");
      })
      .catch((err) => {

        if (cancelled) return;

        console.error(err);

        setError(
          err.message || "Unable to load trusted locations."
        );
      })
      .finally(() => {

        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };

  }, [refreshKey]);


  // Removes by id: the old coordinate based call deleted the first
  // overlapping location, which was not always the one that was clicked.
  async function handleRemove(id) {

    setError("");

    try {

      const result = await removeLocation(id);

      if (!result.ok) {
        throw new Error(result.data?.message);
      }

      setLocations(
        (current) => current.filter(
          location => location.id !== id
        )
      );

    } catch (err) {

      console.error(err);

      setError(err.message || "Unable to remove location.");
    }
  }


  return (
    <section className="panel">

      <div className="panel-head">
        <span className="panel-icon"><Icon name="pin" size={18} /></span>
        <h2>Trusted Locations</h2>
        {!loading && <span className="count">{locations.length}</span>}
      </div>

      {error && <div className="alert error" role="alert">{error}</div>}

      {loading ? (
        <p className="empty">Loading…</p>
      ) : locations.length === 0 ? (
        <p className="empty">No trusted locations found.</p>
      ) : (
        <ul className="item-list">
          {locations.map(location => (
            <li className="item" key={location.id}>
              <div className="item-body">
                <strong>{location.label || "Trusted Location"}</strong>
                <small>
                  {Number(location.latitude).toFixed(4)}, {Number(location.longitude).toFixed(4)}
                  {" · "}{Math.round(location.radiusMeters)} m radius
                </small>
              </div>
              <button
                className="btn btn-ghost btn-danger"
                aria-label="Remove location"
                onClick={() => handleRemove(location.id)}
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


export default TrustedLocations;
