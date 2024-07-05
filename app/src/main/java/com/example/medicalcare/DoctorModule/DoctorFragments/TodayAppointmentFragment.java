package com.example.medicalcare.DoctorModule.DoctorFragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorAppointmentListClass;
import com.example.medicalcare.DoctorModule.DoctorCardActivities.AppointmentListAdapter;
import com.example.medicalcare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class TodayAppointmentFragment extends Fragment {

    DatabaseReference todayAppointmentRef= FirebaseDatabase.getInstance().getReference("DoctorAppointmentList");
    RecyclerView TodayAppointmentRecycler;
    ArrayList<DoctorAppointmentListClass> appointmentList;
    ArrayList<DoctorAppointmentListClass> appointmentListValues;

    DoctorAppointmentListClass doctorAppointmentListClass;
    SharedPreferences preferences;
    AppointmentListAdapter cardAdapter;

    List<String> docspec;
    String mobile;
    ImageButton search,back;
    SearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_today_appointment, container, false);
        TodayAppointmentRecycler=view.findViewById(R.id.TodayAppointmentRecycler);
        search=view.findViewById(R.id.search);
        searchView=view.findViewById(R.id.searchview);



        search.setOnClickListener(task -> {
            if (searchView.getVisibility()==View.GONE){
                searchView.setVisibility(View.VISIBLE);
            }else{
                searchView.setVisibility(View.GONE);

            }
        });
        appointmentList=new ArrayList<>();
        appointmentListValues=new ArrayList<>();
        docspec=new ArrayList<>();
        preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        String specString=preferences.getString("doctorSpecialization","");
        mobile=preferences.getString("DoctorMobile","");
        docspec= Arrays.asList(specString.split(" , "));
        Calendar calendar = Calendar.getInstance();
        int currrentyear = calendar.get(Calendar.YEAR);
        int currentmonth = calendar.get(Calendar.MONTH)+1; // Month starts from 0
        int cuurentday = calendar.get(Calendar.DAY_OF_MONTH);

        String TodayDate=cuurentday+"/"+currentmonth+"/"+currrentyear;

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        cardAdapter=new AppointmentListAdapter(getActivity(),appointmentList,mobile,appointmentListValues);
        TodayAppointmentRecycler.setLayoutManager(linearLayoutManager);
        TodayAppointmentRecycler.setAdapter(cardAdapter);
        for (String spec : docspec) {

            todayAppointmentRef.child(spec).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.hasChild(mobile)) {

                        for (DataSnapshot child : snapshot.child(mobile).getChildren()) {

                            String dateandtime = child.child("dateandtime").getValue(String.class);
                            if (dateandtime != null) {
                                String[] date = dateandtime.split(" ");

                                if (TodayDate.equals(date[0])) {
                                    doctorAppointmentListClass = child.getValue(DoctorAppointmentListClass.class);
                                    appointmentList.add(doctorAppointmentListClass);
                                    appointmentListValues.add(doctorAppointmentListClass);
                                }else {
                                    Toast.makeText(getActivity(), "Today you did not have any meeting", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        // After handling the data, notify the adapter
                        cardAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled if needed
                }
            });
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cardAdapter.filter(newText);
                return false;
            }
        });
        return view;
    }
}