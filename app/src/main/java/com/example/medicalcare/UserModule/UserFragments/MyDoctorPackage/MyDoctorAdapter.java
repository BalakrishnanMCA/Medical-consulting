package com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage;

import static android.content.Context.MODE_PRIVATE;

import static com.example.medicalcare.UserModule.BookingAppointment.TimeChecker.isCorrectTime;
import static com.example.medicalcare.UserModule.BookingAppointment.TimeChecker.isTimeGreaterThanCurrent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.MessagePackage.MessageModule;
import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.BookingAppointment.TimeChecker;
import com.example.medicalcare.VideoCallPackage.repository.MainRepository;
import com.example.medicalcare.VideoCallPackage.ui.CallActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.permissionx.guolindev.PermissionX;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyDoctorAdapter extends RecyclerView.Adapter<MyDoctorAdapter.MyViewHolder> {

    ArrayList<MyDoctorClass> myDoctorlist;
    String currentUser,today;
    private MainRepository mainRepository;
    Context context;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    float rate;
    DatabaseReference ratingRef,reviewRef;


    public MyDoctorAdapter( Context context,ArrayList<MyDoctorClass> myDoctorlist, String currentUser,String today) {
        this.myDoctorlist = myDoctorlist;
        this.currentUser=currentUser;
        this.context=context;
        this.today=today;

    }

    @NonNull
    @Override
    public MyDoctorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mydoctorcard,parent,false);


        return new MyDoctorAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDoctorAdapter.MyViewHolder holder, int position) {

        MyDoctorClass classData=myDoctorlist.get(position);
        if (classData.getProfileimage() != null && !classData.getProfileimage().isEmpty() && classData.getProfileimage() != ""){
            Picasso.get().load(classData.getProfileimage()).into(holder.profileImage);
        }
        holder.docName.setText(classData.getDoctorname());
        holder.docCatagory.setText(classData.getSpecialization());
        holder.consultingDateAndTime.setText(classData.getDateandtime());
        holder.messagebtn.setOnClickListener(view -> {
            Intent intent=new Intent(view.getContext(), MessageModule.class);
            intent.putExtra("receivernumber",classData.getDoctormobile());
            intent.putExtra("sendernumber",currentUser);
            intent.putExtra("receivername",classData.getDoctorname());
            intent.putExtra("receiverprofile",classData.getProfileimage());
            view.getContext().startActivity(intent);

        });
        holder.videocallbtn.setOnClickListener(view -> {
            String[] date=classData.getDateandtime().split(" ");
            if (Objects.equals(date[0], today)){
                if (isCorrectTime(date[1])){
                    mainRepository = MainRepository.getInstance();

                    PermissionX.init((FragmentActivity) context)
                            .permissions(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
                            .request((allGranted, grantedList, deniedList) -> {
                                if (allGranted) {
                                    //login to firebase here

                                    mainRepository.login(
                                            currentUser, view.getContext().getApplicationContext(), () -> {
                                                //if success then we want to move to call activity
                                                Intent intent=new Intent(view.getContext(), CallActivity.class);
                                                intent.putExtra("userMob",currentUser);
                                                intent.putExtra("DocMob",classData.getDoctormobile());
                                                view.getContext().startActivity(intent);
                                            }
                                    );
                                }
                            });

                } else if (isTimeGreaterThanCurrent(date[1])) {
                    Toast.makeText(context, "your consulting time is "+date[1]+": you cant start consulting now.", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(context, "consulting time period is finished", Toast.LENGTH_SHORT).show();

                }
            }else{
                Toast.makeText(context, today, Toast.LENGTH_SHORT).show();

                Toast.makeText(context, "your consulting date is "+date[0]+": you cant start consulting now.", Toast.LENGTH_SHORT).show();
            }


        });
        holder.ratingbtn.setOnClickListener(task-> {

                getRating(classData.getDoctormobile(),classData.getSpecialization());

        });
        holder.docCard.setOnLongClickListener(task->{
            AlertDialog.Builder builder1=new AlertDialog.Builder(context);

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return myDoctorlist.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        CardView docCard;
        TextView docName,docCatagory,consultingDateAndTime;

        ImageView messagebtn,videocallbtn;
        Button ratingbtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            docCard=itemView.findViewById(R.id.doctorCard);
            profileImage=itemView.findViewById(R.id.doc_image);
            docName=itemView.findViewById(R.id.doc_name);
            docCatagory=itemView.findViewById(R.id.doc_catogory);
            consultingDateAndTime=itemView.findViewById(R.id.consulting_date_and_time);
            messagebtn=itemView.findViewById(R.id.message_button);
            videocallbtn=itemView.findViewById(R.id.videocall_button);
            ratingbtn=itemView.findViewById(R.id.review_button);
        }
    }
    public void getRating(String mobile,String spec){
        ratingRef= FirebaseDatabase.getInstance().getReference(spec);
        builder = new AlertDialog.Builder(context);
        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.rating_layout,null);
        builder.setView(dialogView);

        RatingBar ratingBar=dialogView.findViewById(R.id.ratingBar);
        Button ok=dialogView.findViewById(R.id.ok);
        Button cancel=dialogView.findViewById(R.id.cancel);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Display a toast message with the new rating
               rate=rating;
            }
        });
        ok.setOnClickListener(task->{
            if (rate==0){
                Toast.makeText(context, "select Rating", Toast.LENGTH_SHORT).show();
            }else{
                ratingRef.child(mobile).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String overallRating=snapshot.getValue(String.class);
                        if (overallRating!= null){
                            float overallrating=Float.parseFloat(overallRating);
                            Toast.makeText(context, String.valueOf(overallRating), Toast.LENGTH_SHORT).show();
                            float currentrating=((rate+overallrating)/2)*10;
                            float rating= (float) ((int) currentrating) /10;
                            ratingRef.child(mobile).child("rating").setValue(String.valueOf(rating)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    reviewDialog(mobile,String.valueOf(rate));
                                    Toast.makeText(context, "Rating updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                alertDialog.cancel();


            }
        });
        cancel.setOnClickListener(task->
        {
            alertDialog.cancel();
        });

        alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void showThankdialog() {

        builder = new AlertDialog.Builder(context);
        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.thank_dialog,null);
        builder.setView(dialogView);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.cancel();
            }
        });
        alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void reviewDialog(String mobile,String rating){
        SharedPreferences preferences= context.getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name=preferences.getString("name","");

        Calendar calendar = Calendar.getInstance();
       int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Month starts from 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
       String datepath= String.valueOf(day)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
        reviewRef=FirebaseDatabase.getInstance().getReference("ReviewsDoctor");
        String id=reviewRef.push().getKey();
        builder=new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.review_dialog,null);
        builder.setView(dialogView);

        EditText review=dialogView.findViewById(R.id.review_edittext);
        Button skip=dialogView.findViewById(R.id.skip);
        Button done=dialogView.findViewById(R.id.done);

        skip.setOnClickListener(task->{
            ReviewClass data=new ReviewClass(name,datepath,null,rating);
            assert id != null;
            reviewRef.child(mobile).child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    alertDialog.cancel();
                    showThankdialog();
                }
            });
        });
        done.setOnClickListener(task->{
            if (!review.getText().toString().isEmpty()){
                String reviewText=review.getText().toString();
                ReviewClass data=new ReviewClass(name,datepath,reviewText,rating);
                assert id != null;
                reviewRef.child(mobile).child(id).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        alertDialog.cancel();
                        showThankdialog();
                    }
                });
            }else {
                Toast.makeText(context, "enter the review about the doctor", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
