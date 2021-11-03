package com.example.ufs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editUsername, editPassword;
    private Button login;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private int count;
    private boolean res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            Toast.makeText(login.this, "You are already logged in!", Toast.LENGTH_LONG).show();
            Intent homeIntent = new Intent(getApplicationContext(), home.class );
            startActivity(homeIntent);
        }

        editUsername = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress_circular);
        count = 0;
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count>3){
                    //redirect to forgot password activity

                }
                String username = editUsername.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                if(username.isEmpty()){
                    editUsername.setError("user name is required!");
                    editUsername.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    editPassword.setError("password is required!");
                    editPassword.requestFocus();
                    return;
                }
                if(password.length() < 6){
                    editPassword.setError("password has to to be at least 6 characters");
                    editPassword.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String userId = mAuth.getCurrentUser().getUid();

                                    mDatabase = FirebaseDatabase.getInstance().getReference()
                                            .child("Vendor").child(userId);

                                    ValueEventListener eventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {
                                                Toast.makeText(login.this, "Vendor has been logged in successfully", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }else{
                                                Toast.makeText(login.this, "User has been logged in successfully", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                                //redirect to another activity!
                                                Intent homeIntent = new Intent(getApplicationContext(), home.class );
                                                startActivity(homeIntent);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                    };
                                    mDatabase.addListenerForSingleValueEvent(eventListener);

                                }else{
                                    count+= 1;
                                    Toast.makeText(getApplicationContext(), "Failed to log in! Try again! you have " + (3 - count) + " times left" ,Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });
    }

}