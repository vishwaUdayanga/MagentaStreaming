package com.example.magentastreaming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    Button btnStartMagenta;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnStartMagenta = findViewById(R.id.btnStartMagenta);

        btnStartMagenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartMagentaTapped();
            }
        });

    }

    private void btnStartMagentaTapped() {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }
}