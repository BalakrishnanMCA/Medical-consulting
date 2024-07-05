package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.R;

import java.util.List;

public class SpecializationAdapter extends RecyclerView.Adapter<SpecializationAdapter.MyViewHolder> {

    List<String> docspecialization;

    public SpecializationAdapter(List<String> docspecialization) {
        this.docspecialization = docspecialization;
    }

    @NonNull
    @Override
    public SpecializationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.alter_sec_card,parent,false);

        return new SpecializationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecializationAdapter.MyViewHolder holder, int position) {

        String data=docspecialization.get(position);
        holder.specvalue.setText(data);
    }

    @Override
    public int getItemCount() {
        return docspecialization.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView specvalue;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            specvalue=itemView.findViewById(R.id.timecardid);
        }
    }
}
