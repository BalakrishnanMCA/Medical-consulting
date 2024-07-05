package com.example.medicalcare.UserModule.UserCardActivities;



import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.UserModule.BookingAppointment.BookingDoctor;
import com.example.medicalcare.DoctorModule.DoctorSignup.UploadSpecializationData;
import com.example.medicalcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorCardAdapter extends RecyclerView.Adapter<DoctorCardAdapter.MyViewHolder> {

    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference dataRef=firebaseDatabase.getReference("UserFavorites");
    String usermob;
    ArrayList<UploadSpecializationData> favoriteArrayList;

    ArrayList<UploadSpecializationData> specializationData;
    Context context;
    final ArrayList<UploadSpecializationData> specializationDataList;

    public DoctorCardAdapter(ArrayList<UploadSpecializationData> specializationData,String usermob,ArrayList<UploadSpecializationData> favoriteArrayList,ArrayList<UploadSpecializationData> specializationDataList,Context context) {
       this.specializationData=specializationData;
       this.usermob=usermob;
       this.favoriteArrayList=favoriteArrayList;
       this.context=context;
       this.specializationDataList=specializationDataList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UploadSpecializationData carddata=specializationData.get(position);
        holder.docName.setText(carddata.getName());
        holder.docImg.setImageResource(R.drawable.profile);
        if(carddata.getProfileImage() != null && !carddata.getProfileImage().isEmpty()){
            Picasso.get().load(carddata.getProfileImage()).into(holder.docImg);
        }
        holder.docSpec.setText(carddata.getSpecialization());
        String rating="Rating:"+carddata.getRating();
        holder.docRating.setText(rating);
        holder.docAmount.setText(carddata.getAmount());
        String experience=carddata.getExperience()+" years of experience";
        holder.docExperience.setText(experience);


        holder.bookbtn.setOnClickListener(view -> {
            Intent intent=new Intent(view.getContext(), BookingDoctor.class);
            intent.putExtra("doctormob",carddata.getMobileNumber());
            intent.putExtra("doctorspec",carddata.getSpecialization());
            view.getContext().startActivity(intent);

        });


        holder.favorite.setImageResource(R.drawable.borderedheart);

        for (UploadSpecializationData value:favoriteArrayList) {
            String favmob=value.getMobileNumber();
            String favspec=value.getSpecialization();
            String cardmob=carddata.getMobileNumber();
            String cardspec=carddata.getSpecialization();
            if (favmob.equals(cardmob) && favspec.equals(cardspec)){
                holder.favorite.setImageResource(R.drawable.heartfilled);
            }

        }



        holder.favorite.setOnClickListener(view -> {
            Drawable heartFilledDrawable = ContextCompat.getDrawable(view.getContext(), R.drawable.heartfilled);
            assert heartFilledDrawable != null;
            String unique=carddata.getMobileNumber();
            if (usermob != null && unique != null) {
            if(Objects.equals(holder.favorite.getDrawable().getConstantState(),heartFilledDrawable.getConstantState())){
                holder.favorite.setImageResource(R.drawable.borderedheart);


                    dataRef.child(usermob).child(unique).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "doctor removed from favorite", Toast.LENGTH_SHORT).show();
                        }
                    });



            }else {
                holder.favorite.setImageResource(R.drawable.heartfilled);
                String uniquenumber = carddata.getMobileNumber();
                String specification = carddata.getSpecialization();
                if (usermob != null && uniquenumber != null) {
                    dataRef.child(usermob).child(uniquenumber).setValue(carddata).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Toast.makeText(context, "doctor added from favorite", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }


            }
        });


    }

    public void filter(String query) {
        specializationData.clear();
        if (query.isEmpty()) {
            specializationData.addAll(specializationDataList);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (UploadSpecializationData data : specializationDataList) {
                if (data.getName().toLowerCase().contains(searchQuery)) {
                    specializationData.add(data);
                }
            }
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return specializationData.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView docImg;
        TextView docName,docSpec,docRating,docAmount,docExperience;
        ImageView favorite;
        Button bookbtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            docImg=itemView.findViewById(R.id.doc_image);
            docName=itemView.findViewById(R.id.doc_name);
            docSpec=itemView.findViewById(R.id.doc_catogory);
            docRating= itemView.findViewById(R.id.doc_rating);
            docAmount=itemView.findViewById(R.id.doc_amount);
            favorite=itemView.findViewById(R.id.add_to_favorite);
            bookbtn=itemView.findViewById(R.id.book_button);
            docExperience=itemView.findViewById(R.id.doc_experience);
        }
    }

}
