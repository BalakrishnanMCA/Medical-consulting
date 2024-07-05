package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.PaymentHistoryPackage;

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

import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.UserModule.BookingAppointment.PatientPaymentHistoryClass;
import com.example.medicalcare.databinding.ActivityPaymentHistoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;

public class PaymentHistoryActivity extends AppCompatActivity {

    ActivityPaymentHistoryBinding views;
    PaymentHistoryAdapter paymentAdapter;

    ArrayList<PatientPaymentHistoryClass> paymentlist;

    String userMobile;
    SharedPreferences preferencesuser;

    DatabaseReference paymentHistoryRef= FirebaseDatabase.getInstance().getReference("PatientPaymentHistory");

    NetworkChangeReceiver networkChangeReceiver;
    OnBackPressedDispatcher onBackPressedDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityPaymentHistoryBinding.inflate(getLayoutInflater());
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
        preferencesuser = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userMobile=preferencesuser.getString("UserMobile","");

        paymentlist=new ArrayList<>();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        paymentAdapter=new PaymentHistoryAdapter(paymentlist,this);
        views.paymentHistoryRecycler.setLayoutManager(linearLayoutManager);
        views.paymentHistoryRecycler.setAdapter(paymentAdapter);

        paymentHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userMobile)){
                    for (DataSnapshot paymentdata :snapshot.child(userMobile).getChildren()) {
                        PatientPaymentHistoryClass data=paymentdata.getValue(PatientPaymentHistoryClass.class);
                        paymentlist.add(data);
                        paymentAdapter.notifyDataSetChanged();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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