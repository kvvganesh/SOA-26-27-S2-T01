export function getCurrentLocation(){
    return new Promise((resolve, reject)=> {
        if (!navigator.geolocation){
            reject(
             new Error("Geolocation is not supported by this browser.")
             );
             return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) =>{
                resolve({
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude,
                    accuracy: position.coords.accuracy
                        });
                    },

                    (error)=>{
                        reject(error);
                    }
                );
    });

}

export async function getLocationName(latitude, longitude) {
  const response = await fetch(
    `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${latitude}&lon=${longitude}`
  );

  if (!response.ok) {
    throw new Error("Unable to resolve location name.");
  }

  const data = await response.json();

  const address = data.address;

  return (
    address.city ||
    address.town ||
    address.village ||
    address.municipality ||
    address.county ||
    "Unknown"
  );
}