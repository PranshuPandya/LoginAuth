package com.example.loginauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginauth.model.UserDetail;
import com.example.loginauth.util.CustomToast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    EditText emailId,password;
    Button btnSignIn;
    TextView tvfSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailId = findViewById(R.id.editTextEmailSignIn);
        password = findViewById(R.id.editTextPasswordSignIn);
        btnSignIn = findViewById(R.id.signInButton);
        tvfSignUp = findViewById(R.id.textViewSignUp);

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser!=null){
                    moveToHomeActivity(firebaseUser);
                }else{
                    CustomToast.createToast(SignIn.this,"Please Login",true);

                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please provide email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please provide password");
                    password.requestFocus();
                }else {
                    firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                CustomToast.createToast(SignIn.this,"SignIn Unsuccessful, Please Try Again!"+task.getException().getMessage(),true);

                            }
                            else {
                               moveToHomeActivity(task.getResult().getUser());
                            }
                        }
                    });
                }
            }
        });
        tvfSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intSignUp=new Intent(SignIn.this,MainActivity.class);
                intSignUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intSignUp);
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    private void moveToHomeActivity(FirebaseUser firebaseUser){
        firebaseDatabase.getReference().child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDetail userDetail = snapshot.getValue(UserDetail.class);
                        String name = userDetail.getFirstName()+" "+userDetail.getLastName();
                        Intent i = new Intent(getApplicationContext(), Home.class);
                        CustomToast.createToast(getApplicationContext(),"Login Successful",false);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("name",name);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}