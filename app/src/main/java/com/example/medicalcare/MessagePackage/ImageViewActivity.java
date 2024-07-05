package com.example.medicalcare.MessagePackage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medicalcare.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    ImageView loadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        loadImage=findViewById(R.id.load_image);
        String image=getIntent().getStringExtra("imagesrc");
//        Toast.makeText(this, image, Toast.LENGTH_SHORT).show();
        if (image != null && image != ""){
            Picasso.get().load(image).into(loadImage);
        }
    }
}