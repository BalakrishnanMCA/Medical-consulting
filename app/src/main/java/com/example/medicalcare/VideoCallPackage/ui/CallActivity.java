package com.example.medicalcare.VideoCallPackage.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.medicalcare.MessagePackage.RetriveNotificationDetails;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.UserModule.UserFragments.HomeFragment;
import com.example.medicalcare.UserModule.UserFragments.HomePage;
import com.example.medicalcare.VideoCallPackage.repository.MainRepository;
import com.example.medicalcare.VideoCallPackage.utils.DataModelType;
import com.example.medicalcare.databinding.ActivityCallBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallActivity extends AppCompatActivity implements MainRepository.Listener {

    private ActivityCallBinding views;
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    boolean isDoctor;
    String mobile,receiverToken;
    DatabaseReference senderRef,receiverRef;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("VideoCall");

    OnBackPressedDispatcher onBackPressedDispatcher;

    SharedPreferences preferences;
    String userMob,doctorMob;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        isDoctor=getIntent().getBooleanExtra("isDoctor",false);
        if (isDoctor){
            views.callLayout.setVisibility(View.VISIBLE);
            progressDialog();
        }else{
            userMob= getIntent().getExtras().getString("userMob");
            doctorMob=getIntent().getExtras().getString("DocMob");
            sentNotification(Objects.requireNonNull(getIntent().getExtras()).getString("userMob"),getIntent().getExtras().getString("DocMob"));
        }
        preferences= getSharedPreferences("UserProfile",MODE_PRIVATE);
        mobile=preferences.getString("UserMobile","");

        onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press

               if (!isDoctor){
                   dbRef.child(mobile).removeValue();
                   finish();
               }else{

               }

            }
        });
        init();
    }


    private void sentNotification(String userMob, String docMob) {
        receiverRef=FirebaseDatabase.getInstance().getReference("DoctorDetails");
        senderRef=FirebaseDatabase.getInstance().getReference("UserDetails");

        receiverRef.child(docMob).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                RetriveNotificationDetails receiverDetails=task.getResult().getValue(RetriveNotificationDetails.class);
                assert receiverDetails != null;

                receiverToken=receiverDetails.getFcmToken();
            }
        });
        senderRef.child(userMob).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                RetriveNotificationDetails details=task.getResult().getValue(RetriveNotificationDetails.class);
                try {
                    JSONObject jsonObject=new JSONObject();
                    JSONObject notificationObject=new JSONObject();
                    assert details != null;
                    notificationObject.put("title",details.getName());
                    notificationObject.put("body","patient waiting in the waiting room");

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("workId","callRequest");
                    dataObj.put("patientNumber",userMob);



                    jsonObject.put("notification",notificationObject);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",receiverToken);

                    callAPI(jsonObject);
                }catch (Exception e){

                }
            }
        });



    }
    private void callAPI(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        String url="https://fcm.googleapis.com/fcm/send";
        RequestBody body=RequestBody.create(jsonObject.toString(),JSON);
        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization","Bearer AAAA0EbJs78:APA91bFBfHKwyW3UAIRzJb8Z2J5qaLz7sebdMFv-hfN9gV9By-5E4XU0shRNJwG9hFQk6yvwWrBQ0zBApNM_8XYvQMd0vwpFOl2zVWmj7hAIUs7Hs15Ezi84PaVTcnVNVsB7wOSXTEt3")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("Message failure","message could not sent");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("Message delivered","message could be sent");
            }
        });
    }

    private void init(){
        mainRepository = MainRepository.getInstance();
        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;


        mainRepository.subscribeForLatestEvent(data->{
            if (data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    views.incomingNameTV.setText(data.getSender()+" is Calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v->{
                        //star the call here
                        progressDialog();
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v->{
                        mainRepository.sendRejectCallRequest(data.getSender(),()->{
                            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
                        });
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                });
            }else if (data.getType()== DataModelType.RejectCall){
                webrtcClosed();
                Toast.makeText(this, "call rejected", Toast.LENGTH_LONG).show();

            }
        });

        views.switchCameraButton.setOnClickListener(v->{
            mainRepository.switchCamera();
        });

        views.micButton.setOnClickListener(v->{
            if (isMicrophoneMuted){
                views.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
            }else {
                views.micButton.setImageResource(R.drawable.mic);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted=!isMicrophoneMuted;
        });

        views.videoButton.setOnClickListener(v->{
            if (isCameraMuted){
                views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            }else {
                views.videoButton.setImageResource(R.drawable.videocam);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted=!isCameraMuted;
        });

        views.endCallButton.setOnClickListener(v->{
            mainRepository.endCall();
            if (!isDoctor){
                dbRef.child(userMob).removeValue();
            }
            finish();
        });

    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(()->{

                alertDialog.dismiss();

            views.incomingCallLayout.setVisibility(View.GONE);
            views.whoToCallLayout.setVisibility(View.GONE);
            views.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {

        runOnUiThread(this::finish);

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
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isDoctor){
            dbRef.child(userMob).removeValue();
        }


    }
}