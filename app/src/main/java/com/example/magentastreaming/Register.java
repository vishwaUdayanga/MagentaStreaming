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

public class Register extends AppCompatActivity {

    Button btnStartAccount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnStartAccount = findViewById(R.id.btnStartAccount);

        btnStartAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartAccountTapped();
            }
        });
    }

    private void btnStartAccountTapped() {
        Intent intent = new Intent(getApplicationContext(), AppHolder.class);
        startActivity(intent);
    }
}