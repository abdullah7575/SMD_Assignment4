package com.example.smd_assignment_4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    TextInputEditText tietEmail, tietPassword;
    Button btnSignUp;
    TextView tvLogin, tvForgetPassword;
    FirebaseAuth auth;
//    FirebaseUser user;
//    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tietEmail.getText().toString().trim();
                String password = tietEmail.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignUp.this,"Something is missing",Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String userId = auth.getCurrentUser().getUid();
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("email",email);
                                // adding user details in firestore
//                                firestore.collection("users")
//                                        .document(userId)
//                                        .set(data)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if(task.isSuccessful()){
//                                                    Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
//                                                    startActivity(new Intent(SignUp.this,Login.class));
//                                                    finish();
//                                                }
//                                                else{
//                                                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
                                Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this,Login.class));
                                finish();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void init(){
        tietEmail = findViewById(R.id.tietEmail);
        tietPassword = findViewById(R.id.tietPassword);
        btnSignUp = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        auth = FirebaseAuth.getInstance();
//        user = auth.getCurrentUser();  // now make check user!=null
        // now we will get it after creating user and then getting its uID in successListener
//        firestore = FirebaseFirestore.getInstance();
    }
}