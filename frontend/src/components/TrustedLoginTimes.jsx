import { useEffect, useState } from "react";
import Icon from "./Icons";

import { listLoginTimes, removeLoginTime } from "../services/authService";


function TrustedLoginTimes({ refreshKey = 0 }) {

  const [times, setTimes] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  useEffect(() => {

    let cancelled = false;

    listLoginTimes()
      .then((result) => {

        if (cancelled) return;

        if (!result.ok) {
          throw new Error(result.data?.message);
        }

        setTimes(result.data ?? []);
        setError("");
      })
      .catch((err) => {

        if (cancelled) return;

        console.error(err);

        setError(
          err.message || "Unable to load trusted login times."
        );
      })
      .finally(() => {

        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };

  }, [refreshKey]);


  async function handleRemove(id) {

    setError("");

    try {

      const result = await removeLoginTime(id);

      if (!result.ok) {
        throw new Error(result.data?.message);
      }

      setTimes(
        (current) => current.filter(
          time => time.id !== id
        )
      );

    } catch (err) {

      console.error(err);

      setError(err.message || "Unable to remove login time.");
    }
  }


  const pad = (hour) => String(hour).padStart(2, "0") + ":00";

  return (
    <section className="panel">

      <div className="panel-head">
        <span className="panel-icon"><Icon name="clock" size={18} /></span>
        <h2>Trusted Login Times</h2>
        {!loading && <span className="count">{times.length}</span>}
      </div>

      {error && <div className="alert error" role="alert">{error}</div>}

      {loading ? (
        <p className="empty">Loading…</p>
      ) : times.length === 0 ? (
        <p className="empty">No trusted login times found.</p>
      ) : (
        <ul className="item-list">
          {times.map(time => (
            <li className="item" key={time.id}>
              <div className="item-body">
                <strong>{pad(time.startHour)} – {pad(time.endHour)}</strong>
                <small>
                  {time.startHour > time.endHour
                    ? "Trusted time window (overnight)"
                    : "Trusted time window"}
                </small>
              </div>
              <button
                className="btn btn-ghost btn-danger"
                aria-label="Remove login time"
                onClick={() => handleRemove(time.id)}
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


export default TrustedLoginTimes;
