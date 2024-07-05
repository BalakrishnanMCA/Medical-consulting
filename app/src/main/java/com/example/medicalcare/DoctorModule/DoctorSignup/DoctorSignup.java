package com.example.medicalcare.DoctorModule.DoctorSignup;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.aviran.cookiebar2.CookieBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DoctorSignup extends AppCompatActivity {
    String userInput,docfee;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ProgressBar progressBar;

    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
    DatabaseReference ref = firebaseDatabase.getReference();
    //********
    private FirebaseAuth mAuth;
    private String verificationId;
    EditText name,email,doclicence,mob,experience;
    TextView specializationlist;
    TextInputEditText password,confirmpassword;
    LinearLayout signup;
    List<String> docspecialization;

    boolean valueverification=true;
    String total ="";
    String docname,docemail,docmobile,doctorlicence,doctorspecialization,doctorpassword,doctorconfirmpassword,doctorexperience;
    CheckBox phycisian,skin,hair,children,ortho,deit,physio,mental,womenhealth,ent,eyes,teeth,stomach,kidney,heart,lungs,brain,diabetics,neuro,fertility;

    DoctorDetailsUpload uploadData;
    UploadSpecializationData specializationData;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    OnBackPressedDispatcher onBackPressedDispatcher;
    NetworkChangeReceiver networkChangeReceiver;

    int uploadedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_signup);

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


        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        doclicence=findViewById(R.id.license);
        mob=findViewById(R.id.mobile);
        password=findViewById(R.id.password);
        confirmpassword=findViewById(R.id.confirmpassword);
        signup=findViewById(R.id.signup);
        specializationlist=findViewById(R.id.list);
        experience=findViewById(R.id.experience);
        progressBar=findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        editor = preferences.edit();

        if (progressBar.getVisibility()==View.VISIBLE){
            progressBar.setVisibility(View.GONE);
        }



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                valueverification=true;
                docname=name.getText().toString();
                docemail=email.getText().toString();
                docmobile=mob.getText().toString();
                doctorlicence=doclicence.getText().toString();
                doctorspecialization=specializationlist.getText().toString();
                doctorpassword=password.getText().toString();
                doctorconfirmpassword=confirmpassword.getText().toString();
                doctorexperience=experience.getText().toString();


                if (!docname.isEmpty() && !docemail.isEmpty() && !docmobile.isEmpty() && !doctorlicence.isEmpty()
                        && !doctorspecialization.isEmpty() && !doctorpassword.isEmpty() && !doctorconfirmpassword.isEmpty()){
                    if (docmobile.length()!=10){
                        mob.setError("mobile number must be 10 digits");
                        valueverification=false;
                    }else{
                        docmobile="+91"+docmobile;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(docemail).matches()){
                        email.setError("enter valid email address");
                        valueverification=false;
                    }
                    if (doctorpassword.length()>8){
                        password.setError("password must be 8 digits");
                        valueverification=false;
                    }
                    if (!doctorpassword.equals(doctorconfirmpassword)){
                        confirmpassword.setError("password did not matches");
                        valueverification=false;
                    }
                    if (valueverification){
                        progressBar.setVisibility(View.VISIBLE);
                        sendVerificationCode(docmobile);
                        OtpDialog();

                    }

                }
                else {
                    if (TextUtils.isEmpty(name.getText().toString())){
                        name.setError("enter the name");
                    }
                    if (TextUtils.isEmpty(email.getText().toString())){
                        email.setError("enter the email");
                    }
                    if (TextUtils.isEmpty(mob.getText().toString())){
                        mob.setError("enter the mobile number");
                    }
                    if (TextUtils.isEmpty(doclicence.getText().toString())){
                        doclicence.setError("enter the licence number");
                    }
                    if (TextUtils.isEmpty(specializationlist.getText().toString())){
                        specializationlist.setError("select the specialization");
                    }
                    if (TextUtils.isEmpty(password.getText().toString())){
                        password.setError("enter the password");
                    }
                    if (TextUtils.isEmpty(confirmpassword.getText().toString())){
                        confirmpassword.setError("enter the confirm password");
                    }
                    if (TextUtils.isEmpty(experience.getText().toString())){
                        experience.setError("enter your experience");
                    }
                }
            }
        });

        specializationlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
                docspecialization.clear();
            }
        });


    }
    private void OtpDialog() {
        // Create an AlertDialog Builder
        builder = new AlertDialog.Builder(this);
        // Create and show the AlertDialog

        builder.setTitle("Enter OTP");


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
            alertDialog.dismiss();

        });
        builder.setNegativeButton("cancel", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    private void sendVerificationCode(String phoneNumber) {
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
                                Toast.makeText(DoctorSignup.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationId = s;
                            }
                        }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);


    }
    private void showInputDialog() {
        // Create an AlertDialog Builder
        if (docspecialization != null && !docspecialization.isEmpty()){
            docspecialization.clear();
        }
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



        docspecialization = new ArrayList<>();



        // Set positive button (Verify)
//        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//
//                if (phycisian.isChecked()){
//                    docspecialization.add("phycisian");
//                }
//                if (skin.isChecked()){
//                    docspecialization.add("skin specialist");
//                }
//                if (hair.isChecked()){
//                    docspecialization.add("hair specialist");
//                }
//                if (children.isChecked()){
//                    docspecialization.add("Childrens specialist");
//                }
//                if (ortho.isChecked()){
//                    docspecialization.add("orthopedician");
//                }
//                if (deit.isChecked()){
//                    docspecialization.add("Deitition");
//                }
//                if (physio.isChecked()){
//                    docspecialization.add("physiotherapist");
//                }
//                if (mental.isChecked()){
//                    docspecialization.add("Mental wellness");
//                }
//                if (womenhealth.isChecked()){
//                    docspecialization.add("womens health");
//                }
//                if (ent.isChecked()){
//                    docspecialization.add("ENT");
//                }
//                if (eyes.isChecked()){
//                    docspecialization.add("Eyes");
//                }
//                if (teeth.isChecked()){
//                    docspecialization.add("Teeth");
//                }
//                if (stomach.isChecked()){
//                    docspecialization.add("stomach");
//                }
//                if(kidney.isChecked()){
//                    docspecialization.add("kidney");
//                }
//                if(heart.isChecked()){
//                    docspecialization.add("heart");
//                }
//                if (lungs.isChecked()){
//                    docspecialization.add("lungs");
//                }
//                if(brain.isChecked()){
//                    docspecialization.add("brain");
//                }
//                if (diabetics.isChecked()){
//                    docspecialization.add("diabetologist");
//                }
//                if(neuro.isChecked()){
//                    docspecialization.add("neurologist");
//                }
//                if (fertility.isChecked()){
//                    docspecialization.add("fertility specialist");
//                }
//
//                if (!docspecialization.isEmpty()){
//
//                    for (String val:docspecialization) {
//                        if (Objects.equals(total, "")){
//                            total=val;
//
//                        }
//                        else{
//                            total+=" , ";
//                            total+=val;
//
//                        }
//                    }
//                }
//                specializationlist.setText(total);
//
//            }
//        });

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int checkedCount = 0; // Variable to count checked checkboxes
                for (CheckBox checkBox : Arrays.asList(phycisian, skin, hair, children, ortho, deit, physio,
                        mental, womenhealth, ent, eyes, teeth, stomach, kidney, heart, lungs, brain,
                        diabetics, neuro, fertility)) {
                    if (checkBox.isChecked()) {
                        checkedCount++;
                        if (checkedCount > 3) {
                            // More than three checkboxes checked, show toast and return
                            Toast.makeText(getApplicationContext(), "you can only select maximum three specialization", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                // Proceed to add selected checkboxes to the list
                for (CheckBox checkBox : Arrays.asList(phycisian, skin, hair, children, ortho, deit, physio,
                        mental, womenhealth, ent, eyes, teeth, stomach, kidney, heart, lungs, brain,
                        diabetics, neuro, fertility)) {
                    if (checkBox.isChecked()) {
                        // Add checked specialization to the list
                        docspecialization.add(checkBox.getText().toString());
                    }
                }

                // Update UI with selected specializations
                if (!docspecialization.isEmpty()) {
                    StringBuilder totalBuilder = new StringBuilder();
                    for (String val : docspecialization) {
                        totalBuilder.append(val).append(" , ");
                    }
                    // Remove the trailing " , " and set the text
                    specializationlist.setText(totalBuilder.substring(0, totalBuilder.length() - 3));
                }
                alertDialog.cancel();
            }
        });


        // Create and show the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void uploaduserdata() {


        final int totalSpecializations = docspecialization.size();

        uploadData=new DoctorDetailsUpload(docname,docemail,docmobile,doctorlicence,doctorspecialization,doctorpassword,doctorexperience," ");
        for (String value:docspecialization){
            getamount(value,new OnCompleteListener<Void>(){

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    uploadedCount++;

                    // Check if all specializations are uploaded
                    if (uploadedCount == totalSpecializations) {
                        // All specializations uploaded, navigate to DoctorLogin
                        startActivity(new Intent(DoctorSignup.this, DoctorLogin.class));
                        finish();
                    }

                }
            });
        }



        ref.child("DoctorDetails").child(docmobile).setValue(uploadData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                editor.putString("Docprofiletask","1").apply();
            }
        });



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Verification successful, continue with user registration
                        Toast.makeText(DoctorSignup.this, "Phone number verified successfully", Toast.LENGTH_SHORT).show();


                        uploaduserdata();
                    } else {
                        Toast.makeText(DoctorSignup.this, "Verification failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void getamount(String value,OnCompleteListener<Void> onCompleteListener){


            preferences = getSharedPreferences("DoctorProfile", MODE_PRIVATE);
            editor = preferences.edit();
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter amount for "+value);
            // Inflate the layout for the dialog
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.getamount, null);
            builder.setView(dialogView);



            final EditText enteramount = dialogView.findViewById(R.id.amount);

            // Set positive button (Verify)
            builder.setPositiveButton("Done", (dialog, which) -> {

                userInput = enteramount.getText().toString();
                specializationData=new UploadSpecializationData(docname,value,"5.0","",userInput,doctorexperience,docmobile );
                ref.child(value).child(docmobile).setValue(specializationData).addOnCompleteListener(onCompleteListener);
                alertDialog.dismiss();


            });

            // Create and show the AlertDialog
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