package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignUpActivity extends AppCompatActivity {

    EditText editTextTextPersonName ,editTextTextEmailAddress ,editTextTextPassword,editTextTextPasswordConfirm;
    Button buttonSignIn ;
    TextView HaveAccount;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        editTextTextPasswordConfirm = findViewById(R.id.editTextTextPasswordConfirm);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        //for already have account
        HaveAccount = findViewById(R.id.HaveAccount);
        HaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            Toast.makeText(this, "Your are aleady Login", Toast.LENGTH_LONG).show();
            finish();
        }

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextTextEmailAddress.getText().toString().trim();
                String password = editTextTextPassword.getText().toString().trim();
                String passwordConfirm = editTextTextPasswordConfirm.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    editTextTextEmailAddress.setError(" Email is Required. ");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextTextPassword.setError(" Password is Required. ");
                    return;
                }
                if(password.length() < 6){
                    editTextTextPassword.setError("Password is must be more than 6 Characters");
                    return;
                }
                if(!password.equals(passwordConfirm)){
                    editTextTextPasswordConfirm.setError("Confirm Password is Not Same");
                    return;
                }
                progressBar.setVisibility(View.INVISIBLE);

                //create account function
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();

                            //send verification email
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignUpActivity.this, "Verification Email has been sent. ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });


    }
}