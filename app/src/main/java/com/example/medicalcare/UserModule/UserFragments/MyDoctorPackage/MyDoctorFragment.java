package com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.R;
import com.example.medicalcare.VideoCallPackage.repository.MainRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MyDoctorFragment extends Fragment {

    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference dataRef=firebaseDatabase.getReference("MyDoctor");
    RecyclerView myDoctorRecycler;
    MyDoctorAdapter myDoctorAdapter;

    MyDoctorClass myDoctorClass;
    ArrayList<MyDoctorClass> myDoctorlist;

    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    String currentUser;

    LinearLayout incomingCallLayout;
    TextView incomingNameTV,noDataText;
    ImageView acceptButton,rejectButton;

    MainRepository mainRepository;
    int year,month,day;
    String today;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainRepository = MainRepository.getInstance();
        progressDialog();
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_doctor, container, false);
        myDoctorRecycler=view.findViewById(R.id.mydoctorrecycler);
        incomingCallLayout=view.findViewById(R.id.incomingCallLayout);
        incomingNameTV=view.findViewById(R.id.incomingNameTV);
        acceptButton=view.findViewById(R.id.acceptButton);
        rejectButton=view.findViewById(R.id.rejectButton);
        noDataText=view.findViewById(R.id.noDataText);
        SharedPreferences preferences= requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        currentUser=preferences.getString("UserMobile","");

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1; // Month starts from 0
        day = calendar.get(Calendar.DAY_OF_MONTH);
        today= day +"/"+ month +"/"+ year;

        myDoctorlist=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        myDoctorAdapter=new MyDoctorAdapter(getContext(),myDoctorlist,currentUser,today);
        myDoctorRecycler.setLayoutManager(linearLayoutManager);
        myDoctorRecycler.setAdapter(myDoctorAdapter);

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDoctorlist.clear();
                if (snapshot.hasChild(currentUser)){
                    for (DataSnapshot data:snapshot.child(currentUser).getChildren()) {
                        myDoctorClass=data.getValue(MyDoctorClass.class);
                        myDoctorlist.add(myDoctorClass);
                        myDoctorRecycler.setAdapter(myDoctorAdapter);
                        myDoctorAdapter.notifyDataSetChanged();
                        alertDialog.cancel();
                    }
                }else {
                  myDoctorRecycler.setVisibility(View.GONE);
                  noDataText.setVisibility(View.VISIBLE);
                  alertDialog.cancel();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
    public void progressDialog(){

        builder=new AlertDialog.Builder(getActivity(),R.style.TransparentAlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.progress_dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.progressdialog);

        // Set the progress tint color
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.formal), android.graphics.PorterDuff.Mode.SRC_IN);

        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}