package com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.R;

import java.util.ArrayList;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.MyViewHolder> {

    ArrayList<MedicineList> medicineLists;
    Context context;

    public MedicineListAdapter(ArrayList<MedicineList> medicineLists, Context context) {
        this.medicineLists = medicineLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MedicineListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_list_card,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineListAdapter.MyViewHolder holder, int position) {

        MedicineList value=medicineLists.get(position);
        holder.medicineName.setText(value.getName());
        holder.days.setText(value.getDays());
        if (value.isMorning()){
            holder.session1.setBackgroundResource(R.drawable.check);
        }else {
            holder.session1.setBackgroundResource(R.drawable.baseline_cancel_24);
        }
        if (value.isAfternoon()){
            holder.session2.setBackgroundResource(R.drawable.check);
        }else {
            holder.session2.setBackgroundResource(R.drawable.baseline_cancel_24);
        }
        if (value.isNight()){
            holder.session3.setBackgroundResource(R.drawable.check);

        }else {
            holder.session3.setBackgroundResource(R.drawable.baseline_cancel_24);
        }

    }

    @Override
    public int getItemCount() {
        return medicineLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView medicineName,days;
        ImageView session1,session2,session3;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName=itemView.findViewById(R.id.medicineName);
            days=itemView.findViewById(R.id.days);
            session1=itemView.findViewById(R.id.session1);
            session2=itemView.findViewById(R.id.session2);
            session3=itemView.findViewById(R.id.session3);
        }
    }
}
