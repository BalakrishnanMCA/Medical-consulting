package com.example.medicalcare.UserModule.BookingAppointment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalcare.R;
import java.util.List;
import java.util.Objects;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {

    List<String> availableTimeList;
    String selectedPosition;
     public static String selectedValue;

     public static String timepath;
    int previousSelectedPosition;



    public TimeAdapter(List<String> availableTimeList){
        this.availableTimeList=availableTimeList;
    }



    @NonNull
    @Override
    public TimeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.time_card,parent,false);
        return new TimeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeAdapter.ViewHolder holder, int position) {

        String timevalue=availableTimeList.get(position);
        holder.time.setText(timevalue);
        holder.time.setSelected(Objects.equals(selectedPosition, String.valueOf(position)));
        if (holder.time.isSelected()){
            selectedValue=timevalue;
            holder.time.setBackgroundColor( ContextCompat.getColor(holder.itemView.getContext(), R.color.formal));
        }else {
            holder.time.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white));
        }
        holder.time.setOnClickListener(view ->{
            previousSelectedPosition = selectedPosition!= null ? Integer.parseInt(selectedPosition) : RecyclerView.NO_POSITION;
            if (previousSelectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelectedPosition);
            }
            holder.time.setSelected(true);
           selectedPosition= String.valueOf(holder.getAdapterPosition());
            notifyItemChanged(holder.getAdapterPosition());

        });


    }

    @Override
    public int getItemCount() {
        return availableTimeList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.timecardid);

        }
    }
}
