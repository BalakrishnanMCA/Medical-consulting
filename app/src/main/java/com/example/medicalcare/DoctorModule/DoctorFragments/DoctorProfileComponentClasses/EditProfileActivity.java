package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses;

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

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.R;
import com.example.medicalcare.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding profileBinding;

    SharedPreferences preferences;
    String mobile,successToast="";
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    DatabaseReference dataRef= FirebaseDatabase.getInstance().getReference("DoctorDetails");
    OnBackPressedDispatcher onBackPressedDispatcher;

   NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding=ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

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
        profileBinding.backButton.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });

        progressDialog();
        preferences=getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        mobile=preferences.getString("DoctorMobile","");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mobile)){
                    EditProfileDetailRetriver retriver=snapshot.child(mobile).getValue(EditProfileDetailRetriver.class);
                    if (retriver != null){
                        profileBinding.name.setHint(retriver.getName());
                        profileBinding.email.setHint(retriver.getEmail());
                        profileBinding.experience.setHint(retriver.getExperience());
                        if (retriver.getDescription() == null){
                            profileBinding.description.setHint("write the profile description here");
                        }else {
                            profileBinding.description.setHint(retriver.getDescription());
                        }
                        alertDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileBinding.saveChanges.setOnClickListener(task ->{
            if (profileBinding.name.getText().toString().isEmpty() && profileBinding.email.getText().toString().isEmpty() && profileBinding.experience.getText().toString().isEmpty() && profileBinding.description.getText().toString().isEmpty()) {


                if (!profileBinding.name.getText().toString().isEmpty()) {
                    String name = profileBinding.name.getText().toString();
                    dataRef.child(mobile).child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditProfileActivity.this, " Name updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (!profileBinding.email.getText().toString().isEmpty()) {
                    String email = profileBinding.email.getText().toString();
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        dataRef.child(mobile).child("email").setValue(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        profileBinding.email.setError("enter the proper email address");
                    }


                }
                if (!profileBinding.experience.getText().toString().isEmpty()) {
                    String experiece = profileBinding.experience.getText().toString();
                    dataRef.child(mobile).child("experience").setValue(experiece).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditProfileActivity.this, "Experience updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (!profileBinding.description.getText().toString().isEmpty()) {
                    String description = profileBinding.description.getText().toString();
                    dataRef.child(mobile).child("description").setValue(description).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(EditProfileActivity.this, "Description updated successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }else {
                Toast.makeText(this, "Fill the field which you want to change", Toast.LENGTH_SHORT).show();
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