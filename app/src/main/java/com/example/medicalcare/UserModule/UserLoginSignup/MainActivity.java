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
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.UserModule.BookingAppointment.BookingDoctor;
import com.example.medicalcare.UserModule.UserFragments.HomePage;
import com.example.medicalcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.aviran.cookiebar2.CookieBar;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText mobile,password;
    TextView createacc,forgotPassword;
    LinearLayout signinbtn;
    String mobilenumber,userpassword;
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("UserDetails");

    boolean verification=true;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    BottomSheetDialog bottomSheetDialog;
    private FirebaseAuth mAuth;

    EditText bottomSheetMobileNumber,newPassword,confirmPassword;
    Button sentOTP,changepassword;

    LinearLayout mobileNumberLayout,passwordLayout;
    private String verificationId;

    String phoneNumberContainer;
    boolean passwordVerified=true;

    OnBackPressedDispatcher onBackPressedDispatcher;

    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);


        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);
        onBackPressedDispatcher = this.getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();

            }
        });

        setContentView(R.layout.activity_main);
        mobile=findViewById(R.id.mobile);
        password=findViewById(R.id.password);
        signinbtn=findViewById(R.id.signin);
        createacc=findViewById(R.id.createaccount);
        forgotPassword=findViewById(R.id.forgotpassword);
        SharedPreferences preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        mAuth = FirebaseAuth.getInstance();




        //set a onclick event for login button
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog();
                mobilenumber=mobile.getText().toString();
                userpassword=password.getText().toString();

                if (!mobilenumber.isEmpty()&&!userpassword.isEmpty()) {
                    if (mobilenumber.length() != 10) {
                        mobile.setError("mobile number must be 10 digits");
                        verification = false;
                        alertDialog.cancel();
                    } else {
                        mobilenumber = "+91" + mobilenumber;
                    }

                    if (userpassword.length() < 8) {
                        password.setError("password contains atleast 8 characters");
                        verification = false;
                        alertDialog.cancel();
                    }
                    if (verification)
                    {

                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(mobilenumber)) {
                                    String retriveName=snapshot.child(mobilenumber).child("name").getValue(String.class);
                                    String retrivepassword = snapshot.child(mobilenumber).child("userpassword").getValue(String.class);
                                    if (TextUtils.equals(userpassword, retrivepassword)) {
                                        editor.putString("name",retriveName);
                                        editor.putString("UserMobile",mobilenumber);
                                        editor.apply();
                                        alertDialog.cancel();
                                        getToken();
                                    }


                                }else {
                                    Toast.makeText(MainActivity.this, "create account", Toast.LENGTH_SHORT).show();
                                    alertDialog.cancel();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    if (mobilenumber.isEmpty()){
                        mobile.setError("enter the mobile number");
                    }
                    if (userpassword.isEmpty()){
                        password.setError("enter the password");
                    }
                    alertDialog.cancel();
                }

            }
            

        });
        //now set a onclick listener for create account text view to navigate to signup page
        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        forgotPassword.setOnClickListener(task->{
            forgotPasswordBottomSheet(false);
        });

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
    private void forgotPasswordBottomSheet(boolean isOTPverified){
        bottomSheetDialog=new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.forgot_password_bottomsheet,null);
        bottomSheetDialog.setContentView(layout);
        mobileNumberLayout=layout.findViewById(R.id.moileNumberLayout);
        passwordLayout=layout.findViewById(R.id.passwordLayout);
        bottomSheetMobileNumber=layout.findViewById(R.id.mobileNumber);
        newPassword=layout.findViewById(R.id.newPassword);
        confirmPassword=layout.findViewById(R.id.confirmPassword);
        sentOTP=layout.findViewById(R.id.sentOtp);
        changepassword=layout.findViewById(R.id.changePassword);
        if (isOTPverified){

            mobileNumberLayout.setVisibility(View.GONE);
            passwordLayout.setVisibility(View.VISIBLE);
            changepassword.setOnClickListener(task->{
                passwordVerified=true;
                if (!newPassword.getText().toString().isEmpty() || !confirmPassword.getText().toString().isEmpty()){

                    if (newPassword.getText().toString().isEmpty()){
                        newPassword.setError("enter the new password");
                        passwordVerified=false;
                    } else if (newPassword.getText().toString().length()<8) {
                        newPassword.setError("password must contains minimum of 8 characters");
                        passwordVerified=false;
                    }
                    if (confirmPassword.getText().toString().isEmpty()){
                        confirmPassword.setError("enter the confirm password");
                        passwordVerified=false;
                    }
                    if (passwordVerified){
                        if (newPassword.getText().toString().equals(confirmPassword.getText().toString())){
                            updatePassword(newPassword.getText().toString(),phoneNumberContainer);
                        }else {
                            confirmPassword.setError("password mismatched");
                        }
                    }
                }else {
                    Toast.makeText(this, "enter the new password and confirm password", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            sentOTP.setOnClickListener(task->{
                if (bottomSheetMobileNumber.getText().toString().isEmpty()){
                    bottomSheetMobileNumber.setError("enter the mobile number");
                } else if (bottomSheetMobileNumber.getText().toString().length() != 10) {
                    bottomSheetMobileNumber.setError("mobile number must be 10 digit");
                }else{
                    String mobile="+91"+bottomSheetMobileNumber.getText().toString();
                    phoneNumberContainer=mobile;
                    sendVerificationCode(mobile);
                    bottomSheetDialog.cancel();
                }
            });
        }


        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }

    private void updatePassword(String newPassword,String phoneNumberContainer) {

        ref.child("UserDetails").child(phoneNumberContainer).child("password").setValue(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "password changed successfully", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.cancel();
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        if (phoneNumber!=null){
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
                                    Toast.makeText(MainActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    Toast.makeText(MainActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "Phone number verified successfully", Toast.LENGTH_SHORT).show();
                        forgotPasswordBottomSheet(true);
                    } else {
                        Toast.makeText(MainActivity.this, "Verification failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
        alertDialog.show();
    }
    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
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
    void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token=task.getResult();
                    DatabaseReference tokenRef=FirebaseDatabase.getInstance().getReference("UserDetails");
                    tokenRef.child(mobilenumber).child("fcmToken").setValue(token);
                    Log.i("myToken",token);
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Login faild", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}