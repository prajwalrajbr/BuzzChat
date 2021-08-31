package com.example.buzzchat3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText, codeText;
    private Button nextConfirmBtn;
    private String checker = "", phoneNumber = "";
    private LinearLayout linearLayoutPhoneNo, linearLayoutEnterCode;
    private RelativeLayout relativeLayout;
    private TextView enter6DigitCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth fAuth;
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ccp = (CountryCodePicker) findViewById(R.id.countryCodeHolder);
        phoneText = (EditText) findViewById(R.id.phoneNumber);
        ccp.registerCarrierNumberEditText(phoneText);
        codeText = (EditText) findViewById(R.id.codeText);
        nextConfirmBtn = (Button) findViewById(R.id.nextSubmitButton);
        linearLayoutPhoneNo = (LinearLayout) findViewById(R.id.enterPhoneNo);
        linearLayoutEnterCode = (LinearLayout) findViewById(R.id.enterCodeText);
        enter6DigitCode = (TextView) findViewById(R.id.enter6DigitCode);
        fAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        codeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(codeText.getText().toString().length()==6){
                    nextConfirmBtn.performClick();
                }
            }
        });

        nextConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nextConfirmBtn.getText().equals("Confirm") || checker.equals("Code Sent")){
                    String verificationCode = codeText.getText().toString();

                    if(verificationCode.equals("")){
                        Toast.makeText(LoginActivity.this,"Enter the code",Toast.LENGTH_SHORT).show();
                    }else{
                        loadingBar.setTitle("Code Verifying..");
                        loadingBar.setMessage("Please wait, while we verify the code");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }else{
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if(phoneNumber.equals("")){
                        Toast.makeText(LoginActivity.this,"Please enter a valid phone number",Toast.LENGTH_SHORT).show();
                    }else{
                        loadingBar.setTitle("Phone Number Verifying..");
                        loadingBar.setMessage("Please wait, while we verify your Phone Number");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(fAuth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);

                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(LoginActivity.this,"Invalid Phone Number...",Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(LoginActivity.this,"The SMS quota for this phone number has been exceeded",Toast.LENGTH_LONG).show();

                }

                // Show a message and update the UI
                // ...
                loadingBar.dismiss();
                linearLayoutPhoneNo.setVisibility(View.VISIBLE);


                nextConfirmBtn.setText("Next");
                linearLayoutEnterCode.setVisibility(View.GONE);
                enter6DigitCode.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                verificationID = verificationId;
                resendToken = token;

                // ...
                super.onCodeSent(verificationId, token);
                linearLayoutPhoneNo.setVisibility(View.GONE);
                checker = "Code Sent";



                nextConfirmBtn.setText("Confirm");
                linearLayoutEnterCode.setVisibility(View.VISIBLE);
                enter6DigitCode.setVisibility(View.VISIBLE);

                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this,"Code has been sent.",Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            Intent i = new Intent(LoginActivity.this, TabbedActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Phone number verified",Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this,"Successfully Logged In",Toast.LENGTH_LONG).show();
                            Intent i = new Intent(LoginActivity.this, TabbedActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            loadingBar.dismiss();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(LoginActivity.this,"Error: " + task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}