package com.example.medicalcare.MessagePackage;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorHomePage;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.BookingAppointment.BookingDoctor;
import com.example.medicalcare.UserModule.UserFragments.HomePage;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class MessageModule extends AppCompatActivity {

    private static boolean isActive = false;

    CircleImageView profileImage;
    TextView profileName;
    ImageView backButton;
    EditText messageEdit;
    ImageButton sentButton,cameraButton;
    static RecyclerView messageRecycler;

    SharedPreferences preferencesdoctor,preferencesuser;
    String doctorMobile;
    String userMobile;
    String receiverName;
    String receiverProfile;
    static String receivernumber;
    static String sendernumber;
    String message;

    static String messageRoomPath;
    String currentUser;

    static ArrayList<MessageRetriverClass> messageList;
    static MessageAdapter cardAdapter;

    static DatabaseReference Ref=FirebaseDatabase.getInstance().getReference("MessageModule");
    DatabaseReference recentRef=FirebaseDatabase.getInstance().getReference("ChatList");
    DatabaseReference senderRef,receiverRef;
    String senderName,senderimage;
    String receiverToken,sender;
    BottomSheetDialog bottomSheetDialog;
    ImageView photoPicker,locationsharing;
    ActivityResultLauncher<String> galleryLauncher;
    FusedLocationProviderClient fusedLocationClient;

    LocationManager locationManager;
    String currentLocation;
    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("image");

    OnBackPressedDispatcher onBackPressedDispatcher;

    NetworkChangeReceiver networkChangeReceiver;

    public static Context context;

    boolean backToOnline=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_module);

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);
        context= this;


        onBackPressedDispatcher = this.getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                if (Objects.equals(sender, "doctor")){
                    Intent intent=new Intent(MessageModule.this, DoctorHomePage.class);
                    intent.putExtra("openMessageModule",true);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent=new Intent(MessageModule.this, HomePage.class);
                    intent.putExtra("openMessageModule",true);
                    startActivity(intent);
                    finish();
                }

            }
        });



        preferencesdoctor = getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        doctorMobile=preferencesdoctor.getString("DoctorMobile","");
        preferencesuser = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userMobile=preferencesuser.getString("UserMobile","");
        receivernumber=getIntent().getStringExtra("receivernumber");
        sendernumber=getIntent().getStringExtra("sendernumber");
        receiverName=getIntent().getStringExtra("receivername");
        receiverProfile=getIntent().getStringExtra("receiverprofile");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        messageList=new ArrayList<>();

        profileImage=findViewById(R.id.profile_image);
        profileName=findViewById(R.id.profile_name);
        backButton=findViewById(R.id.back_button);
        messageEdit=findViewById(R.id.messageedit);
        sentButton=findViewById(R.id.sent_button);
        messageRecycler=findViewById(R.id.chat_recycler);
        cameraButton=findViewById(R.id.camera);

        profileName.setText(receiverName);
        if (receiverProfile != null && !receiverProfile.isEmpty()){
            Log.e("profile",receiverProfile);
            Picasso.get().load(receiverProfile).into(profileImage);
        }else {
            // Handle the case when the receiverProfile is null or empty
            Picasso.get().load(R.drawable.profile).into(profileImage);
        }

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            setImage(result);
                        }
                    }
                });

        if (Objects.equals(sendernumber, doctorMobile)){

            // if it is true doctor is sender and receiver is user.
            senderRef=FirebaseDatabase.getInstance().getReference("DoctorDetails");
            receiverRef=FirebaseDatabase.getInstance().getReference("UserDetails");
            senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderName=snapshot.child(sendernumber).child("name").getValue(String.class);
                    senderimage=snapshot.child(sendernumber).child("profileImage").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            sender="doctor";
            messageRoomPath=receivernumber+sendernumber;
        }
        else if (Objects.equals(sendernumber, userMobile)) {
            // if it is true user is sender and receiver is doctor.
            sender="user";
            messageRoomPath=sendernumber+receivernumber;
            senderRef=FirebaseDatabase.getInstance().getReference("UserDetails");
            receiverRef=FirebaseDatabase.getInstance().getReference("DoctorDetails");
            senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderName=snapshot.child(sendernumber).child("name").getValue(String.class);
                    senderimage=snapshot.child(sendernumber).child("profileImage").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        cardAdapter=new MessageAdapter(messageList,sendernumber,receivernumber,this);
        messageRecycler.setLayoutManager(linearLayoutManager);
        messageRecycler.setAdapter(cardAdapter);

        if (messageRoomPath != null && messageRoomPath!= ""){
            Ref.addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(messageRoomPath)){
                        messageList.clear();
                        for (DataSnapshot data:snapshot.child(messageRoomPath).getChildren()){
                            MessageRetriverClass messageRetriver=data.getValue(MessageRetriverClass.class);
                            messageList.add(messageRetriver);
                        }
                        messageRecycler.scrollToPosition(messageList.size() - 1);
                        cardAdapter.notifyItemInserted(messageList.size()-1);
                        
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        sentButton.setOnClickListener(view -> {
            if (!messageEdit.getText().toString().isEmpty() && messageEdit != null){
                message=messageEdit.getText().toString();
                messageEdit.setText("");
                sentMessage(message);
                sentNotification(message);

            }else{
                Toast.makeText(this, "enter the message", Toast.LENGTH_SHORT).show();
            }
        });
        cameraButton.setOnClickListener(view -> {

//            galleryLauncher.launch("image/*");
            componentPickerBottomSheet();
        });

    }

    public static void loadmessage(){

    }


    private void sentNotification(String message) {


                receiverRef.child(receivernumber).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                RetriveNotificationDetails receiverDetails=task.getResult().getValue(RetriveNotificationDetails.class);
                assert receiverDetails != null;

                receiverToken=receiverDetails.getFcmToken();
                Toast.makeText(MessageModule.this, receiverToken, Toast.LENGTH_SHORT).show();
            }
        });
                senderRef.child(sendernumber).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                RetriveNotificationDetails details=task.getResult().getValue(RetriveNotificationDetails.class);
                try {
                    JSONObject jsonObject=new JSONObject();
                    JSONObject notificationObject=new JSONObject();
                    assert details != null;
                    notificationObject.put("title",details.getName());
                    notificationObject.put("body",message);

                    JSONObject dataObj=new JSONObject();
                    dataObj.put("userNum",sendernumber);
                    dataObj.put("sender",sender);
                    dataObj.put("profileImage",senderimage);
                    dataObj.put("senderName",senderName);

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

    private void sentMessage(String message) {
        String messageId= Ref.push().getKey();
        MessageRetriverClass addMessage=new MessageRetriverClass(receivernumber,message,null,null);
        messageList.add(addMessage);
//        cardAdapter.notifyItemInserted(messageList.size()-1);
        MessageLoaderClass uploadMessage=new MessageLoaderClass(receivernumber,message);
        UpdateRecentMessage receiverrecentMessage=new UpdateRecentMessage(senderName,senderimage,message,sendernumber);
        recentRef.child(receivernumber).child(sendernumber).setValue(receiverrecentMessage);
        UpdateRecentMessage senderrecentMessage=new UpdateRecentMessage(receiverName,receiverProfile,message,receivernumber);
        recentRef.child(sendernumber).child(receivernumber).setValue(senderrecentMessage);

        assert messageId != null;
        Ref.child(messageRoomPath).child(messageId).setValue(uploadMessage);

    }


    private void setImage(Uri result) {

            String imgId=UUID.randomUUID().toString();
            String messageId= Ref.push().getKey();

        StorageReference imageRef = storageReference.child(imgId + ".jpg");

            imageRef.putFile(result)
                    .addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {

                                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@android.support.annotation.NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            // Save download URL to Firebase Realtime Database using user's UID
                                            assert messageId != null;
                                            PhotoLoaderClass photoLoader=new PhotoLoaderClass(receivernumber,downloadUrl);

                                            Ref.child(messageRoomPath).child(messageId).setValue(photoLoader);
                                            MessageRetriverClass addMessage=new MessageRetriverClass(receivernumber,null,downloadUrl,null);
                                            messageList.add(addMessage);
//                                            cardAdapter.notifyItemInserted(messageList.size()-1);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(MessageModule.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private void componentPickerBottomSheet(){
        bottomSheetDialog=new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.component_picker_bottom_sheet,null);
        bottomSheetDialog.setContentView(layout);
        photoPicker=layout.findViewById(R.id.photoPicker);
        locationsharing=layout.findViewById(R.id.locationSharing);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        photoPicker.setOnClickListener(task->{
            galleryLauncher.launch("image/*");
            bottomSheetDialog.cancel();
        });
        locationsharing.setOnClickListener(task->{
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // redirect to setting to enable the gps option in settings
                OnGPS();
            } else {
                //get the current location of the user
                getCurrentLocation();
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            String messageId= Ref.push().getKey();
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                double latitudeDoublevalue=location.getLatitude();
                                double longitudeDoublevalue=location.getLongitude();

                                //convert the double value to string value to store it in firebase

                                currentLocation= latitudeDoublevalue +","+ longitudeDoublevalue;

                                LocationSharingClass data=new LocationSharingClass(currentLocation,receivernumber);
                                assert messageId != null;
                                Ref.child(messageRoomPath).child(messageId).setValue(data);

                                MessageRetriverClass addMessage=new MessageRetriverClass(receivernumber,null,null,currentLocation);
                                messageList.add(addMessage);
//                                cardAdapter.notifyItemInserted(messageList.size()-1);

                            }
                        }
                    });

        }
    }
    private void OnGPS() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS location").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
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
    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    public static boolean isMessageModuleActive() {
        return isActive;
    }
}