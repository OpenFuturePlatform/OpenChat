// Basic logic for controls
const muteButton = document.getElementById("mute");
const cameraButton = document.getElementById("camera");
const endCallButton = document.getElementById("endCall");

let isMuted = false;
let isCameraOn = true;

muteButton.addEventListener("click", () => {
    isMuted = !isMuted;
    muteButton.textContent = isMuted ? "🔇" : "🎤";
});

cameraButton.addEventListener("click", () => {
    isCameraOn = !isCameraOn;
    cameraButton.textContent = isCameraOn ? "📷" : "📴";
});

endCallButton.addEventListener("click", () => {
    alert("Call ended");
    // Add logic to terminate the call
});
