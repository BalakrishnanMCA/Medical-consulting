package com.example.medicalcare.DoctorModule.DoctorSignup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorHomePage;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.aviran.cookiebar2.CookieBar;

import java.util.HashMap;
import java.util.Map;

public class DoctorLogin extends AppCompatActivity {
    EditText mobile,password;
    TextView createacc;
    LinearLayout signinbtn;
    String mobilenumber,userpassword;
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("DoctorDetails");
    boolean verification=true;
     ProgressBar progressBar;

     AlertDialog alertDialog;
     AlertDialog.Builder builder;

     OnBackPressedDispatcher onBackPressedDispatcher;
    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

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


        mobile=findViewById(R.id.mobile);
        password=findViewById(R.id.password);
        signinbtn=findViewById(R.id.signin);
        createacc=findViewById(R.id.createaccount);
        progressBar=findViewById(R.id.progress);
        SharedPreferences preferences = getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();


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
                        progressBar.setVisibility(View.VISIBLE);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(mobilenumber)){
                                    String retrivepassword=snapshot.child(mobilenumber).child("password").getValue(String.class);
                                    String retriveName=snapshot.child(mobilenumber).child("name").getValue(String.class);
                                    String retriveSpecialization=snapshot.child(mobilenumber).child("doctorSpecialization").getValue(String.class);
                                    if (TextUtils.equals(userpassword,retrivepassword)){
                                        editor.putString("name",retriveName);
                                        editor.putString("doctorSpecialization",retriveSpecialization);
                                        editor.putString("DoctorMobile",mobilenumber);
                                        editor.apply();
                                        alertDialog.cancel();
                                        getToken();

                                    }else {
                                        Toast.makeText(DoctorLogin.this, "incorrect password", Toast.LENGTH_SHORT).show();
                                    }



                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(DoctorLogin.this, error.toString(), Toast.LENGTH_SHORT).show();

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
                }


            }


        });
        //now set a onclick listener for create account text view to navigate to signup page
        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorLogin.this, DoctorSignup.class));
            }
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
                    DatabaseReference tokenRef=FirebaseDatabase.getInstance().getReference("DoctorDetails");
                    Map<String,Object> update=new HashMap<>();
                    update.put("fcmToken",token);
                    tokenRef.child(mobilenumber).updateChildren(update);
                    Log.i("DocToken",token);

                    Intent intent=new Intent(DoctorLogin.this, DoctorHomePage.class);
                    startActivity(intent);
                }
            }
        });
    }
}