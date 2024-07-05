package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.HealthFilesPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.UploadPriscriptionData;
import com.example.medicalcare.R;

import java.util.ArrayList;

public class HealthFilesAdapter extends RecyclerView.Adapter<HealthFilesAdapter.MyViewHolder> {

    ArrayList<UploadPriscriptionData> priscriptionData;
    Context context;

    public HealthFilesAdapter(ArrayList<UploadPriscriptionData> priscriptionData, Context context) {
        this.priscriptionData = priscriptionData;
        this.context = context;
    }

    @NonNull
    @Override
    public HealthFilesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.health_files_card,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HealthFilesAdapter.MyViewHolder holder, int position) {
        UploadPriscriptionData cardData=priscriptionData.get(position);

        String doctorName="Doctor name: "+cardData.getDoctorName();
        holder.doctorName.setText(cardData.getDoctorName());
        holder.Date.setText(cardData.getDate());
        holder.specialization.setText(cardData.getSpecialization());
        holder.viewPrescription.setOnClickListener(task->{
            Intent intent=new Intent(context,PrescriptionDetailsActivity.class);
            intent.putExtra("prescriptionData", cardData);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return priscriptionData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName,Date,specialization;
        Button viewPrescription;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName=itemView.findViewById(R.id.doctorName);
            Date=itemView.findViewById(R.id.date);
            specialization=itemView.findViewById(R.id.specialization);
            viewPrescription=itemView.findViewById(R.id.viewPriscription);
        }
    }
}
