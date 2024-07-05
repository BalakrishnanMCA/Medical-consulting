package com.example.medicalcare.UserModule.BookingAppointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage.ReviewClass;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    ArrayList<reviewRetriver> reviewList;
    Context context;

    public ReviewAdapter(ArrayList<reviewRetriver> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MyViewHolder holder, int position) {

        reviewRetriver data=reviewList.get(position);
        holder.name.setText(data.getName());
        holder.date.setText(data.getDate());
        if (data.getReview() != null){
            holder.review.setText(data.getReview());
        }else {
            holder.reviewtitle.setVisibility(View.GONE);
            holder.review.setVisibility(View.GONE);
        }
        holder.ratingBar.setRating(Float.parseFloat(data.getRating())); // Set the formatted rating
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView name,date,review,reviewtitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar=itemView.findViewById(R.id.ratingBar);
            name=itemView.findViewById(R.id.patientName);
            date=itemView.findViewById(R.id.Date);
            review=itemView.findViewById(R.id.review);
            reviewtitle=itemView.findViewById(R.id.reviewTitleText);
        }
    }
}
