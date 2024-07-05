package com.example.medicalcare.UserModule.UserCardActivities;

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
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.DoctorModule.DoctorSignup.UploadSpecializationData;
import com.example.medicalcare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DoctorListActivity extends AppCompatActivity {


    SearchView searchView;
    ImageButton imageButton,backActivity;
    RecyclerView recyclerView;

    DoctorCardAdapter cardAdapter;
    ArrayList<UploadSpecializationData> specializationData;

    ArrayList<UploadSpecializationData> favoriteArrayList;

    UploadSpecializationData favoriteclass;

    DatabaseReference databaseReference;
    DatabaseReference favoriteRef= FirebaseDatabase.getInstance().getReference("UserFavorites");

    String specialization;
    SharedPreferences preferences;

    TextView pagetitle,noDataText;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    ArrayList<UploadSpecializationData> specializationDataList;

    OnBackPressedDispatcher onBackPressedDispatcher;


   NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bone);

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


        progressDialog();
        preferences=getSharedPreferences("UserProfile", MODE_PRIVATE);
        specialization=preferences.getString("specialization","");


        databaseReference= FirebaseDatabase.getInstance().getReference(specialization);
        SharedPreferences preferences=getSharedPreferences("UserProfile", MODE_PRIVATE);
        String usermob=preferences.getString("UserMobile","");

        pagetitle=findViewById(R.id.pagetitle);
        searchView=findViewById(R.id.searchview);
        imageButton=findViewById(R.id.search);
        recyclerView=findViewById(R.id.physcianrecycler);
        noDataText=findViewById(R.id.noDataText);
        backActivity=findViewById(R.id.backActivityButton);
        pagetitle.setText(specialization);

        backActivity.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchView.getVisibility()==View.GONE){
                    searchView.setVisibility(View.VISIBLE);
                }else{
                    searchView.setVisibility(View.GONE);

                }
            }
        });
        favoriteArrayList=new ArrayList<>();

        favoriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.child(usermob).getChildren()) {
                    favoriteclass=data.getValue(UploadSpecializationData.class);
                    favoriteArrayList.add(favoriteclass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        specializationData=new ArrayList<>();
        specializationDataList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        cardAdapter=new DoctorCardAdapter(specializationData,usermob,favoriteArrayList,specializationDataList,this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cardAdapter);
        // retrive the favorite doctor lis of the user


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specializationData.clear(); // Clear existing data

                if (snapshot.exists()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()) {

                        UploadSpecializationData snapdata= snapshot1.getValue(UploadSpecializationData.class);
                        specializationData.add(snapdata);


                    }
                }else{
                    recyclerView.setVisibility(View.GONE);
                    noDataText.setVisibility(View.VISIBLE);
                }

                specializationDataList.addAll(specializationData);
                cardAdapter.notifyDataSetChanged();
                alertDialog.cancel();
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