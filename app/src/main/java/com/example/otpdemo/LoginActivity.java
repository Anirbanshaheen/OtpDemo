package com.example.otpdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mCountryCode;
    private EditText mPhoneNumber;

    private Button mGenerateOtp;
    private ProgressBar progressBarLogin;

    private TextView textViewFeedBack;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCountryCode = findViewById(R.id.countryCode);
        mPhoneNumber = findViewById(R.id.phoneNumber);
        mGenerateOtp = findViewById(R.id.otpButton);
        progressBarLogin = findViewById(R.id.progressBar);
        textViewFeedBack = findViewById(R.id.feedBack);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = mCountryCode.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();

                String complete_phoneNumber = "+" + countryCode + "" + phoneNumber;

                if (countryCode.isEmpty() || phoneNumber.isEmpty()) {

                    textViewFeedBack.setText("Please fill in the form to continue");
                    textViewFeedBack.setVisibility(View.VISIBLE);
                } else {

                    progressBarLogin.setVisibility(View.VISIBLE);
                    mGenerateOtp.setEnabled(false);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(complete_phoneNumber, 60, TimeUnit.SECONDS, LoginActivity.this, mCallbacks);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                textViewFeedBack.setText("Verification failed, please try again");
                textViewFeedBack.setVisibility(View.VISIBLE);

                progressBarLogin.setVisibility(View.INVISIBLE);
                mGenerateOtp.setEnabled(true);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Intent otpIntent = new Intent(LoginActivity.this,OtpActivity.class);
                otpIntent.putExtra("AuthCredential",s);
                startActivity(otpIntent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
            // why
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        }
    }
}
