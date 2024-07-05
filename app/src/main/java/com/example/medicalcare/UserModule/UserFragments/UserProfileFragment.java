package com.example.medicalcare.UserModule.UserFragments;

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

import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.ChangePasswordActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.EditProfileActivity;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.TermsAndConditionActivity;
import com.example.medicalcare.DoctorModule.DoctorSignup.DoctorLogin;
import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.AboutActivity;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.HealthFilesPackage.HealthFilesActivity;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.NearByClinicPackage.NearbyClinicActivity;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.PaymentHistoryPackage.PaymentHistoryActivity;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.UserChangePasswordActivity;
import com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.UserEditProfileActivity;
import com.example.medicalcare.UserModule.UserLoginSignup.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {
    CircleImageView profilepage,circleImageView;
    ActivityResultLauncher<String> galleryLauncher;

    AlertDialog.Builder builder;

    String profileImageUrl,mobile;
    String profiletask;
    ProgressBar progressBar;
    Button setImage;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    TextView name;

    LinearLayout editProfile,nearbyClinic,heathFiles,changePassword,paymentHistory,termsAndCondition,about,logout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {

                //the result has been pass to the uploadImage method to upload the image to database
                uploadImage(result);

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_profile, container, false);
        profilepage=view.findViewById(R.id.profilepage);
        editProfile=view.findViewById(R.id.edit_profile_layout);
        nearbyClinic=view.findViewById(R.id.clinic_location_layout);
        heathFiles=view.findViewById(R.id.health_files_layout);
        changePassword=view.findViewById(R.id.change_password_layout);
        paymentHistory=view.findViewById(R.id.help_and_support_layout);
        termsAndCondition=view.findViewById(R.id.terms_and_condition_layout);
        about=view.findViewById(R.id.about_layout);
        logout=view.findViewById(R.id.logout_layout);
        name=view.findViewById(R.id.name);




        SharedPreferences preferences = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        profileImageUrl = preferences.getString("profileImageUrl", "");
        mobile=preferences.getString("UserMobile","");
        String username=preferences.getString("name","");
        name.setText(username);
        if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(profilepage);

        }


        editProfile.setOnClickListener(task->{startActivity(new Intent(getActivity(), UserEditProfileActivity.class));});
        nearbyClinic.setOnClickListener(task->{startActivity(new Intent(getActivity(), NearbyClinicActivity.class));});
        heathFiles.setOnClickListener(task->{startActivity(new Intent(getActivity(), HealthFilesActivity.class));});
        changePassword.setOnClickListener(task->{startActivity(new Intent(getActivity(), UserChangePasswordActivity.class));});
        paymentHistory.setOnClickListener(task->{startActivity(new Intent(getActivity(), PaymentHistoryActivity.class));});
        termsAndCondition.setOnClickListener(task->{startActivity(new Intent(getActivity(), TermsAndConditionActivity.class));});
        about.setOnClickListener(task->{startActivity(new Intent(getActivity(), AboutActivity.class));});
        logout.setOnClickListener(view1 -> {
            SharedPreferences logoutpref = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
            SharedPreferences.Editor editorpref = logoutpref.edit();
            editorpref.clear().apply(); // Clear all SharedPreferences values
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();

        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profilepage=view.findViewById(R.id.profilepage);

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
    private void setimageDialog() {

        builder = new AlertDialog.Builder(getContext());
        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.getprofileimage, null);
        builder.setView(dialogView);

        //create sharedpreferences to get the information(mobile number,profiletask,profileimageurl)
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();

        profileImageUrl = preferences.getString("profileImageUrl", "");
        mobile=preferences.getString("UserMobile","");


        //check the profileimageurl,if it is not empty,it can set the image into the "homeprofile" using picasso
        if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(profilepage);

        }


        circleImageView=dialogView.findViewById(R.id.circleImageView);
        setImage=dialogView.findViewById(R.id.setimage);

        progressBar = dialogView.findViewById(R.id.progressBar);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails");
        storageReference= FirebaseStorage.getInstance().getReference().child("Userimage");


        //set a onclick event to setImage button to trigger the ActivityResultLauncher
        setImage.setOnClickListener(view -> {

            //now trigger the ActivityResultLauncher
            galleryLauncher.launch("image/*");

        });


        //now load the image from the database using loadprofileimage method
        loadProfileImage(null);

        circleImageView = dialogView.findViewById(R.id.circleImageView);




        // Set positive button (Verify)
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!Objects.equals(profileImageUrl, "")){
                    Picasso.get().load(profileImageUrl).into(profilepage);
                    databaseReference.child(mobile).child("profileImage").setValue(profileImageUrl);
                    editor.putString("profileImageUrl",profileImageUrl).apply();
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
        editor.putBoolean("profiletask",false);
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

                                loadProfileImage(downloadUrl);


                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadProfileImage(String downloadURL) {

        progressBar.setVisibility(View.VISIBLE);

        if (downloadURL==null) {
            databaseReference.child(mobile).child("profileImage").get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().getValue(String.class);
                    if (downloadUrl != null && !downloadUrl.isEmpty()) {
                        Picasso.get().load(downloadUrl).into(circleImageView);
                        profileImageUrl = downloadUrl;

                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }else{

            profileImageUrl=downloadURL;
            Picasso.get().load(downloadURL).into(circleImageView);
            progressBar.setVisibility(View.GONE);

        }

    }
}