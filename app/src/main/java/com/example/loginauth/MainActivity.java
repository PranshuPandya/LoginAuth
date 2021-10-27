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
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText emailId, firstName, lastName,password;
    Button btnSignUp;
    TextView tvSignIn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailId=findViewById(R.id.editTextEmail);
        firstName=findViewById(R.id.editTextFirstName);
        lastName=findViewById(R.id.editTextLastName);
        password=findViewById(R.id.editTextPassword);
        btnSignUp=findViewById(R.id.signUpButton);
        tvSignIn=findViewById(R.id.textViewSignIn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase =FirebaseDatabase.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final String email = emailId.getText().toString();
            final String fName = firstName.getText().toString();
            final String lName = lastName.getText().toString();
            final String pwd = password.getText().toString();

            if(email.isEmpty()){
                emailId.setError("Please provide email id");
                emailId.requestFocus();
            }else if(fName.isEmpty()){
                firstName.setError("Please provide your first name");
                firstName.requestFocus();
            }else if(lName.isEmpty()){
                lastName.setError("Please provide your last name");
                lastName.requestFocus();
            }else if(pwd.isEmpty()){
                password.setError("Please provide password");
                password.requestFocus();
            }else if(!(email.isEmpty() || pwd.isEmpty())){
                firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            CustomToast.createToast(MainActivity.this,
                                    "SignUp Unsuccessful, Please Try Again!"
                                            +task.getException().getMessage(),true);

                        }
                        else {
                            UserDetail userDetail =new UserDetail(fName,lName);
                            String uid = task.getResult().getUser().getUid();
                            firebaseDatabase.getReference(uid).setValue(userDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(MainActivity.this,Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("name",fName+" "+lName);
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                });
            }else{
                CustomToast.createToast(MainActivity.this,"Error Occured !",true);
            }

            }

        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,SignIn.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }
}