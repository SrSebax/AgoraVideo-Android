package com.example.snifflevideocall;

import android.content.Intent;
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

        // Configurar los TextWatchers para convertir el texto a mayúsculas
        pinDigit1.addTextChangedListener(new UpperCaseTextWatcher(pinDigit1));
        pinDigit2.addTextChangedListener(new UpperCaseTextWatcher(pinDigit2));
        pinDigit3.addTextChangedListener(new UpperCaseTextWatcher(pinDigit3));
        pinDigit4.addTextChangedListener(new UpperCaseTextWatcher(pinDigit4));

        joinButton.setOnClickListener(v -> {
            String pinCode = pinDigit1.getText().toString() + pinDigit2.getText().toString() +
                    pinDigit3.getText().toString() + pinDigit4.getText().toString();

            if (pinCode.length() == 4 && pinCode.matches("[A-Z0-9]+")) {
                // Si el código es válido, inicia la actividad VideoRoom pasando el código como channelName
                Intent intent = new Intent(JoinRoom.this, VideoRoom.class);
                intent.putExtra("channelName", pinCode); // Pasando el código al VideoRoom
                startActivity(intent);
                finish(); // Finaliza la actividad JoinRoom
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

    // Clase para el TextWatcher que convierte a mayúsculas
    private static class UpperCaseTextWatcher implements TextWatcher {

        private final EditText editText;

        public UpperCaseTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int after) {}

        @Override
        public void afterTextChanged(Editable editable) {
            // Convertir el texto a mayúsculas
            String currentText = editable.toString();
            if (!currentText.equals(currentText.toUpperCase())) {
                // Evitar bucles de actualización
                editText.removeTextChangedListener(this);
                editText.setText(currentText.toUpperCase());
                editText.setSelection(currentText.length());  // Coloca el cursor al final
                editText.addTextChangedListener(this);
            }
        }
    }
}
