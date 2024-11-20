package com.example.snifflevideocall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los botones en el diseño
        Button createMeetingButton = findViewById(R.id.btn_create_meeting);
        Button joinMeetingButton = findViewById(R.id.btn_join_meeting);

        // Acción al presionar "Crear Reunión"
        createMeetingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideoRoom.class);
            startActivity(intent); // Inicia la actividad de VideoRoom
        });

        // Acción al presionar "Unirse a Reunión"
        joinMeetingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JoinRoom.class);
            startActivity(intent); // Inicia la actividad de JoinRoom
        });
    }
}