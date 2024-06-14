package com.example.magentastreaming.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.magentastreaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    Button btnStartMagenta;

//    Button btnStartAccount;
    TextInputEditText editTextEmail,editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    Button textView;

//    @SuppressLint("MissingInflatedId")
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnStartMagenta = findViewById(R.id.btnStartMagenta);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.txtEmailEdit);
        editTextPassword = findViewById(R.id.txtCreatePasswordEdit);
        buttonLogin = findViewById(R.id.btnStartMagenta);
        textView = findViewById(R.id.btnRegisterNow);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email,password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                Intent intent = new Intent(getApplicationContext(), AppHolder.class);
                startActivity(intent);


//                if(TextUtils.isEmpty(email)){
//                    Toast.makeText(Login.this,"Enter Email",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(TextUtils.isEmpty(password)){
//                    Toast.makeText(Login.this,"Enter password",Toast.LENGTH_SHORT).show();
//                    return;
//                }

//                mAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                Intent intent = new Intent(getApplicationContext(), AppHolder.class);
//                                startActivity(intent);
////                                if (task.isSuccessful()) {
////                                    Toast.makeText(Login.this, "Login Successful",
////                                            Toast.LENGTH_SHORT).show();
////                                    Intent intent = new Intent(getApplicationContext(), AppHolder.class);
////                                    startActivity(intent);
////                                    finish();
////                                } else {
////
////                                    Toast.makeText(Login.this, "Authentication failed.",
////                                            Toast.LENGTH_SHORT).show();
////
////                                }
//                            }
//                        });
            }
        });

    }
}