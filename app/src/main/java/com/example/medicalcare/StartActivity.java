    package com.example.medicalcare;

    import androidx.annotation.NonNull;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.FragmentActivity;

    import android.Manifest;
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.app.NotificationManager;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.content.SharedPreferences;
    import android.content.pm.PackageManager;
    import android.net.ConnectivityManager;
    import android.net.Network;
    import android.net.NetworkInfo;
    import android.os.Build;
    import android.os.Bundle;
    import android.provider.Settings;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ProgressBar;
    import android.widget.Toast;


    import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorHomePage;
    import com.example.medicalcare.DoctorModule.DoctorSignup.DoctorLogin;
    import com.example.medicalcare.MessagePackage.MessageModule;
    import com.example.medicalcare.MessagePackage.RetriveNotificationDetails;
    import com.example.medicalcare.UserModule.BookingAppointment.BookingDoctor;
    import com.example.medicalcare.UserModule.UserFragments.HomePage;
    import com.example.medicalcare.UserModule.UserLoginSignup.MainActivity;
    import com.example.medicalcare.VideoCallPackage.repository.MainRepository;
    import com.example.medicalcare.VideoCallPackage.ui.CallActivity;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.permissionx.guolindev.PermissionX;

    import org.aviran.cookiebar2.CookieBar;
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

    public class StartActivity extends AppCompatActivity {


        Button patientlogin,doctorlogin;
        private MainRepository mainRepository;


        AlertDialog alertDialog;
        AlertDialog.Builder builder;

        NetworkChangeReceiver networkChangeReceiver;
        SharedPreferences preferencesdoctor,preferencesuser;
        String doctorMobile,userMobile;

        String receivernumber,receiverprofile,receiverName;
        DatabaseReference senderRef,receiverRef;
        String value,uservalue,receiverToken;




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            networkChangeReceiver = new NetworkChangeReceiver();
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

                registerReceiver(networkChangeReceiver, intentFilter);

    checkPermissoin();

            setContentView(R.layout.activity_start);
            patientlogin=findViewById(R.id.patient);
            doctorlogin=findViewById(R.id.doctor);
            SharedPreferences preferences = getSharedPreferences("DoctorProfile", MODE_PRIVATE);
             value=preferences.getString("DoctorMobile","");
            SharedPreferences userpreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
             uservalue=userpreferences.getString("UserMobile","");



            if (getIntent().getExtras() != null){
                if (getIntent().getExtras().getString("sender")!=null){

                    String sender = getIntent().getStringExtra("sender");
                    Toast.makeText(this, sender, Toast.LENGTH_SHORT).show();

                    if (Objects.equals(sender, "doctor")){
                        preferencesuser = getSharedPreferences("UserProfile", MODE_PRIVATE);
                        userMobile=preferencesuser.getString("UserMobile","");
                        Toast.makeText(this, String.valueOf(getIntent().getExtras().getString("userNum")), Toast.LENGTH_SHORT).show();

                        if(getIntent().getExtras().getString("userNum")!= null){
                            Toast.makeText(this, "intent one pass", Toast.LENGTH_SHORT).show();

                            receivernumber=getIntent().getExtras().getString("userNum");
                            receiverprofile=getIntent().getExtras().getString("profileImage");
                            receiverName=getIntent().getExtras().getString("senderName");

                            Intent intent=new Intent(StartActivity.this, MessageModule.class);
                            intent.putExtra("receivernumber",receivernumber);
                            intent.putExtra("sendernumber",userMobile);
                            intent.putExtra("receivername",receiverName);
                            intent.putExtra("receiverprofile",receiverprofile);
                            startActivity(intent);

                        }
                    }else if (Objects.equals(sender, "user")){

                        preferencesdoctor = getSharedPreferences("DoctorProfile", MODE_PRIVATE);
                        doctorMobile=preferencesdoctor.getString("DoctorMobile","");
                        if(getIntent().getExtras().getString("userNum")!= null){
                            receivernumber=getIntent().getExtras().getString("userNum");
                            receiverprofile=getIntent().getExtras().getString("profileImage");
                            receiverName=getIntent().getExtras().getString("senderName");
                            Intent intent=new Intent(StartActivity.this, MessageModule.class);
                            intent.putExtra("receivernumber",receivernumber);
                            intent.putExtra("sendernumber",doctorMobile);
                            intent.putExtra("receivername",receiverName);
                            intent.putExtra("receiverprofile",receiverprofile);
                            startActivity(intent);

                        }
                    }
                }
                else if (Objects.equals(getIntent().getExtras().getString("workId"), "callAcceptRequest")) {

                    Toast.makeText(this, "intent contains", Toast.LENGTH_SHORT).show();


                    mainRepository = MainRepository.getInstance();

                    PermissionX.init(this)
                            .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                            .request((allGranted, grantedList, deniedList) -> {
                                if (allGranted) {
                                    //login to firebase here
                                    Toast.makeText(this, "intent contains", Toast.LENGTH_SHORT).show();

                                    mainRepository.login(
                                            uservalue, this, () -> {
                                                //if success then we want to move to call activity
                                                startActivity(new Intent(this, CallActivity.class));
                                            }
                                    );
                                }
                            });





                }
                else if (Objects.equals(getIntent().getExtras().getString("workId"), "callRequest")){


                    mainRepository = MainRepository.getInstance();

                    PermissionX.init(this)
                            .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                            .request((allGranted, grantedList, deniedList) -> {
                                if (allGranted) {
                                    //login to firebase here

                                    mainRepository.login(
                                            value, this, () -> {
                                                alertDialog(getIntent().getExtras().getString("patientNumber"));
                                                //if success then we want to move to call activity
//                                        view.getContext().startActivity(new Intent(context, CallActivity.class));
                                            }
                                    );
                                }
                            });

                }

            }

            patientlogin.setOnClickListener(view -> {

                progressDialog();
                if (!uservalue.equals("")){
                    startActivity(new Intent(StartActivity.this, HomePage.class));
                    alertDialog.cancel();
                }else {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    alertDialog.cancel();
                }

            });
            doctorlogin.setOnClickListener(view -> {
                progressDialog();
                if (!value.equals("")){
                    startActivity(new Intent(StartActivity.this, DoctorHomePage.class));
                    alertDialog.cancel();
                }else {
                    startActivity(new Intent(StartActivity.this, DoctorLogin.class));
                    alertDialog.cancel();
                }

            });
        }
        @Override
        protected void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState = null; // Clear the saved instance state
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

        public void checkPermissoin(){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,},1);


            }
        }

        public void alertDialog(String mob){
            int i=0;
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Video call");
            builder.setMessage("Do you want to start Video call?");
            builder.setPositiveButton("start call", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mainRepository.sendCallRequest(mob,()->{
                        Toast.makeText(StartActivity.this, "Patient did not entered in waiting room", Toast.LENGTH_SHORT).show();
                        sendNotification(mob);

                    });
                    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("VideoCall");

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(mob).exists()){
                                Intent intent=new Intent(StartActivity.this, CallActivity.class);
                                intent.putExtra("isDoctor",true);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                    alertDialog.cancel();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    i=0;
                    alertDialog.cancel();
                }
            });
            alertDialog=builder.create();
            alertDialog.show();
            alertDialog.setCancelable(false);

        }
        private void sendNotification(String mob) {
            senderRef=FirebaseDatabase.getInstance().getReference("DoctorDetails");
            receiverRef=FirebaseDatabase.getInstance().getReference("UserDetails");

            receiverRef.child(mob).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    RetriveNotificationDetails receiverDetails=task.getResult().getValue(RetriveNotificationDetails.class);
                    assert receiverDetails != null;

                    receiverToken=receiverDetails.getFcmToken();
                }
            });
            senderRef.child(value).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    RetriveNotificationDetails details=task.getResult().getValue(RetriveNotificationDetails.class);
                    try {
                        JSONObject jsonObject=new JSONObject();
                        JSONObject notificationObject=new JSONObject();
                        assert details != null;
                        notificationObject.put("title",details.getName());
                        notificationObject.put("body","doctor try to call you");

                        JSONObject dataObj=new JSONObject();
                        dataObj.put("workId","callAcceptRequest");


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

    }
