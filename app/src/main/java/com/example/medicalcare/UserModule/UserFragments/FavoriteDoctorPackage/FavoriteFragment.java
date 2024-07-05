package com.example.medicalcare.UserModule.UserFragments.FavoriteDoctorPackage;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorSignup.UploadSpecializationData;
import com.example.medicalcare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {


    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference dataRef=firebaseDatabase.getReference("UserFavorites");


    UploadSpecializationData getDoctorData;

    ArrayList<UploadSpecializationData> doctorData;
    ArrayList<UploadSpecializationData> doctorDataList;

    FavoriteCardAdapter cardAdapter;
    RecyclerView recyclerView;

    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    TextView noDataText;
    ImageButton search;
    SearchView searchView;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView=view.findViewById(R.id.favoriterecycler);
        SharedPreferences preferences=requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        String usermob=preferences.getString("UserMobile","");

        noDataText=view.findViewById(R.id.noDataText);
        search=view.findViewById(R.id.search);
        searchView=view.findViewById(R.id.searchview);

        doctorData=new ArrayList<>();
        doctorDataList=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        cardAdapter=new FavoriteCardAdapter(doctorData,usermob,doctorDataList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cardAdapter);

        search.setOnClickListener(task->{
            if (searchView.getVisibility()==View.GONE){
                searchView.setVisibility(View.VISIBLE);
            }else{
                searchView.setVisibility(View.GONE);

            }
        });


        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctorData.clear();
                if (snapshot.hasChild(usermob)){
                    for (DataSnapshot data:snapshot.child(usermob).getChildren()) {
                        getDoctorData=data.getValue(UploadSpecializationData.class);
                        doctorData.add(getDoctorData);
                        recyclerView.setAdapter(cardAdapter);

                    }
                    doctorDataList.addAll(doctorData);
                    cardAdapter.notifyDataSetChanged();
                    alertDialog.cancel();
                }else{
                    recyclerView.setVisibility(View.GONE);
                    noDataText.setVisibility(View.VISIBLE);
                    alertDialog.cancel();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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