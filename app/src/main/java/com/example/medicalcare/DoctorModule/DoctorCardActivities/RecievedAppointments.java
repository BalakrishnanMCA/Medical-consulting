package com.example.medicalcare.DoctorModule.DoctorCardActivities;


import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.medicalcare.DoctorModule.DoctorAppointmentListClass;
import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.PriscriptionActivity;
import com.example.medicalcare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;

public class RecievedAppointments extends AppCompatActivity {

    ImageButton search,back;
    TextView title,noDataText;
    SearchView searchView;


    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference Ref=firebaseDatabase.getReference("DoctorAppointmentList");

    ArrayList<DoctorAppointmentListClass> appointmentList;
    ArrayList<DoctorAppointmentListClass> appointmentListValues;




    DoctorAppointmentListClass doctorAppointmentListClass;
    AppointmentListAdapter cardAdapter;
    RecyclerView appointmentRecycler;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    String titletext,currentUser;
    ImageButton backButton;

    OnBackPressedDispatcher onBackPressedDispatcher;


    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieved_appointments);


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

        search=findViewById(R.id.search);
        back=findViewById(R.id.backbutton);
        title=findViewById(R.id.specificationtitle);
        searchView=findViewById(R.id.searchview);
        appointmentRecycler=findViewById(R.id.appointmentListrecycler);
        noDataText=findViewById(R.id.noDataText);


        back.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });


        Intent intent=getIntent();
        titletext=intent.getStringExtra("spec");
        title.setText(titletext);


        progressDialog();
        appointmentList=new ArrayList<>();
        appointmentListValues=new ArrayList<>();
        search.setOnClickListener(view -> {
            if (searchView.getVisibility()==View.GONE){
                searchView.setVisibility(View.VISIBLE);
            }else{
                searchView.setVisibility(View.GONE);

            }
        });

        SharedPreferences preferences= getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        currentUser=preferences.getString("DoctorMobile","");


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        cardAdapter=new AppointmentListAdapter(this,appointmentList,currentUser,appointmentListValues);
        appointmentRecycler.setLayoutManager(linearLayoutManager);
        appointmentRecycler.setAdapter(cardAdapter);

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(titletext)){
                    if (snapshot.child(titletext).hasChild(currentUser)){
                        for (DataSnapshot data :snapshot.child(titletext).child(currentUser).getChildren()) {
                            doctorAppointmentListClass=data.getValue(DoctorAppointmentListClass.class);
                            appointmentList.add(doctorAppointmentListClass);


                        }
                        appointmentListValues.addAll(appointmentList);
                        cardAdapter.notifyDataSetChanged();
                        alertDialog.cancel();
                    }else {
                        appointmentRecycler.setVisibility(View.GONE);
                        noDataText.setVisibility(View.VISIBLE);
                        alertDialog.cancel();
                    }

                }else {
                    appointmentRecycler.setVisibility(View.GONE);
                    noDataText.setVisibility(View.VISIBLE);
                    alertDialog.cancel();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cardAdapter.filter(newText);
                return false;
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