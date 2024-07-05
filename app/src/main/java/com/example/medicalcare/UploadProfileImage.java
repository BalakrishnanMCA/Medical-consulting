package com.example.medicalcare;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadProfileImage extends AppCompatActivity {

    CircleImageView circleImageView;
    Button setImage;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String mobile="+917339356493";
    ProgressBar progressBar;

    FirebaseUser currentUser;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_image);
        circleImageView=findViewById(R.id.circleImageView);
        setImage=findViewById(R.id.setimage);

        progressBar = findViewById(R.id.progressBar);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("DoctorDetails");
        storageReference= FirebaseStorage.getInstance().getReference().child("image");

        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            uploadImage(result);
                        }
                    }
                });
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                galleryLauncher.launch("image/*");
            }
        });
        loadProfileImage();
    }

    private void uploadImage(Uri imageUri) {
        if (currentUser != null) {

            progressBar.setVisibility(View.VISIBLE);
            // Use the user's UID as the unique identifier
            String uid = currentUser.getUid();

            StorageReference imageRef = storageReference.child(uid + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {

                                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            // Save download URL to Firebase Realtime Database using user's UID
                                            databaseReference.child(mobile).child("profileImage").setValue(downloadUrl);

                                            // Load and display the profile image
                                            loadProfileImage();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(UploadProfileImage.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


        private void loadProfileImage() {
//            if (currentUser != null) {
                progressBar.setVisibility(View.VISIBLE);

                databaseReference.child(mobile).child("profileImage").get().addOnCompleteListener(new OnCompleteListener<com.google.firebase.database.DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.database.DataSnapshot> task) {

                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().getValue(String.class);
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                Picasso.get().load(downloadUrl).into(circleImageView);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
//            }
        }


}