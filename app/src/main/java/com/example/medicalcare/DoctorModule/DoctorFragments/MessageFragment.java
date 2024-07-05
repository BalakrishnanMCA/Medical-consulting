package com.example.medicalcare.DoctorModule.DoctorFragments;

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
import com.example.medicalcare.UserModule.UserFragments.messageFragmentPackage.ChatListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MessageFragment extends Fragment {
    RecyclerView ChatListRecylcer;
    ImageView backButton;

    DatabaseReference chatListRef= FirebaseDatabase.getInstance().getReference("ChatList");
    SharedPreferences preferences;
    String DocmobNumber;
    ArrayList<UpdateRecentMessage> DocchatList;
    ChatListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_message, container, false);
        ChatListRecylcer=view.findViewById(R.id.DocmessageRecycler);
        backButton=view.findViewById(R.id.backButton);
        preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        DocmobNumber=preferences.getString("DoctorMobile","");
        DocchatList=new ArrayList<>();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        listAdapter=new ChatListAdapter(DocchatList,getActivity(),DocmobNumber);
        ChatListRecylcer.setLayoutManager(linearLayoutManager);
        ChatListRecylcer.setAdapter(listAdapter);
        chatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DocchatList.clear();
                if (snapshot.hasChild(DocmobNumber)){
                    for (DataSnapshot children :snapshot.child(DocmobNumber).getChildren()) {
                        UpdateRecentMessage data = children.getValue(UpdateRecentMessage.class);
                        DocchatList.add(data);

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