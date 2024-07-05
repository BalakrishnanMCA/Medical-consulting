package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.NearByClinicPackage;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.UploadLocation;
import com.example.medicalcare.R;

import java.util.ArrayList;

public class NearByClinicsAdapter extends RecyclerView.Adapter<NearByClinicsAdapter.MyViewHolder> {

    ArrayList<UploadLocation> clinicLocationArrayList;
    Context context;
    Location currentLocation;
    String sourceAddress,destinationAddress;

    public NearByClinicsAdapter(ArrayList<UploadLocation> clinicLocationArrayList, Context context, Location currentLocation) {
        this.clinicLocationArrayList = clinicLocationArrayList;
        this.context = context;
        this.currentLocation=currentLocation;
    }

    @NonNull
    @Override
    public NearByClinicsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.clinic_card,parent,false);
        return new NearByClinicsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByClinicsAdapter.MyViewHolder holder, int position) {
        UploadLocation carddata=clinicLocationArrayList.get(position);
        holder.clinicName.setText(carddata.getName());
        holder.clinicAddress.setText(carddata.getAddress());
        holder.doctorDetailBtn.setOnClickListener(task->{
            Intent intent=new Intent(context, DoctorClinicActivity.class);
            intent.putExtra("ReferenceId",carddata.getMobile());
            context.startActivity(intent);
        });



        sourceAddress= currentLocation.getLatitude()+","+currentLocation.getLongitude();
        destinationAddress=carddata.getLatitude()+","+carddata.getLongitude();



        holder.navigateLocationBtn.setOnClickListener(task->{
            String uri = "https://www.google.com/maps/dir/?api=1&origin=" + sourceAddress + "&destination=" + destinationAddress;
            Intent mapintent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapintent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapintent);

        });

    }

    @Override
    public int getItemCount() {
        return clinicLocationArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Button doctorDetailBtn,navigateLocationBtn;
        TextView clinicName,clinicAddress;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            clinicName=itemView.findViewById(R.id.clinicName);
            clinicAddress=itemView.findViewById(R.id.clinicAddress);
            doctorDetailBtn=itemView.findViewById(R.id.doctorDetails);
            navigateLocationBtn=itemView.findViewById(R.id.navigateLocation);
        }
    }



}
