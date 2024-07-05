package com.example.medicalcare.DoctorModule.DoctorCardActivities;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.DoctorModule.DoctorAppointmentListClass;
import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.PriscriptionActivity;
import com.example.medicalcare.MessagePackage.MessageModule;
import com.example.medicalcare.MessagePackage.RetriveNotificationDetails;
import com.example.medicalcare.R;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.MyViewHolder> {

    ArrayList<DoctorAppointmentListClass> doctorAppointmentListClasses;
    ArrayList<DoctorAppointmentListClass> doctorAppointmentListClassesList;

    String currentUser;
    Context context;
    MainRepository mainRepository;
    AlertDialog alertDialog;
    boolean startcall=false;
    String receiverToken;
    DatabaseReference senderRef,receiverRef;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("VideoCall");



    public AppointmentListAdapter(Context context, ArrayList<DoctorAppointmentListClass> doctorAppointmentListClasses, String currentUser,ArrayList<DoctorAppointmentListClass> doctorAppointmentListClassesList)  {
        this.doctorAppointmentListClasses = doctorAppointmentListClasses;
        this.currentUser=currentUser;
        this.context=context;
        this.doctorAppointmentListClassesList=doctorAppointmentListClassesList;
    }

    @NonNull
    @Override
    public AppointmentListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_appointment_list_card,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentListAdapter.MyViewHolder holder, int position) {
        DoctorAppointmentListClass cardData=doctorAppointmentListClasses.get(position);
        if (cardData.getProfileimage() != null && !cardData.getProfileimage().isEmpty()){
            Picasso.get().load(cardData.getProfileimage()).into(holder.userImage);
        }
        holder.userName.setText(cardData.getUsername());
        holder.DocCatogory.setText(cardData.getSpecialization());
        holder.consultingDateAndTime.setText(cardData.getDateandtime());
        holder.descriptionText.setText(cardData.getDescription());
        holder.messageIcon.setOnClickListener(view -> {

            Intent intent= new Intent(view.getContext(), MessageModule.class);
            intent.putExtra("receivernumber",cardData.getUserMobile());
            intent.putExtra("sendernumber",currentUser);
            intent.putExtra("receivername",cardData.getUsername());
            intent.putExtra("receiverprofile",cardData.getProfileimage());
            view.getContext().startActivity(intent);

        });
        holder.videocallIcon.setOnClickListener(view -> {
            mainRepository = MainRepository.getInstance();

            PermissionX.init((FragmentActivity) context)
                    .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            //login to firebase here

                            mainRepository.login(
                                    currentUser, view.getContext().getApplicationContext(), () -> {
                                        alertDialog(cardData.getUserMobile());
                                        //if success then we want to move to call activity
//                                        view.getContext().startActivity(new Intent(context, CallActivity.class));
                                    }
                            );
                        }
                    });


        });
        holder.descriptionButton.setOnClickListener(view -> {
            if (holder.descriptionLayout.getVisibility() == View.VISIBLE){
                holder.descriptionLayout.setVisibility(View.GONE);
            }else {
                holder.cardLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                TransitionManager.beginDelayedTransition(holder.cardLayout,new AutoTransition());
                holder.descriptionLayout.setVisibility(View.VISIBLE);
            }
        });
        holder.priscriptionButton.setOnClickListener(view -> {
            Intent intent=new Intent(context, PriscriptionActivity.class);
            intent.putExtra("specialization",cardData.getSpecialization());
            intent.putExtra("userMobile",cardData.getUserMobile());
            context.startActivity(intent);
        });


    }

    public void filter(String query) {
        doctorAppointmentListClasses.clear();
        if (query.isEmpty()) {
            doctorAppointmentListClasses.addAll(doctorAppointmentListClassesList);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (DoctorAppointmentListClass data : doctorAppointmentListClassesList) {
                if (data.getUsername().toLowerCase().contains(searchQuery)) {
                    doctorAppointmentListClasses.add(data);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return doctorAppointmentListClasses.size();
    }

    public int alertDialog(String mob){
        startcall=true;
        int i=0;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Video call");
        builder.setMessage("Do you want to start Video call?");
        builder.setPositiveButton("start call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainRepository.sendCallRequest(mob,()->{
                    Toast.makeText(context, "Patient did not entered in waiting room", Toast.LENGTH_SHORT).show();
                    sendNotification(mob);

                });
                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("VideoCall");

                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(mob).exists()){
                            Intent intent=new Intent(context, CallActivity.class);
                            intent.putExtra("isDoctor",true);
                            context.startActivity(intent);
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
        return i;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView userName,DocCatogory,consultingDateAndTime,descriptionText;
        ImageView messageIcon,videocallIcon;
        Button descriptionButton,priscriptionButton;
        LinearLayout descriptionLayout,cardLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage=itemView.findViewById(R.id.user_image);
            userName=itemView.findViewById(R.id.user_name);
            DocCatogory=itemView.findViewById(R.id.doc_catogory);
            consultingDateAndTime=itemView.findViewById(R.id.consulting_date_and_time);
            messageIcon=itemView.findViewById(R.id.messageicon);
            videocallIcon=itemView.findViewById(R.id.videocallicon);
            descriptionButton=itemView.findViewById(R.id.description_button);
            priscriptionButton=itemView.findViewById(R.id.priscription_button);
            descriptionText=itemView.findViewById(R.id.description_text);
            descriptionLayout=itemView.findViewById(R.id.description_layout);
            cardLayout=itemView.findViewById(R.id.cardlayout);
        }
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
        senderRef.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
