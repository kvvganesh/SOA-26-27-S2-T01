const API_BASE_URL = "http://localhost:8081";


export async function register(registerData) {

  const response = await fetch(
    `${API_BASE_URL}/users/register`,
    {
      method: "POST",

      headers: {
        "Content-Type": "application/json"
      },

      body: JSON.stringify(registerData)
    }
  );

  const data = await response.json();

  return {
    statusCode: response.status,
    data: data
  };
}


export async function enrollSecurity(securityData) {

  const response = await fetch(
    `${API_BASE_URL}/security/enroll`,
    {
      method: "POST",

      headers: {
        "Content-Type": "application/json"
      },

      body: JSON.stringify(securityData)
    }
  );

  const data = await response.text();

  return {
    statusCode: response.status,
    data: data
  };
}


export async function login(loginData) {

  const response = await fetch(
    `${API_BASE_URL}/auth/login`,
    {
      method: "POST",

      credentials: "include",

      headers: {
        "Content-Type": "application/json"
      },

      body: JSON.stringify(loginData)
    }
  );

  const data = await response.json();

  return {
    statusCode: response.status,
    data
  };
}


export async function verifyMFA(username, otp) {

  const response = await fetch(
    `${API_BASE_URL}/auth/verify-mfa`,
    {
      method: "POST",

      credentials: "include",

      headers: {
        "Content-Type": "application/json"
      },

      body: JSON.stringify({
        username,
        otp
      })
    }
  );

  const data = await response.json();

  return {
    statusCode: response.status,
    data
  };
}

export async function rememberDevice(deviceId, deviceName) {
  const response = await fetch(
    `${API_BASE_URL}/security/devices/remember`,
    {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        deviceId,
        deviceName
      })
    }
  );

  const data = await response.json().catch(() => null);

  return {
    statusCode: response.status,
    data
  };
}


export async function rememberLocation(
  latitude,
  longitude,
  radiusMeters,
  label
) {
  const response = await fetch(
    `${API_BASE_URL}/security/locations`,
    {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        latitude,
        longitude,
        radiusMeters,
        label
      })
    }
  );

  const data = await response.json().catch(() => null);

  return {
    statusCode: response.status,
    data
  };
}


export async function rememberLoginTime(
  startHour,
  endHour
) {
  const response = await fetch(
    `${API_BASE_URL}/security/login-times/remember`,
    {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        startHour,
        endHour
      })
    }
  );

  const data = await response.json().catch(() => null);

  return {
    statusCode: response.status,
    data
  };
}