export function getDeviceInfo(){

return {
userAgent: navigator.userAgent,
platform: navigator.platform,
language: navigator.language,
screenWidth: window.screen.width,
screenHeight: window.screen.height
};
}

export function getDeviceId(){
    const STORAGE_KEY= "adaptive_mfa_device_id";

    let deviceId= localStorage.getItem(STORAGE_KEY);
    if(!deviceId){
        deviceId= crypto.randomUUID();

        localStorage.setItem(
        STORAGE_KEY,
        deviceId
        );
    }
    return deviceId;
}