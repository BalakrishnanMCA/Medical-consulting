package com.example.medicalcare.DoctorModule.DoctorFragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.DoctorModule.DoctorCardActivities.RecievedAppointments;
import com.example.medicalcare.R;

import java.util.List;

public class SpecializationCardAdapter extends RecyclerView.Adapter<SpecializationCardAdapter.MyViewHolder> {

    List<String> specializationlist;



    public SpecializationCardAdapter(List<String> specializationlist) {
        this.specializationlist = specializationlist;
    }

    @NonNull
    @Override
    public SpecializationCardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.specialization_card,parent,false);

        return new SpecializationCardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecializationCardAdapter.MyViewHolder holder, int position) {
        String settext=specializationlist.get(position);
        holder.spec.setText(settext);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), RecievedAppointments.class);
                intent.putExtra("spec",settext);
                view.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return specializationlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView spec;
        CardView card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            spec=itemView.findViewById(R.id.specid);
            card=itemView.findViewById(R.id.cardview);
        }
    }
}
