package com.example.smd_assignment_4;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText tietEmail, tietPassword;
    Button btnLogin;
    TextView tvSignup, tvForgetPassword;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        if(user!=null){
            Intent i = new Intent(Login.this,ShoppingList.class);
            startActivity(i);
            finish();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tietEmail.getText().toString().trim();
                String password = tietPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this,"Something is missing",Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.show();
                auth.signInWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.dismiss();
                                startActivity(new Intent(Login.this,ShoppingList.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        });
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etYourEmail = new EditText(view.getContext());
                AlertDialog.Builder forgotPasswordDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Enter Email to reset Password")
                        .setView(etYourEmail)
                        .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String yourEmail = etYourEmail.getText().toString().trim();
                                if (yourEmail.isEmpty()) {
                                    etYourEmail.setError("Provide valid Email address");
                                } else {
                                    ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                                    progressDialog.show();
                                    auth.sendPasswordResetEmail(yourEmail)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(Login.this, "Check Your Mail Inbox", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                forgotPasswordDialog.show();
            }
        });

//        setOnClickListenerToSignUp();
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,SignUp.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void init(){
        tietEmail = findViewById(R.id.tietEmail);
        tietPassword = findViewById(R.id.tietPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();  // now make check user!=null
    }
}
