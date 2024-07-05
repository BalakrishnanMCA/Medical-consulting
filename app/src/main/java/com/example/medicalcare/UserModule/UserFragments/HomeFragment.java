package com.example.medicalcare.UserModule.UserFragments;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.UserCardActivities.DoctorListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    View view;

    CircleImageView circleImageView,homeprofile;
    ImageView notification,physicianicon,skinicon,childicon,orthoicon,deititionicon,physiotherapisticon,mentalicon,womensicon,enticon,eyeicon,boneicon,teethicon,kidneyicon,stomacchicon,hearticon,lungsicon,brainicon,diabeticsicon,fertilityicon,neuroicon;

    TextView username;

    LinearLayout physiciancard,skicard,childcard,orthocard,deititioncard,physiotherapistcard,mentalcard,womenscard,entcard,eyecard,bonecard,teethcard,kidneycard,stomacchcard,heartcard,lungscard,braincard,diabeticscard,fertilitycard,neurocard;

    CardView physicianviewcard,skiviewcard,childviewcard,orthoviewcard,deititionviewcard,physiotherapistviewcard,mentalviewcard,womensviewcard,entviewcard,eyeviewcard,boneviewcard,teethviewcard,kidneyviewcard,stomacchviewcard,heartviewcard,lungsviewcard,brainviewcard,diabeticsviewcard,fertilityviewcard,neuroviewcard;

    ScrollView physicianbackLayout,skibackLayout,childbackLayout,orthobackLayout,deititionbackLayout,physiotherapistbackLayout,mentalbackLayout,womensbackLayout,entbackLayout,eyebackLayout,bonebackLayout,teethbackLayout,kidneybackLayout,stomacchbackLayout,heartbackLayout,lungsbackLayout,brainbackLayout,diabeticsbackLayout,fertilitybackLayout,neurobackLayout;

    Button setImage;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String mobile,profiletask="false";
    ProgressBar progressBar;

    StorageReference storageReference;


    AlertDialog.Builder builder;

    String profileImageUrl = "";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ActivityResultLauncher<String> galleryLauncher;
    boolean visible;


    final boolean[] physiciancardFlipped = {false},skicardFlipped = {false},childcardFlipped = {false},orthocardFlipped = {false},deititioncardFlipped = {false},physiotherapistcardFlipped = {false},mentalcardFlipped = {false},womenscardFlipped = {false},entcardFlipped = {false},eyecardFlipped = {false},bonecardFlipped = {false},teethcardFlipped = {false},kidneycardFlipped = {false},stomacchcardFlipped = {false},heartcardFlipped = {false},lungscardFlipped = {false},braincardFlipped = {false},diabeticscardFlipped = {false},fertilitycardFlipped = {false},neurocardFlipped = {false};




    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_home, container, false);

        homeprofile = view.findViewById(R.id.homeprofile);
        username=view.findViewById(R.id.username);
        preferences = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        profileImageUrl = preferences.getString("profileImageUrl", "");
        mobile=preferences.getString("UserMobile","");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails");
        storageReference= FirebaseStorage.getInstance().getReference().child("Userimage");

        String name=preferences.getString("name","");
        username.setText(name);
        if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(homeprofile);

        }else{
            loadProfileImage(null);
        }


        physiciancard=view.findViewById(R.id.physciancard);
        skicard=view.findViewById(R.id.skincard);
        childcard=view.findViewById(R.id.childcard);
        orthocard=view.findViewById(R.id.orthocard);
        deititioncard=view.findViewById(R.id.deititioncard);
        physiotherapistcard=view.findViewById(R.id.physiotherapistcard);
        mentalcard=view.findViewById(R.id.metalcard);
        womenscard=view.findViewById(R.id.womenscard);
        entcard=view.findViewById(R.id.entcard);
        eyecard=view.findViewById(R.id.eyecard);
        bonecard=view.findViewById(R.id.bonecard);
        teethcard=view.findViewById(R.id.teethcard);
        kidneycard=view.findViewById(R.id.kidneycard);
        stomacchcard=view.findViewById(R.id.stomachcard);
        heartcard=view.findViewById(R.id.heartcard);
        lungscard=view.findViewById(R.id.lungscard);
        braincard=view.findViewById(R.id.braincard);
        diabeticscard=view.findViewById(R.id.diabeticscard);
        fertilitycard=view.findViewById(R.id.fertilitycard);
        neurocard=view.findViewById(R.id.neurocard);

        physicianicon=view.findViewById(R.id.physcianicon);
        skinicon=view.findViewById(R.id.skinicon);
        childicon=view.findViewById(R.id.childicon);
        orthoicon=view.findViewById(R.id.orthoicon);
        deititionicon=view.findViewById(R.id.deititionicon);
        physiotherapisticon=view.findViewById(R.id.physiotherapisticon);
        mentalicon=view.findViewById(R.id.mentalicon);
        womensicon=view.findViewById(R.id.womensicon);
        enticon=view.findViewById(R.id.enticon);
        eyeicon=view.findViewById(R.id.eyeicon);
        boneicon=view.findViewById(R.id.boneicon);
        teethicon=view.findViewById(R.id.teethicon);
        kidneyicon=view.findViewById(R.id.kidneyicon);
        stomacchicon=view.findViewById(R.id.stomachicon);
        hearticon=view.findViewById(R.id.hearticon);
        lungsicon=view.findViewById(R.id.lungsicon);
        brainicon=view.findViewById(R.id.brainicon);
        diabeticsicon=view.findViewById(R.id.diabeticsicon);
        fertilityicon=view.findViewById(R.id.fertilityicon);
        neuroicon=view.findViewById(R.id.neuroicon);

        physicianviewcard=view.findViewById(R.id.physcianviewcard);
        skiviewcard=view.findViewById(R.id.skinviewcard);
        childviewcard=view.findViewById(R.id.childviewcard);
        orthoviewcard=view.findViewById(R.id.orthoviewcard);
        deititionviewcard=view.findViewById(R.id.deititionviewcard);
        physiotherapistviewcard=view.findViewById(R.id.physiotherapistviewcard);
        mentalviewcard=view.findViewById(R.id.metalviewcard);
        womensviewcard=view.findViewById(R.id.womensviewcard);
        entviewcard=view.findViewById(R.id.entviewcard);
        eyeviewcard=view.findViewById(R.id.eyeviewcard);
        boneviewcard=view.findViewById(R.id.boneviewcard);
        teethviewcard=view.findViewById(R.id.teethviewcard);
        kidneyviewcard=view.findViewById(R.id.kidneyviewcard);
        stomacchviewcard=view.findViewById(R.id.stomachviewcard);
        heartviewcard=view.findViewById(R.id.heartviewcard);
        lungsviewcard=view.findViewById(R.id.lungsviewcard);
        brainviewcard=view.findViewById(R.id.brainviewcard);
        diabeticsviewcard=view.findViewById(R.id.diabeticsviewcard);
        fertilityviewcard=view.findViewById(R.id.fertilityviewcard);
        neuroviewcard=view.findViewById(R.id.neuroviewcard);

        physicianbackLayout=view.findViewById(R.id.physcianbackLayout);
        skibackLayout=view.findViewById(R.id.skinbackLayout);
        childbackLayout=view.findViewById(R.id.childbackLayout);
        orthobackLayout=view.findViewById(R.id.orthobackLayout);
        deititionbackLayout=view.findViewById(R.id.deititionbackLayout);
        physiotherapistbackLayout=view.findViewById(R.id.physiobackLayout);
        mentalbackLayout=view.findViewById(R.id.mentalbackLayout);
        womensbackLayout=view.findViewById(R.id.womenbackLayout);
        entbackLayout=view.findViewById(R.id.entbackLayout);
        eyebackLayout=view.findViewById(R.id.eyebackLayout);
        bonebackLayout=view.findViewById(R.id.bonebackLayout);
        teethbackLayout=view.findViewById(R.id.teethbackLayout);
        kidneybackLayout=view.findViewById(R.id.kidneybackLayout);
        stomacchbackLayout=view.findViewById(R.id.stomachbackLayout);
        heartbackLayout=view.findViewById(R.id.heartbackLayout);
        lungsbackLayout=view.findViewById(R.id.lungsbackLayout);
        brainbackLayout=view.findViewById(R.id.brainbackLayout);
        diabeticsbackLayout=view.findViewById(R.id.diabeticsbackLayout);
        fertilitybackLayout=view.findViewById(R.id.fertilitybackLayout);
        neurobackLayout=view.findViewById(R.id.neurobackLayout);


        physiciancard.setOnClickListener(view ->{
            editor.putString("specialization","Phycisian").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        skicard.setOnClickListener(view ->{
            editor.putString("specialization","Skin").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        childcard.setOnClickListener(view ->{
            editor.putString("specialization","Childrens specialist").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        orthocard.setOnClickListener(view ->{
            editor.putString("specialization","orthopedician").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        deititioncard.setOnClickListener(view ->{
            editor.putString("specialization","Dietition").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        physiotherapistcard.setOnClickListener(view ->{
            editor.putString("specialization","physiotherapist").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        mentalcard.setOnClickListener(view ->{
            editor.putString("specialization","Mental wellness").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        womenscard.setOnClickListener(view ->{
            editor.putString("specialization","womens health").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        entcard.setOnClickListener(view ->{
            editor.putString("specialization","ENT").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        eyecard.setOnClickListener(view ->{
            editor.putString("specialization","Eyes").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        bonecard.setOnClickListener(view ->{
            editor.putString("specialization","orthopedician").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        } );
        teethcard.setOnClickListener(view ->{
            editor.putString("specialization","Teeth").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        kidneycard.setOnClickListener(view ->{
            editor.putString("specialization","kidney").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        stomacchcard.setOnClickListener(view ->{
            editor.putString("specialization","stomach").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        heartcard.setOnClickListener(view ->{
            editor.putString("specialization","heart").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        lungscard.setOnClickListener(view ->{
            editor.putString("specialization","lungs").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        braincard.setOnClickListener(view -> {
            editor.putString("specialization","brain").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        diabeticscard.setOnClickListener(view ->{
            editor.putString("specialization","diabetologist").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        fertilitycard.setOnClickListener(view ->{
            editor.putString("specialization","fertility specialist").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });
        neurocard.setOnClickListener(view ->{
            editor.putString("specialization","neurologist").apply();
            startActivity(new Intent(getActivity(), DoctorListActivity.class));
        });


        physicianicon.setOnClickListener(view -> flipCard(physicianviewcard,physiciancard,physicianbackLayout,physicianicon,physiciancardFlipped));
        skinicon.setOnClickListener(view -> flipCard(skiviewcard,skicard,skibackLayout,skinicon,skicardFlipped));
        childicon.setOnClickListener(view -> flipCard(childviewcard,childcard,childbackLayout,childicon,childcardFlipped));
        orthoicon.setOnClickListener(view -> flipCard(orthoviewcard,orthocard,orthobackLayout,orthoicon,orthocardFlipped));
        deititionicon.setOnClickListener(view -> flipCard(deititionviewcard,deititioncard,deititionbackLayout,deititionicon,deititioncardFlipped));
        physiotherapisticon.setOnClickListener(view -> flipCard(physiotherapistviewcard,physiotherapistcard,physiotherapistbackLayout,physiotherapisticon,physiciancardFlipped));
        mentalicon.setOnClickListener(view ->flipCard(mentalviewcard,mentalcard,mentalbackLayout,mentalicon,mentalcardFlipped) );
        womensicon.setOnClickListener(view -> flipCard(womensviewcard,womenscard,womensbackLayout,womensicon,womenscardFlipped));
        enticon.setOnClickListener(view -> flipCard(entviewcard,entcard,entbackLayout,enticon,entcardFlipped));
        eyeicon.setOnClickListener(view -> flipCard(eyeviewcard,eyecard,eyebackLayout,eyeicon,eyecardFlipped));
        boneicon.setOnClickListener(view -> flipCard(boneviewcard,bonecard,bonebackLayout,boneicon,bonecardFlipped));
        teethicon.setOnClickListener(view -> flipCard(teethviewcard,teethcard,teethbackLayout,teethicon,teethcardFlipped));
        kidneyicon.setOnClickListener(view -> flipCard(kidneyviewcard,kidneycard,kidneybackLayout,kidneyicon,kidneycardFlipped));
        stomacchicon.setOnClickListener(view -> flipCard(stomacchviewcard,stomacchcard,stomacchbackLayout,stomacchicon,stomacchcardFlipped));
        hearticon.setOnClickListener(view -> flipCard(heartviewcard,heartcard,heartbackLayout,hearticon,heartcardFlipped));
        lungsicon.setOnClickListener(view -> flipCard(lungsviewcard,lungscard,lungsbackLayout,lungsicon,lungscardFlipped));
        brainicon.setOnClickListener(view -> flipCard(brainviewcard,braincard,brainbackLayout,brainicon,braincardFlipped));
        diabeticsicon.setOnClickListener(view -> flipCard(diabeticsviewcard,diabeticscard,diabeticsbackLayout,diabeticsicon,diabeticscardFlipped));
        fertilityicon.setOnClickListener(view -> flipCard(fertilityviewcard,fertilitycard,fertilitybackLayout,fertilityicon,fertilitycardFlipped));
        neuroicon.setOnClickListener(view -> flipCard(neuroviewcard,neurocard,neurobackLayout,neuroicon,neurocardFlipped));




        return view;
    }




    //now initialize the onViewcreated method to call the setImageDialoge method to get profile image
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences= requireContext().getSharedPreferences("UserProfile",MODE_PRIVATE);

        editor=preferences.edit();

        String task=preferences.getString("profile","1");

        //After view has been created the method will be called for get profile image.
        // it occurs when the user have been login first time
        if (Objects.equals(task, "1")){

//            setimageDialog();
            editor.putString("profile","0");

        }




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

    //Now create the setImageDialoge to get the profile image
    private void setimageDialog() {

        builder = new AlertDialog.Builder(getContext());
        // Inflate the layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.getprofileimage, null);
        builder.setView(dialogView);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails");
        storageReference= FirebaseStorage.getInstance().getReference().child("Userimage");
        //create sharedpreferences to get the information(mobile number,profiletask,profileimageurl)
        SharedPreferences preferences = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        editor=preferences.edit();

        profileImageUrl = preferences.getString("profileImageUrl", "");
//        profiletask= preferences.getString("profiletask","");


        //check the profileimageurl,if it is not empty,it can set the image into the "homeprofile" using picasso
        if(!Objects.equals(profileImageUrl, "")){

            Picasso.get().load(profileImageUrl).into(homeprofile);

        }


        circleImageView=dialogView.findViewById(R.id.circleImageView);
        setImage=dialogView.findViewById(R.id.setimage);

        progressBar = dialogView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);






        //set a onclick event to setImage button to trigger the ActivityResultLauncher
        setImage.setOnClickListener(view -> {

            //now trigger the ActivityResultLauncher
            galleryLauncher.launch("image/*");

        });


        //now load the image from the database using loadprofileimage method
        loadProfileImage(null);

        circleImageView = dialogView.findViewById(R.id.circleImageView);




        // Set positive button (Verify)
        builder.setPositiveButton("Reload page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!Objects.equals(profileImageUrl, "")){
                    Picasso.get().load(profileImageUrl).into(homeprofile);
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
//        if (!Objects.equals(profiletask, "false")){

            alertDialog.show();
            alertDialog.setCancelable(false);
            editor.putBoolean("profiletask",false);
            editor.apply();

//        }


    }

    //create uploadimage method to upload the image to database
    private void uploadImage(Uri imageUri) {


            progressBar.setVisibility(View.VISIBLE);

            StorageReference imageRef = storageReference.child(mobile + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {

                                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@android.support.annotation.NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            // Save download URL to Firebase Realtime Database using user's UID
//                                            databaseReference.child(mobile).child("profileImage").setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    // Load and display the profile image
//                                                    loadProfileImage();
//                                                }
//                                            });
                                            loadProfileImage(downloadUrl);


                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    //loadprofileimage is used to load the image from the database
    private void loadProfileImage(String downloadURL) {
        preferences = requireActivity().getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor updatePhoto=preferences.edit();
        if (progressBar!=null){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            visible=false;
        }
        if (downloadURL==null){

            databaseReference.child(mobile).child("profileImage").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<DataSnapshot> task) {

                    if (task.isSuccessful()) {
                        String downloadUrl = task.getResult().getValue(String.class);
                        if (downloadUrl != null && !downloadUrl.isEmpty()) {
                            if (circleImageView!=null){
                                Picasso.get().load(downloadUrl).into(circleImageView);
                            }else{
                                Picasso.get().load(downloadUrl).into(homeprofile);

                            }
                            profileImageUrl=downloadUrl;
                            updatePhoto.putString("profileImageUrl",profileImageUrl).apply();
                        }
                    }
                    if (visible){
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });
        }else{
            Picasso.get().load(downloadURL).into(circleImageView);
            profileImageUrl=downloadURL;
            updatePhoto.putString("profileImageUrl",profileImageUrl).apply();
        }

    }



    //flip card is animation method to filp the catogory card
    public void flipCard(final CardView getcard,final LinearLayout frontLayout,final ScrollView backLayout,final ImageView geticon,final boolean[] getboolean) {
        ViewPropertyAnimator animator = getcard.animate();
        ViewPropertyAnimator animator2 = backLayout.animate();
        animator.rotationYBy(180f).setDuration(500);
        animator2.rotationYBy(180f).setDuration(500);
        animator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                getboolean[0] = !getboolean[0];
                updateCardLayout(frontLayout,backLayout,geticon,getboolean[0]);
            }
        });
    }

    //upload card is maintain the visibility of front card and back card
    private void updateCardLayout(final LinearLayout frontLayout,final ScrollView backLayout,final ImageView geticon,Boolean getboolean) {
        if (getboolean) {
            frontLayout.setVisibility(View.GONE);
            backLayout.setVisibility(View.VISIBLE);
            geticon.setImageResource(R.drawable.back_arrow);
        } else {
            frontLayout.setVisibility(View.VISIBLE);
            backLayout.setVisibility(View.GONE);
            geticon.setImageResource(R.drawable.ibut);
        }
    }





}