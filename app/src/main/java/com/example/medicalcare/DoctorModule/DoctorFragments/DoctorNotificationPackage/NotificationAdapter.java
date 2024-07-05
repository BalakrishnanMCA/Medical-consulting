package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorNotificationPackage;

import android.content.Context;
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
import com.example.medicalcare.UserModule.BookingAppointment.DoctorNotificationClass;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    ArrayList<DoctorNotificationClass> notificationlist;
    Context context;

    public NotificationAdapter(ArrayList<DoctorNotificationClass> notificationlist, Context context) {
        this.notificationlist = notificationlist;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_notification_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {
        DoctorNotificationClass notification=notificationlist.get(position);

        String notificationContent="New appointment received on: "+notification.getSentDate();
        holder.notificationText.setText(notificationContent);
        holder.patientName.setText(notification.getPatientName());
        holder.specification.setText(notification.getSpecification());
        holder.dateandtime.setText(notification.getAppointmentDate());
        holder.notificationCard.setOnClickListener(task->{
            Intent intent=new Intent(context, RecievedAppointments.class);
            intent.putExtra("spec",notification.getSpecification());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView notificationCard;
        TextView notificationText,patientName,specification,dateandtime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationCard=itemView.findViewById(R.id.notificationCard);
            notificationText=itemView.findViewById(R.id.notificationText);
            patientName=itemView.findViewById(R.id.patientName);
            specification=itemView.findViewById(R.id.specification);
            dateandtime=itemView.findViewById(R.id.dateandtime);
        }
    }
}
