package com.example.medicalcare.UserModule.UserFragments.UserProfileComponentClasses.HealthFilesPackage;

import static android.content.ContentValues.TAG;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.MedicineList;
import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.MedicineListAdapter;
import com.example.medicalcare.DoctorModule.DoctorCardActivities.PriscriptonPackage.UploadPriscriptionData;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.example.medicalcare.databinding.ActivityPrescriptionDetailsBinding;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import org.aviran.cookiebar2.CookieBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

public class PrescriptionDetailsActivity extends AppCompatActivity {

    ActivityPrescriptionDetailsBinding views;
    UploadPriscriptionData prescriptionDetails;
    ArrayList<MedicineList> medicineList;
    MedicineListAdapter medicineListAdapter;
    private ActivityResultLauncher<Intent> storageAccessLauncher;
    NetworkChangeReceiver networkChangeReceiver;
    OnBackPressedDispatcher onBackPressedDispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views=ActivityPrescriptionDetailsBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);
        onBackPressedDispatcher = this.getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                finish();

            }
        });
        views.backButton.setOnClickListener(task->{
            onBackPressedDispatcher.onBackPressed();
        });

        Intent intent=getIntent();
        prescriptionDetails=(UploadPriscriptionData) intent.getSerializableExtra("prescriptionData");
        storageAccessLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                Uri treeUri = data.getData();
                assert treeUri != null;
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                if (pickedDir != null && pickedDir.isDirectory()) {
                    createPdf(pickedDir);
                }
            }
        });
        views.docName.setText(prescriptionDetails.getDoctorName());
        views.date.setText(prescriptionDetails.getDate());
        views.patientName.setText(prescriptionDetails.getPatientName());
        views.patientAge.setText(prescriptionDetails.getPatientAge());
        views.patientGender.setText(prescriptionDetails.getPatientGender());

        medicineList=prescriptionDetails.getMedicine();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        medicineListAdapter=new MedicineListAdapter(medicineList,this);
        views.medicineRecycler.setLayoutManager(linearLayoutManager);
        views.medicineRecycler.setAdapter(medicineListAdapter);
        medicineListAdapter.notifyDataSetChanged();



        views.createPdfButton.setOnClickListener(task->{
            if (isPermissionGranted()){
                requestStorageAccess();
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(permission()){
                    requestStorageAccess();
                }
            }
        });



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with PDF generation
                requestStorageAccess();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied, cannot write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void requestStorageAccess() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        storageAccessLauncher.launch(intent);
    }
    private void createPdf(DocumentFile directory) {
        if (directory == null || !directory.isDirectory()) {
            Toast.makeText(this, "Invalid directory", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileName = prescriptionDetails.getDoctorName() + ".pdf";
        DocumentFile pdfFile = directory.createFile("application/pdf", fileName);

        if (pdfFile == null) {
            Toast.makeText(this, "Failed to create PDF file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(pdfFile.getUri(), "w");
            if (pfd != null) {
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                PdfWriter writer = new PdfWriter(fos);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);




                // Add the header information
                document.add(new Paragraph("PRESCRIPTION DOCUMENT").setTextAlignment(TextAlignment.CENTER).setFontSize(24));
                document.add(new Paragraph("Doctor Name: " + prescriptionDetails.getDoctorName()).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(18).setMarginTop(20));
                document.add(new Paragraph("Date: " + prescriptionDetails.getDate()).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(18));
                document.add(new Paragraph("Patient Name: " + prescriptionDetails.getPatientName()).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(18));
                document.add(new Paragraph("Patient Age: " + prescriptionDetails.getPatientAge()).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(18));
                document.add(new Paragraph("Patient Gender: " + prescriptionDetails.getPatientGender()).setVerticalAlignment(VerticalAlignment.MIDDLE).setFontSize(18));

                // Create table for medicines
                Table medicineTable = new Table(5).setVerticalAlignment(VerticalAlignment.MIDDLE); // 5 columns
                medicineTable.addCell(new Cell().add(new Paragraph("Name")).setFontSize(18).setBold().setUnderline());
                medicineTable.addCell(new Cell().add(new Paragraph("Morning")).setFontSize(18).setBold().setUnderline());
                medicineTable.addCell(new Cell().add(new Paragraph("Afternoon")).setFontSize(18).setBold().setUnderline());
                medicineTable.addCell(new Cell().add(new Paragraph("Night")).setFontSize(18).setBold().setUnderline());
                medicineTable.addCell(new Cell().add(new Paragraph("Days")).setFontSize(18).setBold().setUnderline());



                // Add medicine details to the table
                for (MedicineList medicine : medicineList) {
                    medicineTable.addCell(new Cell().add(new Paragraph(medicine.getName())).setFontSize(18));
                    medicineTable.addCell(new Cell().add(new Paragraph(medicine.isMorning() ? "1" : "0")).setFontSize(18));
                    medicineTable.addCell(new Cell().add(new Paragraph(medicine.isAfternoon() ? "1" : "0")).setFontSize(18));
                    medicineTable.addCell(new Cell().add(new Paragraph(medicine.isNight() ? "1" : "0")).setFontSize(18));
                    medicineTable.addCell(new Cell().add(new Paragraph(medicine.getDays())).setFontSize(18));
                }

                document.add(medicineTable);


                document.add(new Paragraph("Digitaly signed by: \n" + prescriptionDetails.getDoctorName()).setTextAlignment(TextAlignment.RIGHT).setFontSize(18).setMarginTop(20));

                document.close();

                Toast.makeText(this, "PDF generated successfully", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isPermissionGranted(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
           return false;
        } else {
            return true;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean permission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 123);
            return true;
        }else return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister NetworkChangeReceiver
        unregisterReceiver(networkChangeReceiver);
    }



    private class NetworkChangeReceiver extends BroadcastReceiver {
        private boolean wasConnected = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = isNetworkConnected(context);

            if (isConnected != wasConnected) {
                if (isConnected) {
                    CookieBar.build((Activity) context)
                            .setTitle("Network Status")
                            .setMessage("Back to online")
                            .show();
                } else {
                    CookieBar.build((Activity) context)
                            .setTitle("Network Status")
                            .setMessage("No internet Connection")
                            .show();
                }
                wasConnected = isConnected;
            }
        }
    }

    // Method to check network connectivity
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


}