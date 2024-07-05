package com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.PaymentReceivedPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.BookingAppointment.DoctorPaymentReceivedClass;
import com.example.medicalcare.UserModule.BookingAppointment.PatientPaymentHistoryClass;

import java.util.ArrayList;

public class PaymentReceivedAdapter extends RecyclerView.Adapter<PaymentReceivedAdapter.MyViewHolder> {

    ArrayList<DoctorPaymentReceivedClass> paymentlist;
    Context context;

    public PaymentReceivedAdapter(ArrayList<DoctorPaymentReceivedClass> paymentlist, Context context) {
        this.paymentlist = paymentlist;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_received_card,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DoctorPaymentReceivedClass paymentData=paymentlist.get(position);
        int date=Integer.parseInt(paymentData.getReceivedDate());
        int year=date%10000;
        date=date/10000;
        int month=date%10;
        date=date/10;
        int day=date;

        String paymentdate=String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
        holder.date.setText(paymentdate);
        holder.patientName.setText(paymentData.getPatientName());
        holder.specilization.setText(paymentData.getSpecification());
        String[] payment=paymentData.getPaymentAmount().split(" ");
        String amount="₹"+payment[1];
        holder.paymentAmount.setText(amount);



    }

    @Override
    public int getItemCount() {
        return paymentlist.size();
    }

    public static class MyViewHolder  extends RecyclerView.ViewHolder {
        TextView date,patientName,specilization,paymentAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            patientName=itemView.findViewById(R.id.patientName);
            specilization=itemView.findViewById(R.id.specialization);
            paymentAmount=itemView.findViewById(R.id.paymentAmount);
        }
    }
}
