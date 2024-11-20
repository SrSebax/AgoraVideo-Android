package com.example.snifflevideocall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;

public class VideoRoom extends AppCompatActivity {
    private String appId = "efc3a59a74ba4640a4c39745320fb06f"; // Reemplaza con tu App ID
    private String channelName;
    private String token = null;

    private RtcEngine mRtcEngine;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            runOnUiThread(() -> {
                Toast.makeText(VideoRoom.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(() -> Toast.makeText(VideoRoom.this, "User offline: " + uid, Toast.LENGTH_SHORT).show());
        }
    };

    private void initializeAndJoinChannel() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;

            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error: " + e.getMessage());
        }

        mRtcEngine.enableVideo();
        mRtcEngine.startPreview();

        FrameLayout localContainer = findViewById(R.id.local_video_view_container);
        SurfaceView localSurfaceView = new SurfaceView(getBaseContext());
        localContainer.addView(localSurfaceView);

        mRtcEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        ChannelMediaOptions options = new ChannelMediaOptions();
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;

        mRtcEngine.joinChannel(token, channelName, 0, options);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout remoteContainer = findViewById(R.id.remote_video_view_container);
        SurfaceView remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        remoteContainer.addView(remoteSurfaceView);

        mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private String[] getRequiredPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.BLUETOOTH_CONNECT
            };
        } else {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };
        }
    }

    private boolean checkPermissions() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermissions()) {
            initializeAndJoinChannel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_room);

        // Generar un nombre de canal aleatorio de 4 dígitos con letras y números
        channelName = generateRandomChannelName();
        TextView tvRoomInfo = findViewById(R.id.tv_room_info);
        tvRoomInfo.setText("# SALA: " + channelName);

        if (checkPermissions()) {
            initializeAndJoinChannel();
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), 22);
        }

        // Crear instancia de VideoRoomControls
        VideoRoomControls controls = new VideoRoomControls(this, mRtcEngine);

        // Configuración de botones
        ImageView btnMute = findViewById(R.id.btn_mute);
        ImageView btnSwitchCamera = findViewById(R.id.btn_switch_camera);
        ImageView btnEndCall = findViewById(R.id.btn_end_call);

        // Agregar listeners
        btnMute.setOnClickListener(v -> controls.toggleMic(btnMute));
        btnSwitchCamera.setOnClickListener(v -> controls.toggleCamera(btnSwitchCamera));
        btnEndCall.setOnClickListener(v -> controls.endCall(VideoRoom.this));
    }


    private String generateRandomChannelName() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder channelName = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = (int) (Math.random() * characters.length());
            channelName.append(characters.charAt(index));
        }
        return channelName.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null) {
            mRtcEngine.stopPreview();
            mRtcEngine.leaveChannel();
        }
    }
}