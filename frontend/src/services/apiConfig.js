// One place for the backend address.
// Override with VITE_API_BASE_URL in a .env file if the API runs elsewhere.
export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";


// The backend answers with JSON (ErrorResponse / AuthenticationResponse) but
// a few failures (proxy errors, empty bodies) are not JSON - never let that
// throw and hide the real HTTP status.
export async function parseBody(response) {

  const text = await response.text();

  if (!text) {
    return null;
  }

  try {
    return JSON.parse(text);
  } catch {
    return { message: text };
  }
}


// Small wrapper: always sends the session cookie and returns
// { statusCode, data } like the rest of the app expects.
export async function apiRequest(path, options = {}) {

  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: "include",
    ...options,
    headers: {
      ...(options.body ? { "Content-Type": "application/json" } : {}),
      ...options.headers
    }
  });

  return {
    statusCode: response.status,
    ok: response.ok,
    data: await parseBody(response)
  };
}
