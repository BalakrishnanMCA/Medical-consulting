package com.example.medicalcare.DoctorModule.DoctorFragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorNotificationPackage.DoctorAppointmentNotificationActivity;
import com.example.medicalcare.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorHomeFragment extends Fragment {



    ActivityResultLauncher<String> galleryLauncher;

    CircleImageView circleImageView,homeprofile;
    TextView name;
    ImageView notificationButton;

    List<String> docspec;

    RecyclerView recyclerView;
    Button setImage;

    DatabaseReference databaseReference;

    String mobile,profiletask="",docspecialization="";
    ProgressBar progressBar;

    StorageReference storageReference;


    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    String profileImageUrl = "";
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    SpecializationCardAdapter cardAdapter;

    int increment=0;


    public DoctorHomeFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_doctor_home, container, false);
        preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        databaseReference= FirebaseDatabase.getInstance().getReference("DoctorDetails");

        progressDialog();
        homeprofile = view.findViewById(R.id.homeprofile);
        name=view.findViewById(R.id.doctornameview);
        recyclerView=view.findViewById(R.id.recycler);
        notificationButton=view.findViewById(R.id.notificationbtn);

        notificationButton.setOnClickListener(task->{
            Intent intent=new Intent(requireActivity(), DoctorAppointmentNotificationActivity.class);
            requireActivity().startActivity(intent);
        });

        mobile=preferences.getString("DoctorMobile","");
        String getname=preferences.getString("name","");
        name.setText(getname);

        docspec=new ArrayList<>();
        String specString=preferences.getString("doctorSpecialization","");
        docspec= Arrays.asList(specString.split(" , "));


        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),2);
        cardAdapter=new SpecializationCardAdapter(docspec);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(cardAdapter);
        alertDialog.cancel();




        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //now create the ActivityResultLauncher to get the image from the gallery.
        // This code works when the instance has been triggered.
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {

                        //the result has been pass to the uploadImage method to upload the image to database
                        uploadImage(result);

                    }
                });





    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        editor = preferences.edit();
        profileImageUrl = preferences.getString("profileImageUrl", "");
        mobile=preferences.getString("DoctorMobile","");
        profiletask= preferences.getString("Docprofiletask","");



        //show dialog to get image from the user when user login first time
        if (Objects.equals(profiletask, "1")) {
//            setimagedialog();
            editor.putString("Docprofiletask","0");
        }else if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(homeprofile);

        }else {
            loadProfileImage(null);
        }

//        getToken();

    }

    public void setimagedialog(){
        builder = new AlertDialog.Builder(getContext());
        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.getprofileimage, null);
        builder.setView(dialogView);
        circleImageView=dialogView.findViewById(R.id.circleImageView);
        setImage=dialogView.findViewById(R.id.setimage);
        progressBar = dialogView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("DoctorDetails");
        storageReference= FirebaseStorage.getInstance().getReference().child("image");
        setImage.setOnClickListener(view1 -> galleryLauncher.launch("image/*"));

        circleImageView = dialogView.findViewById(R.id.circleImageView);
        // Set positive button (Verify)
        builder.setPositiveButton("Done", (dialog, which) -> {



            if (!profileImageUrl.isEmpty()) {
                // Use Picasso or Glide to load the image
                Picasso.get().load(profileImageUrl).into(homeprofile);
                editor.putString("profileImageUrl",profileImageUrl).apply();
                databaseReference.child(mobile).child("profileImage").setValue(profileImageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireActivity(), "photo uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });


        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();


        alertDialog.show();
        alertDialog.setCancelable(false);
        editor.putString("Docprofiletask","0");
        editor.apply();


    }



    private void uploadImage(Uri imageUri) {

            progressBar.setVisibility(View.VISIBLE);
            // Use the user's UID as the unique identifier

            StorageReference imageRef = storageReference.child(mobile + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnCompleteListener(requireActivity(), task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {

                            imageRef.getDownloadUrl().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    String downloadUrl = task1.getResult().toString();
                                    // Save download URL to Firebase Realtime Database using user's UID
                                    int total=docspec.size();
                                    for (String val:docspec){
                                        increment++;

                                        DatabaseReference specializationRef = FirebaseDatabase.getInstance().getReference();
                                        specializationRef.child(val).child(mobile).child("profileImage").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if (increment==total){
//                                                    databaseReference.child(mobile).child("profileImage").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void unused) {
//                                                            // Load and display the profile image
//                                                            loadProfileImage();
//                                                        }
//                                                    });
                                                    loadProfileImage(downloadUrl);
                                                }
                                            }
                                        });
                                    }




                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    private void loadProfileImage(String downloadURL) {
        preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        editor = preferences.edit();
        if (progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
        }

        if (downloadURL==null) {


            databaseReference.child(mobile).child("profileImage").get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().getValue(String.class);
                    if (downloadUrl != null && !downloadUrl.isEmpty()) {
                        if (circleImageView != null) {
                            Picasso.get().load(downloadUrl).into(circleImageView);
                        } else {
                            Picasso.get().load(downloadUrl).into(homeprofile);
                        }
                        profileImageUrl = downloadUrl;
                        editor.putString("profileImageUrl", profileImageUrl).apply();
                    }
                }
                if (progressBar!= null){
                    if (progressBar.getVisibility()==View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

            });
        }else {
            Picasso.get().load(downloadURL).into(circleImageView);
            profileImageUrl = downloadURL;
            editor.putString("profileImageUrl", profileImageUrl).apply();


        }
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