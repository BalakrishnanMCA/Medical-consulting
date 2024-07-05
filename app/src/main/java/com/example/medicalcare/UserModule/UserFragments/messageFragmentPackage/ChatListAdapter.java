package com.example.medicalcare.UserModule.UserFragments.messageFragmentPackage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.MessagePackage.MessageModule;
import com.example.medicalcare.MessagePackage.UpdateRecentMessage;
import com.example.medicalcare.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    ArrayList<UpdateRecentMessage> chatList;

   Context context;
   String mobNumber;

    public ChatListAdapter(ArrayList<UpdateRecentMessage> chatList, Context context,String mobNumber) {
        this.chatList = chatList;
        this.context = context;
        this.mobNumber=mobNumber;
    }

    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_card,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {
        UpdateRecentMessage data=chatList.get(position);

        if (data.getProfileImage()!=null && !data.getProfileImage().isEmpty()){
            Picasso.get().load(data.getProfileImage()).into(holder.profile);
        }
        holder.name.setText(data.getChatlistname());
        holder.message.setText(data.getMessage());
        holder.messageCard.setOnClickListener(task->{
            Intent intent=new Intent(context, MessageModule.class);
            intent.putExtra("receivernumber",data.getMobNumber());
            intent.putExtra("sendernumber",mobNumber);
            intent.putExtra("receivername",data.getChatlistname());
            intent.putExtra("receiverprofile",data.getProfileImage());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView messageCard;
        CircleImageView profile;
        TextView name,message;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.profile);
            name=itemView.findViewById(R.id.Name);
            message=itemView.findViewById(R.id.message);
            messageCard=itemView.findViewById(R.id.messageCard);
        }
    }
}
