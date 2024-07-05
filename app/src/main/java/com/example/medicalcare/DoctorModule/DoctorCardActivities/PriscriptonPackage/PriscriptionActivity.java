package com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityPriscriptionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;
import java.util.Calendar;

public class PriscriptionActivity extends AppCompatActivity {

    ActivityPriscriptionBinding views;

    ArrayList<MedicineList> medicinelist;
    boolean validationSuccess=true;
    String today,docName,medicineName,patientName,patientAge,patientGender,days,specialization,userMobile,mobile;
    boolean morning,afternoon,night;

    MedicineListAdapter cardAdapter;
    DatabaseReference priscriptionRef= FirebaseDatabase.getInstance().getReference("Priscription");
    DatabaseReference DocpriscriptionRef= FirebaseDatabase.getInstance().getReference("DoctorPriscription");
    OnBackPressedDispatcher onBackPressedDispatcher;



    NetworkChangeReceiver networkChangeReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityPriscriptionBinding.inflate(getLayoutInflater());
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

        views.backButton.setOnClickListener(tsk->{
            onBackPressedDispatcher.onBackPressed();
        });
        Intent intent=getIntent();
        specialization=intent.getStringExtra("specialization");
        userMobile=intent.getStringExtra("userMobile");
        SharedPreferences preferences= getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        docName=preferences.getString("name","");
        mobile=preferences.getString("DoctorMobile","");
        onBackPressedDispatcher = this.getOnBackPressedDispatcher();


        medicinelist=new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month starts from 0, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        today= String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        cardAdapter=new MedicineListAdapter(medicinelist,this);
        views.medicineRecycler.setLayoutManager(linearLayoutManager);
        views.medicineRecycler.setAdapter(cardAdapter);


        views.genderdropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                views.genderdropdown.setError(null);
            }
        });

        views.docName.setText(docName);
        views.date.setText(today);
        views.addMedicine.setOnClickListener(task-> {
            validationSuccess=true;
            if (views.medicineName.getText().toString().isEmpty()){
                views.medicineName.setError("enter the medicine name");
                validationSuccess=false;
            }else{
                medicineName=views.medicineName.getText().toString();
            }
            if (views.days.getText().toString().isEmpty()){
                views.days.setError("enter how many days");
                validationSuccess=false;
            }else {
                days=views.days.getText().toString();
            }
            if (!views.morning.isChecked() && !views.afternoon.isChecked() && !views.night.isChecked()){
                Toast.makeText(this, "enter the tablet session", Toast.LENGTH_SHORT).show();
                validationSuccess=false;
            }else{
                if (views.morning.isChecked()){
                    morning=true;
                }
                if (views.afternoon.isChecked()){
                    afternoon=true;
                }
                if (views.night.isChecked()){
                    night=true;
                }
            }
            if (validationSuccess){
                MedicineList list=new MedicineList(medicineName,morning,afternoon,night,days);
                medicinelist.add(list);
                Toast.makeText(this, "medicine added", Toast.LENGTH_SHORT).show();
                cardAdapter.notifyDataSetChanged();
                clearData();
            }
        });
        views.sentPriscription.setOnClickListener(task->{
            validationSuccess=true;
            if (medicinelist.isEmpty()){
                Toast.makeText(this, "Medicine list is empty", Toast.LENGTH_SHORT).show();
                validationSuccess=false;
            }
            if (views.patientName.getText().toString().isEmpty()){
                views.patientName.setError("enter the patient name");
                validationSuccess=false;
            }else{
                patientName=views.patientName.getText().toString();
            }
            if (views.patientAge.getText().toString().isEmpty()){
                views.patientAge.setError("enter the age of the patient");
                validationSuccess=false;
            }else {
                patientAge=views.patientAge.getText().toString();
            }
            if (views.genderdropdown.getText().toString().isEmpty()){
                views.genderdropdown.setError("select the gender of patient");
                validationSuccess=false;
            }else{
                patientGender=views.genderdropdown.getText().toString();
            }
            if(validationSuccess){
                uploadPriscription();

            }
        });


    }

    private void uploadPriscription() {
        UploadPriscriptionData uploadData=new UploadPriscriptionData(docName,specialization,today,patientName,patientAge,patientGender,medicinelist);
        priscriptionRef.child(userMobile).child(mobile).setValue(uploadData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DocpriscriptionRef.child(mobile).child(userMobile).setValue(uploadData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PriscriptionActivity.this, "prescription sented", Toast.LENGTH_SHORT).show();
                            onBackPressedDispatcher.onBackPressed();

                        }
                    });

                }
            }
        });
    }

    private void clearData() {
        views.medicineName.setText("");
        views.days.setText("");
        views.morning.setChecked(false);
        views.afternoon.setChecked(false);
        views.night.setChecked(false);
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