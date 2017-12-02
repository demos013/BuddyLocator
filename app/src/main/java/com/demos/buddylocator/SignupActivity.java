package com.demos.buddylocator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.demos.buddylocator.model.Users;
import com.demos.buddylocator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String email;
    private String password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference mUsersRef = mRootRef.child("User");
                    Users dbuser = new Users(email,"", 0,0);
                    mUsersRef.child(user.getUid()).setValue(dbuser);

                    Intent intent = new Intent(SignupActivity.this,Home.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void onClickSignup_phone(View view) {
        EditText edtemail = findViewById(R.id.email);
        EditText edtpassword    = findViewById(R.id.password);
        EditText edtconfpassword = findViewById(R.id.confirmpassword);
        email = edtemail.getText().toString();
        password = edtpassword.getText().toString();
        String repassword = edtconfpassword.getText().toString();

        if(!isEmailValid(edtemail.getText().toString())){
            edtemail.setError("example@buddylocator.com");

            Toast.makeText(SignupActivity.this, "please input correct email", Toast.LENGTH_LONG).show();
        }
        if(edtpassword.getText().toString().length()<6){
            edtpassword.setError("At least 6 characters");
            Toast.makeText(SignupActivity.this, "please input correct password", Toast.LENGTH_LONG).show();
        }
        else if(edtconfpassword.getText().toString().length()<6){
            edtconfpassword.setError("At least 6 characters");
            Toast.makeText(SignupActivity.this, "please input correct password", Toast.LENGTH_LONG).show();
        }
        else if(!edtconfpassword.getText().toString().equals(edtpassword.getText().toString())){
            edtconfpassword.setError("The password does not match. Please try again");
        }
        else{

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "The email address you have entered is already registered.\nplease input new email address.", Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });
        }





    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



}
