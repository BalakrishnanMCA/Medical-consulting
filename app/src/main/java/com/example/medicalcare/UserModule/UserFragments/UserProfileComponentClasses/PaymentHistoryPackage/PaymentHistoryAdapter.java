package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.PaymentHistoryPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.BookingAppointment.PatientPaymentHistoryClass;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.MyViewHolder> {
    ArrayList<PatientPaymentHistoryClass> paymentlist;
    Context context;

    public PaymentHistoryAdapter(ArrayList<PatientPaymentHistoryClass> paymentlist, Context context) {
        this.paymentlist = paymentlist;
        this.context = context;
    }

    @NonNull
    @Override
    public PaymentHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_history_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHistoryAdapter.MyViewHolder holder, int position) {
        PatientPaymentHistoryClass paymentData=paymentlist.get(position);

        int date=Integer.parseInt(paymentData.getPaymentDate());
        int year=date%10000;
        date=date/10000;
        int month=date%10;
        date=date/10;
        int day=date;

        String paymentdate=String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
        holder.date.setText(paymentdate);
        holder.doctorName.setText(paymentData.getDoctorName());
        holder.specilization.setText(paymentData.getSpecification());
        String[] payment=paymentData.getPaymentAmount().split(" ");
        String amount="₹"+payment[1];
        holder.paymentAmount.setText(amount);

    }

    @Override
    public int getItemCount() {
        return paymentlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date,doctorName,specilization,paymentAmount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            doctorName=itemView.findViewById(R.id.doctorName);
            specilization=itemView.findViewById(R.id.specialization);
            paymentAmount=itemView.findViewById(R.id.paymentAmount);
        }
    }
}
