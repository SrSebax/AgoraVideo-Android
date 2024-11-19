package com.example.snifflevideocall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.FrameLayout;
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

public class MainActivity extends AppCompatActivity {
    // Fill in the App ID obtained from the Agora Console
    private String appId = "efc3a59a74ba4640a4c39745320fb06f";
    // Fill in the channel name
    private String channelName = "12345";
    // Fill in the temporary token generated from Agora Console
    private String token = null;

    private RtcEngine mRtcEngine;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        // Callback when successfully joining the channel
        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Join channel success", Toast.LENGTH_SHORT).show();
            });
        }

        // Callback when a remote user or host joins the current channel
        @Override
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> {
                // When a remote user joins the channel, display the remote video stream for the specified uid
                setupRemoteVideo(uid);
            });
        }

        // Callback when a remote user or host leaves the current channel
        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "User offline: " + uid, Toast.LENGTH_SHORT).show();
            });
        }

    };

    private void initializeAndJoinChannel() {
        try {
            // Create an RtcEngineConfig instance and configure it
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            // Create and initialize an RtcEngine instance
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
        // Enable the video module
        mRtcEngine.enableVideo();

        // Enable local preview
        mRtcEngine.startPreview();

        // Create a SurfaceView object and make it a child object of FrameLayout
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        container.addView(surfaceView);
        // Pass the SurfaceView object to the SDK and set the local view
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        // Create an instance of ChannelMediaOptions and configure it
        ChannelMediaOptions options = new ChannelMediaOptions();
        // Set the user role to BROADCASTER or AUDIENCE according to the scenario
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // In the video calling scenario, set the channel profile to CHANNEL_PROFILE_COMMUNICATION
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;

        // Join the channel using a temporary token and channel name, setting uid to 0 means the engine will randomly generate a username
        // The onJoinChannelSuccess callback will be triggered upon success
        mRtcEngine.joinChannel(token, channelName, 0, options);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        // Pass the SurfaceView object to the SDK and set the remote view
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private static final int PERMISSION_REQ_ID = 22;

    // Obtain recording, camera and other permissions required to implement real-time audio and video interaction
    private String[] getRequiredPermissions(){
        // Determine the permissions required when targetSDKVersion is 31 or above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO, // Recording permission
                    Manifest.permission.CAMERA, // Camera permission
                    Manifest.permission.READ_PHONE_STATE, // Permission to read phone status
                    Manifest.permission.BLUETOOTH_CONNECT // Bluetooth connection permission
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
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // System permission request callback
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
        setContentView(R.layout.activity_main);
        // If authorized, initialize RtcEngine and join the channel
        if (checkPermissions()) {
            initializeAndJoinChannel();
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSION_REQ_ID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop local video preview
        mRtcEngine.stopPreview();
        // Leave the channel
        mRtcEngine.leaveChannel();
    }
}