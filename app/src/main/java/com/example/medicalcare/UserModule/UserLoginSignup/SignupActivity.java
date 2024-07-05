package com.example.medicalcare.UserModule.UserLoginSignup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.medicalcare.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.aviran.cookiebar2.CookieBar;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {
    EditText firstname,mobile,email;
    TextInputEditText password,confirmpassword;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    DatabaseReference ref = firebaseDatabase.getReference();
//********
    private FirebaseAuth mAuth;
    private String verificationId;


    boolean ValidateFaild=false,passconfirm=true;
    LinearLayout signupbtn;
    TextView bacttologin;
    String name,mobilenumber,emailid,userpassword;

    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    OnBackPressedDispatcher onBackPressedDispatcher;
    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);
        onBackPressedDispatcher = this.getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                finish();

            }
        });



        firstname=findViewById(R.id.firstname);
        mobile=findViewById(R.id.mobile);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirmpassword=findViewById(R.id.confirmpassword);

        signupbtn=findViewById(R.id.signup);
        bacttologin=findViewById(R.id.backtologin);

        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        editor = preferences.edit();

        //initialize the authentication
        mAuth = FirebaseAuth.getInstance();


        //set onlick event for the signup button
        signupbtn.setOnClickListener(view -> {
            passconfirm=true;
            progressDialog();

            //validating the all the field in the signup page
            if(TextUtils.isEmpty(firstname.getText().toString())&&TextUtils.isEmpty(mobile.getText().toString())&&TextUtils.isEmpty(email.getText().toString())&&TextUtils.isEmpty(Objects.requireNonNull(password.getText()).toString())&&TextUtils.isEmpty(Objects.requireNonNull(confirmpassword.getText()).toString())){

                //show the toast message if all field are empty
                Toast.makeText(SignupActivity.this, "Fill all the field to sign up the account", Toast.LENGTH_LONG).show();
                alertDialog.cancel();
            }else{

                //if firstname field is empty ,it shows error message in the field
                if(TextUtils.isEmpty(firstname.getText().toString())){
                    firstname.setError("field is required");
                    ValidateFaild=true;
                    alertDialog.cancel();
                }


                //if mobile number field is empty ,it shows error message in the field
                if (TextUtils.isEmpty(mobile.getText().toString())) {

                    mobile.setError("field is required");
                    ValidateFaild=true;
                    alertDialog.cancel();
                }else if (mobile.getText().toString().length()!=10){

                    //if mobile number is below 10 digits it shows the error message
                    mobile.setError("mobile number must be 10 digits");
                    ValidateFaild=true;
                    alertDialog.cancel();

                } else {
                    mobilenumber="+91"+mobile.getText().toString();
                }

                //if email field is empty ,it shows error message in the field
                if (TextUtils.isEmpty(email.getText().toString())) {

                    email.setError("field is required");
                    ValidateFaild=true;
                    alertDialog.cancel();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setError("enter the valid email address");
                    ValidateFaild=true;
                    alertDialog.cancel();
                }

                //if password field is empty ,it shows error message in the field
                if (TextUtils.isEmpty(Objects.requireNonNull(password.getText()).toString())) {

                    password.setError("field is required");
                    ValidateFaild=true;
                    passconfirm=false;
                    alertDialog.cancel();

                }

                //if confirm password field is empty ,it shows error message in the field
                if (TextUtils.isEmpty(Objects.requireNonNull(confirmpassword.getText()).toString())) {

                    confirmpassword.setError("field is required");
                    ValidateFaild=true;
                    alertDialog.cancel();

                }

                //if password and confirm passaword is mismatched it shows the error message in confirm password field
                if (!password.getText().toString().equals(confirmpassword.getText().toString())&& passconfirm){
                    confirmpassword.setError("password mismatched");
                    ValidateFaild=true;
                    alertDialog.cancel();
                }






            }
            //here we check any validation error is occured or not
            if (!ValidateFaild&&passconfirm){

                Toast.makeText(SignupActivity.this, "validation succcess", Toast.LENGTH_SHORT).show();
               sendVerificationCode(mobilenumber);

            }


        });


    }
    private void sendVerificationCode(String phoneNumber) {
        if (phoneNumber!=null){
            alertDialog.cancel();
            showInputDialog();
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phoneNumber) // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this) // (optional) Activity for callback binding
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    signInWithPhoneAuthCredential(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(SignupActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    verificationId = s;
                                }
                            }).build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        }



    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Verification successful, continue with user registration
                        Toast.makeText(SignupActivity.this, "Phone number verified successfully", Toast.LENGTH_SHORT).show();
                        uploaduserdata();
                    } else {
                        Toast.makeText(SignupActivity.this, "Verification failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showInputDialog() {
        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Text");

        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.getotp, null);
        builder.setView(dialogView);

        // Reference to the EditText in the dialog layout
        final EditText enteredotp = dialogView.findViewById(R.id.otpfield);

        // Set positive button (Verify)
        builder.setPositiveButton("Verify", (dialog, which) -> {

            String userInput = enteredotp.getText().toString();
            verifyVerificationCode(userInput);
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void uploaduserdata() {

        name=firstname.getText().toString();
        emailid=email.getText().toString();
        userpassword= Objects.requireNonNull(password.getText()).toString();

        UploadUserDetails upload=new UploadUserDetails(name,mobilenumber,emailid,userpassword);
        ref.child("UserDetails").child(mobilenumber).setValue(upload);
        editor.putString("profile","1");
        editor.apply();
        startActivity(new Intent(SignupActivity.this, MainActivity.class));

    }

    public void progressDialog(){

        builder=new AlertDialog.Builder(this,R.style.TransparentAlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.progress_dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.progressdialog);

        // Set the progress tint color
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.formal), android.graphics.PorterDuff.Mode.SRC_IN);

        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister NetworkChangeReceiver
        unregisterReceiver(networkChangeReceiver);
    }



    private class NetworkChangeReceiver extends BroadcastReceiver {
        private boolean wasConnected = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = isNetworkConnected(context);

            if (isConnected != wasConnected) {
                if (isConnected) {
                    CookieBar.build((Activity) context)
                            .setTitle("Network Status")
                            .setMessage("Back to online")
                            .show();
                } else {
                    CookieBar.build((Activity) context)
                            .setTitle("Network Status")
                            .setMessage("No internet Connection")
                            .show();
                }
                wasConnected = isConnected;
            }
        }
    }

    // Method to check network connectivity
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


}