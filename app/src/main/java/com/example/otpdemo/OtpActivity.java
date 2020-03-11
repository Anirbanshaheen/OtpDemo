package com.example.otpdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String mAuthVerificationId;

    private EditText mOtpText;
    private Button mVerifyOtpButton;
    private ProgressBar progressBarOtp;
    private TextView textViewOtpFeedBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mAuthVerificationId = getIntent().getStringExtra("AuthCredential");

        textViewOtpFeedBack = findViewById(R.id.otp_feedBack);
        progressBarOtp = findViewById(R.id.progressBarOtp);
        mOtpText = findViewById(R.id.editTextOtp);

        mVerifyOtpButton = findViewById(R.id.VerifyOtp);

        mVerifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpText = mOtpText.getText().toString();

                if (otpText.isEmpty()) {


                    textViewOtpFeedBack.setVisibility(View.VISIBLE);
                    textViewOtpFeedBack.setText("Please fill in the form and try again");
                } else {

                    progressBarOtp.setVisibility(View.VISIBLE);
                    mVerifyOtpButton.setEnabled(false);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId, otpText);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendUserHome();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                textViewOtpFeedBack.setVisibility(View.VISIBLE);
                                textViewOtpFeedBack.setText("There was an error");
                            }
                        }

                        progressBarOtp.setVisibility(View.INVISIBLE);
                        mVerifyOtpButton.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendUserHome();
        }
    }


    public void sendUserHome() {
        Intent homeIntent = new Intent(OtpActivity.this, MainActivity.class);
        // why
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}
