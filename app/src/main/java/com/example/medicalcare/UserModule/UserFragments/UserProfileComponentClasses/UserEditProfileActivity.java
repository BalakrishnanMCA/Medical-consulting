package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses;

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
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.EditProfileActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.EditProfileDetailRetriver;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityUserEditProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

public class UserEditProfileActivity extends AppCompatActivity {

    ActivityUserEditProfileBinding views;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    SharedPreferences preferences;
    String mobile;
    DatabaseReference dataRef= FirebaseDatabase.getInstance().getReference("UserDetails");


    NetworkChangeReceiver networkChangeReceiver;

    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityUserEditProfileBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

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

        views.backButton.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });
        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        mobile=preferences.getString("UserMobile","");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mobile)){
                    RetriveEditProfileDetails retriver=snapshot.child(mobile).getValue(RetriveEditProfileDetails.class);
                    if (retriver != null){
                        views.name.setHint(retriver.getName());
                        views.email.setHint(retriver.getEmailid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        views.saveChanges.setOnClickListener(task ->{
            if (!views.name.getText().toString().isEmpty()){
                String name=views.name.getText().toString();
                dataRef.child(mobile).child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserEditProfileActivity.this, " Name updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (!views.email.getText().toString().isEmpty()){
                String email=views.email.getText().toString();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    dataRef.child(mobile).child("email").setValue(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UserEditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    views.email.setError("enter the proper email address");
                }


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

}