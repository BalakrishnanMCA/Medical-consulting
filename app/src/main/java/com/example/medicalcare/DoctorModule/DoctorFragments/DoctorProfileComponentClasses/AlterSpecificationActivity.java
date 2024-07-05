package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.R;
import com.example.medicalcare.databinding.ActivityAlterSpecificationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AlterSpecificationActivity extends AppCompatActivity {

    ActivityAlterSpecificationBinding views;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    CheckBox phycisian,skin,hair,children,ortho,deit,physio,mental,womenhealth,ent,eyes,teeth,stomach,kidney,heart,lungs,brain,diabetics,neuro,fertility;
    List<String> docspecialization;
    String total ="";
    TextView specializationlist;

    SpecializationAdapter cardAdapter;

    SharedPreferences preferences;
    String mobile,retrivedSpec;
    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("DoctorDetails");

    OnBackPressedDispatcher onBackPressedDispatcher;
    NetworkChangeReceiver networkChangeReceiver;

    boolean backToOnline=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityAlterSpecificationBinding.inflate(getLayoutInflater());
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
        progressDialog();
//        docspecialization = new ArrayList<>();
        preferences=getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        mobile=preferences.getString("DoctorMobile","");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mobile)){
                    retrivedSpec=snapshot.child(mobile).child("doctorSpecialization").getValue(String.class);
                    assert retrivedSpec != null;
                    docspecialization=new ArrayList<>(Arrays.asList(retrivedSpec.split(" , ")));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(AlterSpecificationActivity.this,LinearLayoutManager.VERTICAL,false);
                cardAdapter=new SpecializationAdapter(docspecialization);
                views.specRecycler.setLayoutManager(linearLayoutManager);
                views.specRecycler.setAdapter(cardAdapter);
                cardAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        },2000);





        views.modifyButton.setOnClickListener(task ->{
            showInputDialog();
        });



    }
    private void showInputDialog() {
        // Create an AlertDialog Builder
        builder = new AlertDialog.Builder(this);

        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.catagorycheckbox, null);
        builder.setView(dialogView);


        phycisian=dialogView.findViewById(R.id.physcian);
        skin=dialogView.findViewById(R.id.skin);
        hair=dialogView.findViewById(R.id.hair);
        children=dialogView.findViewById(R.id.child_specialist);
        ortho=dialogView.findViewById(R.id.ortho);
        deit=dialogView.findViewById(R.id.diet);
        physio=dialogView.findViewById(R.id.physio);
        mental=dialogView.findViewById(R.id.metal);
        womenhealth=dialogView.findViewById(R.id.womenhealth);
        ent=dialogView.findViewById(R.id.ent);
        eyes=dialogView.findViewById(R.id.eyes);
        teeth=dialogView.findViewById(R.id.dentist);
        stomach=dialogView.findViewById(R.id.stomach);
        kidney=dialogView.findViewById(R.id.kidney);
        heart=dialogView.findViewById(R.id.heart);
        lungs=dialogView.findViewById(R.id.lungs);
        brain=dialogView.findViewById(R.id.brain);
        diabetics=dialogView.findViewById(R.id.diabetics);
        neuro=dialogView.findViewById(R.id.neuro);
        fertility=dialogView.findViewById(R.id.fertility);



        for (String data :docspecialization) {
            if (Objects.equals(data, "phycisian")){
                phycisian.setChecked(true);
            }
            if (Objects.equals(data, "skin specialist")){
                skin.setChecked(true);
            }
            if (Objects.equals(data, "hair specialist")){
                hair.setChecked(true);
            }
            if (Objects.equals(data, "Childrens specialist")){
                children.setChecked(true);
            }
            if (Objects.equals(data, "orthopedician")){
                ortho.setChecked(true);
            }
            if (Objects.equals(data, "Deitition")){
                deit.setChecked(true);
            }
            if (Objects.equals(data, "physiotherapist")){
                physio.setChecked(true);
            }
            if (Objects.equals(data, "Mental wellness")){
                mental.setChecked(true);
            }
            if (Objects.equals(data, "womens health")){
                womenhealth.setChecked(true);
            }
            if (Objects.equals(data, "ENT")){
                ent.setChecked(true);
            }
            if (Objects.equals(data, "Eyes")){
                eyes.setChecked(true);
            }
            if (Objects.equals(data, "Teeth")){
                teeth.setChecked(true);
            }
            if (Objects.equals(data, "stomach")){
                stomach.setChecked(true);
            }
            if (Objects.equals(data, "kidney")){
                kidney.setChecked(true);
            }
            if (Objects.equals(data, "heart")){
                heart.setChecked(true);
            }
            if (Objects.equals(data, "lungs")){
                lungs.setChecked(true);
            }
            if (Objects.equals(data, "brain")){
                brain.setChecked(true);
            }
            if (Objects.equals(data, "diabetologist")){
                diabetics.setChecked(true);
            }
            if (Objects.equals(data, "neurologist")){
                neuro.setChecked(true);
            }
            if (Objects.equals(data, "fertility specialist")){
                fertility.setChecked(true);
            }
        }



        // Set positive button (Verify)
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog();
                docspecialization.clear();

                if (phycisian.isChecked()){
                    docspecialization.add("phycisian");
                }
                if (skin.isChecked()){
                    docspecialization.add("skin specialist");
                }
                if (hair.isChecked()){
                    docspecialization.add("hair specialist");
                }
                if (children.isChecked()){
                    docspecialization.add("Childrens specialist");
                }
                if (ortho.isChecked()){
                    docspecialization.add("orthopedician");
                }
                if (deit.isChecked()){
                    docspecialization.add("Deitition");
                }
                if (physio.isChecked()){
                    docspecialization.add("physiotherapist");
                }
                if (mental.isChecked()){
                    docspecialization.add("Mental wellness");
                }
                if (womenhealth.isChecked()){
                    docspecialization.add("womens health");
                }
                if (ent.isChecked()){
                    docspecialization.add("ENT");
                }
                if (eyes.isChecked()){
                    docspecialization.add("Eyes");
                }
                if (teeth.isChecked()){
                    docspecialization.add("Teeth");
                }
                if (stomach.isChecked()){
                    docspecialization.add("stomach");
                }
                if(kidney.isChecked()){
                    docspecialization.add("kidney");
                }
                if(heart.isChecked()){
                    docspecialization.add("heart");
                }
                if (lungs.isChecked()){
                    docspecialization.add("lungs");
                }
                if(brain.isChecked()){
                    docspecialization.add("brain");
                }
                if (diabetics.isChecked()){
                    docspecialization.add("diabetologist");
                }
                if(neuro.isChecked()){
                    docspecialization.add("neurologist");
                }
                if (fertility.isChecked()){
                    docspecialization.add("fertility specialist");
                }

                if (!docspecialization.isEmpty()){

                    for (String val:docspecialization) {
                        if (Objects.equals(total, "")){
                            total=val;

                        }
                        else{
                            total+=" , ";
                            total+=val;

                        }
                    }
                }
                ref.child(mobile).child("doctorSpecialization").setValue(total).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        cardAdapter.notifyDataSetChanged();
                        Toast.makeText(AlterSpecificationActivity.this, "specialization is updated", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

            }
        });


        // Create and show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
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