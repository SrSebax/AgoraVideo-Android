package com.example.snifflevideocall;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.agora.rtc2.RtcEngine;

public class VideoRoomControls {
    private final Context context;
    private final RtcEngine rtcEngine;
    private boolean isMicMuted = false;
    private boolean isCameraOn = true;

    public VideoRoomControls(Context context, RtcEngine rtcEngine) {
        this.context = context;
        this.rtcEngine = rtcEngine;
    }

    public void toggleMic(ImageView btnMute) {
        isMicMuted = !isMicMuted;
        rtcEngine.muteLocalAudioStream(isMicMuted);
        btnMute.setImageResource(isMicMuted ? R.drawable.ic_mic_off : R.drawable.ic_mic_on);
        Toast.makeText(context, isMicMuted ? "Micrófono apagado" : "Micrófono encendido", Toast.LENGTH_SHORT).show();
    }

    public void toggleCamera(ImageView btnSwitchCamera) {
        isCameraOn = !isCameraOn;

        if (isCameraOn) {
            // Enciende la cámara
            rtcEngine.muteLocalVideoStream(false);
            rtcEngine.startPreview(); // Reinicia la vista previa

            // Oculta la pantalla negra
            View blackScreenOverlay = ((VideoRoom) context).findViewById(R.id.black_screen_overlay);
            blackScreenOverlay.setVisibility(View.GONE);  // Ocultar la pantalla negra

        } else {
            // Apaga la cámara
            rtcEngine.muteLocalVideoStream(true);
            rtcEngine.stopPreview(); // Detiene la vista previa

            // Muestra la pantalla negra
            View blackScreenOverlay = ((VideoRoom) context).findViewById(R.id.black_screen_overlay);
            blackScreenOverlay.setVisibility(View.VISIBLE);  // Mostrar la pantalla negra
        }

        btnSwitchCamera.setImageResource(isCameraOn ? R.drawable.ic_camera_on : R.drawable.ic_camera_off);
        Toast.makeText(context, isCameraOn ? "Cámara encendida" : "Cámara apagada", Toast.LENGTH_SHORT).show();
    }



    public void endCall(Context activityContext) {
        new android.app.AlertDialog.Builder(activityContext)
                .setTitle("Salir de la reunión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    rtcEngine.leaveChannel();
                    if (activityContext instanceof android.app.Activity) {
                        ((android.app.Activity) activityContext).finish(); // Salir de la actividad
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
