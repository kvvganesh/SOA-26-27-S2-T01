import { useEffect, useState } from "react";

const API_BASE_URL =
  "http://localhost:8081";

function TrustedLoginTimes() {

  const [times, setTimes] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");


  async function loadTimes() {

    try {

      const response =
        await fetch(
          `${API_BASE_URL}/security/login-times`,
          {
            method: "GET",
            credentials: "include"
          }
        );

      if (!response.ok) {
        throw new Error(
          "Unable to load login times."
        );
      }

      const data =
        await response.json();

      setTimes(data);

    } catch (error) {

      console.error(error);

      setError(
        "Unable to load trusted login times."
      );

    } finally {

      setLoading(false);
    }
  }


  async function removeTime(id) {

    try {

      const response =
        await fetch(
          `${API_BASE_URL}/security/login-times/${id}`,
          {
            method: "DELETE",
            credentials: "include"
          }
        );

      if (!response.ok) {
        throw new Error(
          "Unable to remove login time."
        );
      }

      setTimes(
        times.filter(
          time => time.id !== id
        )
      );

    } catch (error) {

      console.error(error);

      setError(
        "Unable to remove login time."
      );
    }
  }


  useEffect(() => {
    loadTimes();
  }, []);


  if (loading) {

    return (
      <section>

        <h2>Trusted Login Times</h2>

        <p>Loading...</p>

      </section>
    );
  }


  return (
    <section>

      <h2>Trusted Login Times</h2>

      {error && (
        <p>{error}</p>
      )}

      {times.length === 0 ? (

        <p>
          No trusted login times found.
        </p>

      ) : (

        times.map(time => (

          <div key={time.id}>

            <h3>
              Trusted Time Window
            </h3>

            <p>
              Start: {time.startHour}:00
            </p>

            <p>
              End: {time.endHour}:00
            </p>

            <button
              onClick={() =>
                removeTime(time.id)
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

export default TrustedLoginTimes;