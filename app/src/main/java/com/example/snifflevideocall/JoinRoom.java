package com.example.snifflevideocall;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JoinRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        EditText pinDigit1 = findViewById(R.id.pin_digit_1);
        EditText pinDigit2 = findViewById(R.id.pin_digit_2);
        EditText pinDigit3 = findViewById(R.id.pin_digit_3);
        EditText pinDigit4 = findViewById(R.id.pin_digit_4);
        Button joinButton = findViewById(R.id.btn_join);

        // Configurar los TextWatchers para mover el foco entre los campos
        pinDigit1.addTextChangedListener(new PinTextWatcher(pinDigit2));
        pinDigit2.addTextChangedListener(new PinTextWatcher(pinDigit3));
        pinDigit3.addTextChangedListener(new PinTextWatcher(pinDigit4));

        joinButton.setOnClickListener(v -> {
            String pinCode = pinDigit1.getText().toString() + pinDigit2.getText().toString() +
                    pinDigit3.getText().toString() + pinDigit4.getText().toString();

            if (pinCode.length() == 4 && pinCode.matches("[A-Z0-9]+")) {
                // Lógica para unirse a la sala
                Toast.makeText(JoinRoom.this, "Uniéndose a la sala: " + pinCode, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(JoinRoom.this, "Por favor, ingrese un código válido de 4 caracteres.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Clase para el TextWatcher que mueve el foco entre campos
    private static class PinTextWatcher implements TextWatcher {

        private final EditText nextEditText;

        public PinTextWatcher(EditText nextEditText) {
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
            if (charSequence.length() == 1) {
                nextEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }
}
