package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage;

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
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.HealthFilesPackage.HealthFilesAdapter;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.HealthFilesPackage.PrescriptionDetailsActivity;

import java.util.ArrayList;

public class SentPrescriptionAdapter extends RecyclerView.Adapter<SentPrescriptionAdapter.MyViewHolder> {
    ArrayList<UploadPriscriptionData> priscriptionData;
    Context context;

    public SentPrescriptionAdapter(ArrayList<UploadPriscriptionData> priscriptionData, Context context) {
        this.priscriptionData = priscriptionData;
        this.context = context;
    }
    @NonNull
    @Override
    public SentPrescriptionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_prescription_card,parent,false);

        return new SentPrescriptionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SentPrescriptionAdapter.MyViewHolder holder, int position) {
        UploadPriscriptionData cardData=priscriptionData.get(position);

        String patientName="PatientName: "+cardData.getPatientName();
        holder.patientName.setText(patientName);
        holder.Date.setText(cardData.getDate());
        holder.specialization.setText(cardData.getSpecialization());
        holder.viewPrescription.setOnClickListener(task->{
            Intent intent=new Intent(context, PrescriptionDetailsActivity.class);
            intent.putExtra("prescriptionData", cardData);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return priscriptionData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView patientName,Date,specialization;
        Button viewPrescription;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            patientName=itemView.findViewById(R.id.patientName);
            Date=itemView.findViewById(R.id.date);
            specialization=itemView.findViewById(R.id.specialization);
            viewPrescription=itemView.findViewById(R.id.viewPriscription);
        }
    }
}
