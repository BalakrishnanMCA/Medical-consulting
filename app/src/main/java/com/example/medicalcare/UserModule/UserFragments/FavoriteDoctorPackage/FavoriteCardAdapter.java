package com.example.medicalcare.UserModule.UserFragments.FavoriteDoctorPackage;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.UserModule.BookingAppointment.BookingDoctor;
import com.example.medicalcare.DoctorModule.DoctorSignup.UploadSpecializationData;
import com.example.medicalcare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class FavoriteCardAdapter extends RecyclerView.Adapter<FavoriteCardAdapter.MyViewHolder>{
    ArrayList<UploadSpecializationData> specializationData;
    ArrayList<UploadSpecializationData> specializationDataArrayList;

    String usermob;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference=firebaseDatabase.getReference("UserFavorites");
    public FavoriteCardAdapter(ArrayList<UploadSpecializationData> specializationData,String usermob,ArrayList<UploadSpecializationData> specializationDataArrayList) {
        this.specializationData=specializationData;
        this.usermob=usermob;
        this.specializationDataArrayList=specializationDataArrayList;
    }

    @NonNull
    @Override
    public FavoriteCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.favoritedoctorcard,parent,false);

        return new FavoriteCardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCardAdapter.MyViewHolder holder, int position) {
        UploadSpecializationData carddata=specializationData.get(position);
        holder.docName.setText(carddata.getName());
        if(carddata.getProfileImage() != null && !carddata.getProfileImage().isEmpty()){
            Picasso.get().load(carddata.getProfileImage()).into(holder.docImg);
        }
        String spec=carddata.getSpecialization()+" and more";
        holder.docSpec.setText(spec);
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

        holder.favorite.setImageResource(R.drawable.heartfilled);
        holder.favorite.setOnClickListener(view -> {
            String unique=carddata.getMobileNumber();
            databaseReference.child(usermob).child(unique).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        notifyDataSetChanged();
                    }
                }
            });

        });



    }
    public void filter(String query) {
        specializationData.clear();
        if (query.isEmpty()) {
            specializationData.addAll(specializationDataArrayList);
        } else {
            String searchQuery = query.toLowerCase().trim();
            for (UploadSpecializationData data : specializationDataArrayList) {
                if (data.getName().toLowerCase().contains(searchQuery) ) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder{

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
