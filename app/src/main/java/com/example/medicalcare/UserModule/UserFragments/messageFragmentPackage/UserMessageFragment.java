package com.example.medicalcare.UserModule.UserFragments.messageFragmentPackage;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medicalcare.MessagePackage.UpdateRecentMessage;
import com.example.medicalcare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserMessageFragment extends Fragment {



   RecyclerView ChatListRecylcer;
   ImageView backButton;

   DatabaseReference chatListRef= FirebaseDatabase.getInstance().getReference("ChatList");
   SharedPreferences preferences;
   String mobNumber;
   ArrayList<UpdateRecentMessage> chatList;
   ChatListAdapter listAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_message, container, false);
        ChatListRecylcer=view.findViewById(R.id.messageRecycler);
        backButton=view.findViewById(R.id.backButton);
        preferences = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        mobNumber=preferences.getString("UserMobile","");
        chatList=new ArrayList<>();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        listAdapter=new ChatListAdapter(chatList,getActivity(),mobNumber);
        ChatListRecylcer.setLayoutManager(linearLayoutManager);
        ChatListRecylcer.setAdapter(listAdapter);
        chatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                if (snapshot.hasChild(mobNumber)){
                    for (DataSnapshot children :snapshot.child(mobNumber).getChildren()) {
                        UpdateRecentMessage data = children.getValue(UpdateRecentMessage.class);
                        chatList.add(data);

                    }
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}