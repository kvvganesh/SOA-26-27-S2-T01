import { apiRequest } from "./apiConfig";


function post(path, body) {

  return apiRequest(path, {
    method: "POST",
    body: JSON.stringify(body)
  });
}


// =========================================================
// REGISTRATION / LOGIN
// =========================================================

export function register(registerData) {
  return post("/users/register", registerData);
}


export function enrollSecurity(securityData) {
  // Success is a plain-text message, failures are JSON. apiRequest
  // normalises both into { data: { message } }.
  return post("/security/enroll", securityData);
}


export function login(loginData) {
  return post("/auth/login", loginData);
}


export function verifyMFA(username, otp) {
  return post("/auth/verify-mfa", { username, otp });
}


export function logout() {
  return post("/auth/logout", {});
}


// =========================================================
// REMEMBER (needs a recent successful sign-in)
// =========================================================

export function rememberDevice(deviceId, deviceName) {
  return post("/security/devices/remember", { deviceId, deviceName });
}


export function rememberLocation(
  latitude,
  longitude,
  radiusMeters,
  label,
  accuracy
) {
  return post("/security/locations", {
    latitude,
    longitude,
    radiusMeters,
    label,
    accuracy
  });
}


export function rememberLoginTime(startHour, endHour) {
  return post("/security/login-times/remember", { startHour, endHour });
}


// =========================================================
// LIST / REMOVE
// =========================================================

export function listDevices() {
  return apiRequest("/security/devices");
}

export function removeDevice(deviceId) {
  return apiRequest(
    `/security/devices/${encodeURIComponent(deviceId)}`,
    { method: "DELETE" }
  );
}


export function listLocations() {
  return apiRequest("/security/locations");
}

// Locations are removed by their id (not by coordinates).
export function removeLocation(id) {
  return apiRequest(`/security/locations/${id}`, { method: "DELETE" });
}


// The signed-in user's OWN audit trail (newest first, max 100).
export function listMyAuditLogs(limit = 50) {
  return apiRequest(`/security/my-audit-logs?limit=${limit}`);
}


export function listLoginTimes() {
  return apiRequest("/security/login-times");
}

export function removeLoginTime(id) {
  return apiRequest(`/security/login-times/${id}`, { method: "DELETE" });
}
