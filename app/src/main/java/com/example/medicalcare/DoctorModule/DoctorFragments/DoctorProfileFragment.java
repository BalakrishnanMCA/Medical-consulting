package com.example.medicalcare.DoctorModule.DoctorFragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.AlterSpecificationActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.ChangePasswordActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.ClinicLocationActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.EditProfileActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.PaymentReceivedPackage.PaymentReceivedActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.TermsAndConditionActivity;
import com.example.medicalcare.DoctorModule.DoctorSignup.DoctorLogin;
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


public class DoctorProfileFragment extends Fragment {

    CircleImageView profilepage,circleImageView;
    ActivityResultLauncher<String> galleryLauncher;

    AlertDialog.Builder builder;

    String profileImageUrl,mobile,profiletask;

    ProgressBar progressBar;
    Button setImage;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    List<String> docspec;
    LinearLayout editProfile,clinicLocation,alterSpecification,changePassword,termsAndCondition,logout,receivedPayment,sentPrescription;

    int increment=0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_doctor_profile, container, false);



        SharedPreferences preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        profileImageUrl = preferences.getString("profileImageUrl", "");
        mobile=preferences.getString("DoctorMobile","");
        profiletask= preferences.getString("Docprofiletask","");
        String getname=preferences.getString("name","");
        TextView name=view.findViewById(R.id.name);
        name.setText(getname);








        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilepage=view.findViewById(R.id.profilepage);
        editProfile=view.findViewById(R.id.edit_profile_layout);
        clinicLocation=view.findViewById(R.id.clinic_location_layout);
        alterSpecification=view.findViewById(R.id.alter_specification_layout);
        changePassword=view.findViewById(R.id.change_password_layout);
        termsAndCondition=view.findViewById(R.id.terms_and_condition_layout);
        logout=view.findViewById(R.id.logout_layout);
        receivedPayment=view.findViewById(R.id.payment_received_layout);
        sentPrescription=view.findViewById(R.id.sent_prescription_layout);


        SharedPreferences preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        profileImageUrl = preferences.getString("profileImageUrl", "");

        SharedPreferences.Editor editor=preferences.edit();

        editProfile.setOnClickListener(task -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        clinicLocation.setOnClickListener(task ->startActivity(new Intent(getActivity(), ClinicLocationActivity.class)));
        alterSpecification.setOnClickListener(task ->startActivity(new Intent(getActivity(), AlterSpecificationActivity.class)));
        changePassword.setOnClickListener(task ->startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));
        termsAndCondition.setOnClickListener(task ->startActivity(new Intent(getActivity(), TermsAndConditionActivity.class)));
        receivedPayment.setOnClickListener(task ->startActivity(new Intent(getActivity(), PaymentReceivedActivity.class)));
        sentPrescription.setOnClickListener(task->startActivity(new Intent(getActivity(), SentPrescriptionActivity.class)));
        logout.setOnClickListener(view1 -> {
            SharedPreferences logoutpref = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
            SharedPreferences.Editor editorpref = logoutpref.edit();
            editorpref.clear().apply(); // Clear all SharedPreferences values
            Intent intent = new Intent(getActivity(), DoctorLogin.class);
            startActivity(intent);
            requireActivity().finish();

        });

        if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(profilepage);

        }
        profilepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                setimageDialog();
                AlertDialog.Builder builder1=new AlertDialog.Builder(requireActivity());
                AlertDialog alertDialog=builder1.create();
                builder1.setMessage("Do you want to change image");
                builder1.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();

                    }
                });
                builder1.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setimageDialog();

                    }
                });
                builder1.show();
            }
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //now create the ActivityResultLauncher to get the image from the gallery.
        // This code works when the instance has been triggered.
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {

                //the result has been pass to the uploadImage method to upload the image to database
                uploadImage(result);

            }
        });

        SharedPreferences preferences= requireActivity().getSharedPreferences("DoctorProfile",MODE_PRIVATE);
        docspec=new ArrayList<>();
        String specString=preferences.getString("doctorSpecialization","");
        docspec= Arrays.asList(specString.split(" , "));

    }

    private void setimageDialog() {

        builder = new AlertDialog.Builder(getContext());
        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.getprofileimage, null);
        builder.setView(dialogView);

        //create sharedpreferences to get the information(mobile number,profiletask,profileimageurl)
        SharedPreferences preferences = requireActivity().getSharedPreferences("DoctorProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();

        profileImageUrl = preferences.getString("profileImageUrl", "");
        mobile=preferences.getString("DoctorMobile","");
        profiletask= preferences.getString("Docprofiletask","");


        //check the profileimageurl,if it is not empty,it can set the image into the "homeprofile" using picasso
        if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(profilepage);

        }


        circleImageView=dialogView.findViewById(R.id.circleImageView);
        setImage=dialogView.findViewById(R.id.setimage);

        progressBar = dialogView.findViewById(R.id.progressBar);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("DoctorDetails");
        storageReference= FirebaseStorage.getInstance().getReference().child("image");


        //set a onclick event to setImage button to trigger the ActivityResultLauncher
        setImage.setOnClickListener(view -> {

            //now trigger the ActivityResultLauncher
            galleryLauncher.launch("image/*");

        });


        //now load the image from the database using loadprofileimage method
                databaseReference.child(mobile).child("profileImage").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                String downloadUrl = task.getResult().getValue(String.class);
                loadProfileImage(downloadUrl);

            }
            progressBar.setVisibility(View.GONE);
        });

        circleImageView = dialogView.findViewById(R.id.circleImageView);




        // Set positive button (Verify)
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!Objects.equals(profileImageUrl, "")){
                    Picasso.get().load(profileImageUrl).into(profilepage);
                    editor.putString("profileImageUrl",profileImageUrl).apply();
                    databaseReference.child(mobile).child("profileImage").setValue(profileImageUrl);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editor.remove("profileImageUrl").apply();

            }
        });


        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();


            alertDialog.show();
            alertDialog.setCancelable(false);
            editor.putString("Docprofiletask","false");
            editor.apply();




    }

    private void uploadImage(Uri imageUri) {

        progressBar.setVisibility(View.VISIBLE);
        // Use the user's UID as the unique identifier

        StorageReference imageRef = storageReference.child(mobile + ".jpg");

        imageRef.putFile(imageUri)
                .addOnCompleteListener(requireActivity(), task -> {

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
                                            if(increment==total){
                                                loadProfileImage(downloadUrl);
                                                progressBar.setVisibility(View.GONE);
//                                                databaseReference.child(mobile).child("profileImage").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void unused) {
//                                                        // Load and display the profile image
//                                                        progressBar.setVisibility(View.GONE);
//                                                        loadProfileImage(downloadUrl);
//                                                    }
//                                                });
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

    private void loadProfileImage(String downloadUrl) {

        progressBar.setVisibility(View.VISIBLE);

        if (downloadUrl != null && !downloadUrl.isEmpty()) {
            Picasso.get().load(downloadUrl).into(circleImageView);
            profileImageUrl=downloadUrl;
            progressBar.setVisibility(View.GONE);

        }


//        databaseReference.child(mobile).child("profileImage").get().addOnCompleteListener(task -> {
//
//            if (task.isSuccessful()) {
//                String downloadUrl = task.getResult().getValue(String.class);
//
//            }
//            progressBar.setVisibility(View.GONE);
//        });

    }

}