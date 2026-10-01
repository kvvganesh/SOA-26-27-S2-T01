// Error with a machine-readable code and a message that is safe to show.
export class LocationError extends Error {

  constructor(code, message) {
    super(message);
    this.name = "LocationError";
    this.code = code;   // UNSUPPORTED | DENIED | UNAVAILABLE | TIMEOUT
  }
}


function toLocationError(error) {

  // GeolocationPositionError: 1 = denied, 2 = unavailable, 3 = timeout
  switch (error?.code) {

    case 1:
      return new LocationError(
        "DENIED",
        "Location permission was denied. Allow location access for this site to use location-based trust."
      );

    case 2:
      return new LocationError(
        "UNAVAILABLE",
        "Your device could not determine its location."
      );

    case 3:
      return new LocationError(
        "TIMEOUT",
        "Finding your location took too long."
      );

    default:
      return new LocationError(
        "UNAVAILABLE",
        "Unable to determine your location."
      );
  }
}


function requestPosition(options) {

  return new Promise((resolve, reject) => {

    navigator.geolocation.getCurrentPosition(
      (position) => resolve({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        accuracy: position.coords.accuracy
      }),
      (error) => reject(toLocationError(error)),
      options
    );
  });
}


// Resolves { latitude, longitude, accuracy } or rejects with a LocationError.
//
// High-accuracy mode is tried first; on laptops / desktops it often times
// out or is unavailable, so we retry once in normal mode before giving up.
// (Permission denied is final - retrying would just ask again.)
export async function getCurrentLocation() {

  if (!("geolocation" in navigator)) {
    throw new LocationError(
      "UNSUPPORTED",
      "Geolocation is not supported by this browser."
    );
  }

  try {

    return await requestPosition({
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    });

  } catch (error) {

    if (error.code === "DENIED") {
      throw error;
    }

    return requestPosition({
      enableHighAccuracy: false,
      timeout: 10000,
      maximumAge: 30000
    });
  }
}


// Best-effort place name for a label. Never throws - returns null when the
// lookup fails so callers can fall back to a generic label.
export async function getLocationName(latitude, longitude) {

  const controller = new AbortController();
  const timer = setTimeout(() => controller.abort(), 4000);

  try {

    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${latitude}&lon=${longitude}`,
      { signal: controller.signal }
    );

    if (!response.ok) {
      return null;
    }

    const data = await response.json();

    const address = data?.address;

    return (
      address?.city ||
      address?.town ||
      address?.village ||
      address?.municipality ||
      address?.county ||
      null
    );

  } catch {

    return null;

  } finally {

    clearTimeout(timer);
  }
}
