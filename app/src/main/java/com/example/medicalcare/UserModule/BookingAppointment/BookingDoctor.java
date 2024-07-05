package com.example.medicalcare.UserModule.BookingAppointment;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalcare.DoctorModule.DoctorAppointmentListClass;
import com.example.medicalcare.DoctorModule.DoctorFragments.DoctorProfileComponentClasses.SentPriscriptionPackage.SentPrescriptionActivity;
import com.example.medicalcare.DoctorModule.DoctorSignup.UploadSpecializationData;
import com.example.medicalcare.MessagePackage.MessageModule;
import com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage.MyDoctorClass;
import com.example.medicalcare.R;
import com.example.medicalcare.UserModule.UserFragments.HomePage;
import com.example.medicalcare.UserModule.UserFragments.MyDoctorPackage.ReviewClass;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingDoctor extends AppCompatActivity {


    ImageView docImage;
    TextView name,experience,counsultingFees,Rating,description,specialization,Dateview;
    Button selectDate,showReview,bookAppointment,messagebtn;
    RecyclerView timeRecycler;

    UploadSpecializationData doctorDetails;

    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference doctorspecificationRef,doctorDetailRef,DoctorAppointmentRef,DoctorAppointmentListRef,MyDoctorRef,DoctorNotificationRef,PatientPaymentHistoryRef,DoctorPaymentReceivedRef,reviewRef;

    List<String> availableTime,existingTimeList,availableTimeList;

    String doctormob,doctorspec,doctordescription,doctorAllSpecialization;

    int year,month,day;

    String datevalue=null,timevalue=null,patientDescription,dateandtime,docImg;

    TimeAdapter timeAdapter;

    LinearLayout timeLayout;

    OnBackPressedDispatcher onBackPressedDispatcher;

    boolean isDataValid=false,isDataUploadSuccess=false;

    Handler handler;
    BottomSheetDialog bottomSheetDialog;

    SharedPreferences preferences;
    String userprofileImageUrl,username,usermobile,datepath;
    AlertDialog alertDialog,progressdialog;
    AlertDialog.Builder builder;

    boolean cardValidation=true;

    String cardnum,expirydate,cvvnum;
    TextView DoctorName,spec,PatientName,PatientDescription,ConsultingDate,ConsultingTime,ConsultingAmount;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("VideoCall");

    ArrayList<reviewRetriver> reviewData;

    NetworkChangeReceiver networkChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_doctor);

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkChangeReceiver, intentFilter);


        //here handle the back button event using onBackPressedDispatcher
        onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back button press
                availableTimeList.clear();
                existingTimeList.clear();
                TimeAdapter.selectedValue = null;
                datevalue=null;
                timevalue=null;
                finish();
            }
        });


        //create the instance for handler which is used the delay the process in certain period limit
        handler = new Handler();


        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userprofileImageUrl = preferences.getString("profileImageUrl", "");
        username=preferences.getString("name","");
        usermobile=preferences.getString("UserMobile","");

        //create the intent to get the value which is passed from the previous activity
        Intent intent=getIntent();
        doctormob=intent.getStringExtra("doctormob");
        doctorspec=intent.getStringExtra("doctorspec");


        if (doctormob == null){
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
        }

        //declare the all database references here which is used in bookingDoctor Activity
        doctorspecificationRef=firebaseDatabase.getReference(doctorspec);
        doctorDetailRef=firebaseDatabase.getReference("DoctorDetails");
        DoctorAppointmentRef=firebaseDatabase.getReference("DoctorAppointments");
        DoctorAppointmentListRef=firebaseDatabase.getReference("DoctorAppointmentList");
        MyDoctorRef=firebaseDatabase.getReference("MyDoctor");
        DoctorNotificationRef=firebaseDatabase.getReference("DoctorNotification");
        PatientPaymentHistoryRef=firebaseDatabase.getReference("PatientPaymentHistory");
        DoctorPaymentReceivedRef=firebaseDatabase.getReference("DoctorPaymentReceived");
        reviewRef=FirebaseDatabase.getInstance().getReference("ReviewsDoctor").child(doctormob);


        availableTimeList = new ArrayList<>();
        availableTime =new ArrayList<>();
        existingTimeList = new ArrayList<>();
        reviewData=new ArrayList<>();




        getExsistingtime();


        //create the calender to get the today date and store it in string variable
        Calendar calendar = Calendar.getInstance();
         year = calendar.get(Calendar.YEAR);
         month = calendar.get(Calendar.MONTH)+1; // Month starts from 0
         day = calendar.get(Calendar.DAY_OF_MONTH);
         datepath= String.valueOf(day)+String.valueOf(month)+String.valueOf(year);

         //set the findviewby id for all xml component to be used in bookingDoctor activity
        docImage=findViewById(R.id.doc_img);
        name=findViewById(R.id.doc_name);
        experience=findViewById(R.id.doc_experience);
        counsultingFees=findViewById(R.id.doc_price);
        Rating=findViewById(R.id.doc_rating);
        description=findViewById(R.id.doc_description);
        specialization=findViewById(R.id.doc_spec);
        Dateview=findViewById(R.id.date_view);
        selectDate=findViewById(R.id.dateselectbtn);
        showReview=findViewById(R.id.review);
        bookAppointment=findViewById(R.id.bookappointment);
        timeRecycler=findViewById(R.id.timerecycler);
        timeLayout=findViewById(R.id.timelayout);
        messagebtn=findViewById(R.id.message);


        // get the doctor details from the database
        getDoctorDetails();

        //set onclick listener for data selecting button
        selectDate.setOnClickListener(view ->{

            // set the blink animation for this button
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            selectDate.startAnimation(animation);

            //set the handler to delay the process for button animation(Blink)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //create the datepicker dialoge to get the date from the user
                    DatePickerDialog getDate=new DatePickerDialog(BookingDoctor.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            datevalue=String.valueOf(i2)+"/"+String.valueOf(i1+1)+"/"+String.valueOf(i);
                            Dateview.setText(datevalue);

                            //get the available time by useing getavailabletime() method and pass it to the recycler adapter class
                            availableTimeList=getAvailableTime();
                            timeAdapter=new TimeAdapter(availableTimeList);
                            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(BookingDoctor.this,LinearLayoutManager.HORIZONTAL,false);
                            timeRecycler.setLayoutManager(linearLayoutManager);
                            timeRecycler.setAdapter(timeAdapter);
                            timeAdapter.notifyDataSetChanged();
                            timeLayout.setVisibility(View.VISIBLE);
                        }
                    },year,month,day);
                    getDate.show();
                }
            },500);

        });

        //set onclick listener for appointment booking button
        bookAppointment.setOnClickListener(view -> {

            // set the blink animation for this button
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            bookAppointment.startAnimation(animation);

            //set the handler to delay the process for button animation(Blink)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timevalue= TimeAdapter.selectedValue;
                    if(datevalue == null){
                        Toast.makeText(BookingDoctor.this, "select the date for consultation", Toast.LENGTH_SHORT).show();
                        isDataValid=false;
                    }else {
                        isDataValid=true;
                    }
                    if(timevalue == null){
                        Toast.makeText(BookingDoctor.this, "select the time for consultation", Toast.LENGTH_SHORT).show();
                        isDataValid=false;
                    }else {
                        isDataValid=true;
                    }
                    if(isDataValid){
                        dateandtime=datevalue+" "+timevalue;
                        descriptionBottomSheet();
                        isDataValid=false;
                    }
                }
            },500);


        });


        //set onclick listener for review button
        showReview.setOnClickListener(view -> {

            // set the blink animation for this button
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            showReview.startAnimation(animation);

            //set the handler to delay the process for button animation(Blink)
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reviewBottomSheet();
                }
            },500);



        });

        //set onclick listener for message btn
        messagebtn.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
            messagebtn.startAnimation(animation);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent=new Intent(BookingDoctor.this, MessageModule.class);
                    intent.putExtra("receivernumber",doctormob);
                    intent.putExtra("sendernumber",usermobile);
                    intent.putExtra("receivername",name.getText().toString());
                    intent.putExtra("receiverprofile",docImg);
                    startActivity(intent);

                }
            },500);
        });


    }
    private void getExsistingtime(){
        if (!existingTimeList.isEmpty()){
            existingTimeList.clear();
        }
        DoctorAppointmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if (snapshot.child(doctormob).hasChild(datepath)){

                    if (snapshot.child(doctormob).child(datepath).hasChild("_9AM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_9AM").getValue(String.class);
                        existingTimeList.add(data);
                    }
                    if (snapshot.child(doctormob).child(datepath).hasChild("_10AM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_10AM").getValue(String.class);
                        existingTimeList.add(data);
                    }
                    if (snapshot.child(doctormob).child(datepath).hasChild("_11AM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_11AM").getValue(String.class);
                        existingTimeList.add(data);
                    }
                    if (snapshot.child(doctormob).child(datepath).hasChild("_12PM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_12PM").getValue(String.class);
                        existingTimeList.add(data);
                    }
                    if (snapshot.child(doctormob).child(datepath).hasChild("_2PM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_2PM").getValue(String.class);
                        existingTimeList.add(data);
                    }
                    if (snapshot.child(doctormob).child(datepath).hasChild("_3PM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_3PM").getValue(String.class);
                        existingTimeList.add(data);
                    }
                    if (snapshot.child(doctormob).child(datepath).hasChild("_4PM")){
                        String data =snapshot.child(doctormob).child(datepath).child("_4PM").getValue(String.class);
                        existingTimeList.add(data);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private List<String> getAvailableTime(){
        List<String> timeSlots = new ArrayList<>();
        timeSlots.add("9AM");
        timeSlots.add("10AM");
        timeSlots.add("11AM");
        timeSlots.add("12PM");
        timeSlots.add("2PM");
        timeSlots.add("3PM");
        timeSlots.add("4PM");



        availableTime=new ArrayList<>(timeSlots);

        if (!existingTimeList.isEmpty()){


                for (String existingtime:existingTimeList) {
                    if (availableTime.contains(existingtime)){
                        availableTime.remove(existingtime);
                    }
                }

            return availableTime;
        }else {
            return timeSlots;
        }
    }


    private void getDoctorDetails(){
        doctorDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(doctormob)){
                    if (snapshot.child(doctormob).hasChild("description")){
                        doctordescription=snapshot.child(doctormob).child("description").getValue(String.class);
                        if (doctordescription != null && !doctordescription.equals("")){
                            description.setText(doctordescription);
                        }
                    }
                    doctorAllSpecialization=snapshot.child(doctormob).child("doctorSpecialization").getValue(String.class);
                    specialization.setText(doctorAllSpecialization);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        doctorspecificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(doctormob)){
                    doctorDetails=snapshot.child(doctormob).getValue(UploadSpecializationData.class);
                    if (doctorDetails != null){

                        name.setText("Dr."+doctorDetails.getName());
                        experience.setText(doctorDetails.getExperience()+" years experience");
                        counsultingFees.setText("Rs "+doctorDetails.getAmount());

                        Rating.setText("Rating: "+doctorDetails.getRating());

                        if (snapshot.child(doctormob).hasChild("profileImage")  ){
                            docImg=snapshot.child(doctormob).child("profileImage").getValue(String.class);
                            if (docImg != null && !docImg.equals("")){
                                Picasso.get().load(docImg).into(docImage);
                            }

                        }


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void reviewBottomSheet(){
        reviewData.clear();
        bottomSheetDialog=new BottomSheetDialog(BookingDoctor.this);
        View layout = LayoutInflater.from(BookingDoctor.this).inflate(R.layout.fragment_review_bottom_sheet,null);
        bottomSheetDialog.setContentView(layout);


        RecyclerView reviewRecycler=layout.findViewById(R.id.reviewrecycler);
        ImageView cancel=layout.findViewById(R.id.cancel_bottm_sheet);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        ReviewAdapter reviewAdapter=new ReviewAdapter(reviewData,this);
        reviewRecycler.setLayoutManager(linearLayoutManager);
        reviewRecycler.setAdapter(reviewAdapter);
        reviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot review :snapshot.getChildren()) {
                        reviewRetriver data=review.getValue(reviewRetriver.class);
                        reviewData.add(data);
                        reviewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cancel.setOnClickListener(task->{
            bottomSheetDialog.cancel();
        });


        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }
    private void descriptionBottomSheet(){
        BottomSheetDialog bottomSheet=new BottomSheetDialog(BookingDoctor.this);
        View layout = LayoutInflater.from(BookingDoctor.this).inflate(R.layout.problem_description,null);
        Button donebtn=layout.findViewById(R.id.button);
        EditText description=layout.findViewById(R.id.edittext);
        donebtn.setOnClickListener(view -> {
            patientDescription=description.getText().toString();
            if (!patientDescription.equals("")){
                bottomSheet.cancel();
                //upload the details after click the done button in bottom sheet
//                UploadUserBookingDetails();
                showConfirmationDialoge();

            }else {
                Toast.makeText(this, "enter the details", Toast.LENGTH_SHORT).show();
            }

        });
        bottomSheet.setContentView(layout);
        bottomSheet.show();

    }
    public void paymentTypeBottomSheet(){
        BottomSheetDialog bottomSheet=new BottomSheetDialog(BookingDoctor.this);
        View layout = LayoutInflater.from(BookingDoctor.this).inflate(R.layout.payment_type_layout,null);
        ImageView backButton=layout.findViewById(R.id.backButton);
        ImageView cancelButton=layout.findViewById(R.id.cancelPayment);
        RadioButton upi=layout.findViewById(R.id.upi);
        RadioButton cardPayment=layout.findViewById(R.id.cardPayment);
        Button nextButton=layout.findViewById(R.id.nextButton);
        backButton.setOnClickListener(task -> {
            bottomSheet.cancel();
        });
        cancelButton.setOnClickListener(task->{
            bottomSheet.cancel();
        });

        nextButton.setOnClickListener(task->{
            if (upi.isChecked()){
                UPIPaymentBottomSheet();
            } else if (cardPayment.isChecked()) {
                CardPaymentBottomSheet();
            }else{
                Toast.makeText(this, "select any one of the payment method", Toast.LENGTH_SHORT).show();
            }
        });


        bottomSheet.setContentView(layout);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.show();
    }

    public void UPIPaymentBottomSheet(){
        BottomSheetDialog bottomSheet=new BottomSheetDialog(BookingDoctor.this);
        View layout = LayoutInflater.from(BookingDoctor.this).inflate(R.layout.upi_payment_layout,null);
        ImageView backButton=layout.findViewById(R.id.backButton);
        ImageView cancelButton=layout.findViewById(R.id.cancelPayment);
        TextView userName=layout.findViewById(R.id.userName);
        TextView doctorName=layout.findViewById(R.id.doctorName);
        TextView amount=layout.findViewById(R.id.amount);
        Button paymentButton=layout.findViewById(R.id.paymentButton);

        userName.setText(username);
        doctorName.setText(name.getText().toString());
        amount.setText(counsultingFees.getText().toString());
        paymentButton.setOnClickListener(task->{
            UploadUserBookingDetails();
            bottomSheet.cancel();
        });
        backButton.setOnClickListener(task -> {
            bottomSheet.cancel();
            paymentTypeBottomSheet();
        });
        cancelButton.setOnClickListener(task->{
            bottomSheet.cancel();
        });



        bottomSheet.setContentView(layout);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.show();
    }
    public void CardPaymentBottomSheet(){
        BottomSheetDialog bottomSheet=new BottomSheetDialog(BookingDoctor.this);
        View layout = LayoutInflater.from(BookingDoctor.this).inflate(R.layout.card_payment_layout,null);
        ImageView backButton=layout.findViewById(R.id.backButton);
        ImageView cancelButton=layout.findViewById(R.id.cancelPayment);
        EditText cardNumber=layout.findViewById(R.id.cardNumber);
        EditText expireDate=layout.findViewById(R.id.expireDate);
        EditText cvv=layout.findViewById(R.id.cvv);
        Button proceedPayment=layout.findViewById(R.id.proceedPayment);
        Calendar calendar = Calendar.getInstance();
        int thisyear = calendar.get(Calendar.YEAR);
        int thismonth = calendar.get(Calendar.MONTH) + 1;

        backButton.setOnClickListener(task -> {
            bottomSheet.cancel();
            paymentTypeBottomSheet();
        });
        cancelButton.setOnClickListener(task->{
            bottomSheet.cancel();
        });



        proceedPayment.setOnClickListener(task->{
            cardValidation=true;
            if (!cardNumber.getText().toString().isEmpty() && !expireDate.getText().toString().isEmpty() && !cvv.getText().toString().isEmpty()){
                if (cardNumber.getText().toString().length() == 16){
                    cardnum=cardNumber.getText().toString();
                }else{
                    cardNumber.setError("incorrect card number");
                    cardValidation=false;
                }
                if (expireDate.getText().toString().contains("/")){

                    String[] parts=expireDate.getText().toString().split("/");
                   int thisyearinTwodigit=thisyear%100;
                   int year=Integer.parseInt(parts[1])%100;
                   int month=Integer.parseInt(parts[0]);
                   if (thisyearinTwodigit<year){
                       expirydate=expireDate.getText().toString();
                    }else if (thisyear == year){
                       if (thismonth<month){
                           expirydate=expireDate.getText().toString();
                       }else{
                            expireDate.setError("Card validity expired");
                           cardValidation=false;
                       }
                   }else{
                       expireDate.setError("Card validity expired");
                       cardValidation=false;
                   }
                }else{
                    expireDate.setError("separate the month and year using '/'");
                    cardValidation=false;
                }

                if (cvv.getText().toString().length()==3){
                    cvvnum=cvv.getText().toString();
                }else{
                    cvv.setError("Invalid cvv");
                    cardValidation=false;
                }

                if (cardValidation){
                    UploadUserBookingDetails();
                    bottomSheet.cancel();
                }

            }else {
                Toast.makeText(this, "enter the card details", Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheet.setContentView(layout);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.show();
    }

    private void showConfirmationDialoge() {
        builder = new AlertDialog.Builder(this);
        // Inflate the layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.confirmation_dialoge_layout,null);
        builder.setView(dialogView);
        DoctorName=dialogView.findViewById(R.id.DoctorName);
        spec=dialogView.findViewById(R.id.spec);
        PatientName=dialogView.findViewById(R.id.patientName);
        PatientDescription=dialogView.findViewById(R.id.patientDescription);
        ConsultingDate=dialogView.findViewById(R.id.consultingDate);
        ConsultingTime=dialogView.findViewById(R.id.consultingTime);
        ConsultingAmount=dialogView.findViewById(R.id.consultingAmount);

        DoctorName.setText(name.getText().toString());
        spec.setText(doctorspec);
        PatientDescription.setText(patientDescription);
        ConsultingDate.setText(datevalue);
        ConsultingTime.setText(timevalue);
        ConsultingAmount.setText(counsultingFees.getText().toString());
        PatientName.setText(username);

        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                paymentTypeBottomSheet();
                alertDialog.cancel();
            }
        });
        alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }


    public void UploadUserBookingDetails(){
        progressDialog();
        AtomicInteger operationsCompleted = new AtomicInteger(0);
        // Total number of operations to be completed
        int totalOperations = 6;
        DoctorAppointmentDetails();
        DoctorAppointmentListDetails();
        MyDoctorDetails();
//        videoCall();
        SendDoctorNotification();
        PatientPaymentHistory();
        DoctorPaymentReceived();

        ValueEventListener completionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Increment the counter for each completed operation
                operationsCompleted.incrementAndGet();
                // If all operations are completed, show success dialog
                if (operationsCompleted.get() == totalOperations) {
                    progressdialog.cancel();
                    showSuccessDialoge();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation if needed
                progressdialog.cancel();
                Toast.makeText(BookingDoctor.this, "appointment booking faild", Toast.LENGTH_SHORT).show();
            }
        };
        DoctorAppointmentRef.addValueEventListener(completionListener);
        DoctorAppointmentListRef.addValueEventListener(completionListener);
        MyDoctorRef.addValueEventListener(completionListener);
        DoctorNotificationRef.addValueEventListener(completionListener);
        PatientPaymentHistoryRef.addValueEventListener(completionListener);
        DoctorPaymentReceivedRef.addValueEventListener(completionListener);
    }

    private void DoctorPaymentReceived() {
        String uniqueId=DoctorPaymentReceivedRef.push().getKey();
        DoctorPaymentReceivedClass data=new DoctorPaymentReceivedClass(username,datepath,doctorspec,counsultingFees.getText().toString());
        assert uniqueId != null;
        DoctorPaymentReceivedRef.child(doctormob).child(uniqueId).setValue(data);

    }

    private void PatientPaymentHistory() {
        String uniqueId=DoctorPaymentReceivedRef.push().getKey();
        PatientPaymentHistoryClass data=new PatientPaymentHistoryClass(name.getText().toString(),datepath,doctorspec, counsultingFees.getText().toString());
        assert uniqueId != null;
        PatientPaymentHistoryRef.child(usermobile).child(uniqueId).setValue(data);
    }

    private void SendDoctorNotification() {
        Calendar calendar = Calendar.getInstance();
        int currrentyear = calendar.get(Calendar.YEAR);
        int currentmonth = calendar.get(Calendar.MONTH)+1; // Month starts from 0
        int cuurentday = calendar.get(Calendar.DAY_OF_MONTH);

        String sentDate=cuurentday+"/"+currentmonth+"/"+currrentyear;
        String uniqueId=DoctorPaymentReceivedRef.push().getKey();
        DoctorNotificationClass data=new DoctorNotificationClass(doctorspec,username,sentDate,dateandtime);
        assert uniqueId != null;
        DoctorNotificationRef.child(doctormob).child(uniqueId).setValue(data);

    }

    public void DoctorAppointmentDetails() {

        DoctorAppointmentRef.child(doctormob).child(datepath).child("_"+timevalue).setValue(timevalue);

        isDataUploadSuccess=true;


    }
    public void DoctorAppointmentListDetails(){

        DoctorAppointmentListClass listClass=new DoctorAppointmentListClass(username,usermobile,doctorspec,userprofileImageUrl,dateandtime,patientDescription);
        DoctorAppointmentListRef.child(doctorspec).child(doctormob).child(usermobile).setValue(listClass);
        isDataUploadSuccess=true;

    }
    public void MyDoctorDetails(){
        String docname=doctorDetails.getName();
        MyDoctorClass myDoctorClass=new MyDoctorClass(docname,doctormob,doctorspec,docImg,dateandtime);
        MyDoctorRef.child(usermobile).child(doctormob).setValue(myDoctorClass);
        isDataUploadSuccess=true;
    }

//    public void videoCall(){
//        dbRef.child(doctormob).setValue("");
//        dbRef.child(usermobile).setValue("");
//        isDataUploadSuccess=true;
//    }



    public void showSuccessDialoge(){


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.booking_success_dialog,null);
        builder.setView(dialogView);
        builder.setPositiveButton("ok", (dialogInterface, i) -> {
            alertDialog.dismiss();
            Intent intent=new Intent(this, HomePage.class);
            intent.putExtra("openMyDoctor",true);
            startActivity(intent);
        });
        alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();



    }

    public void progressDialog(){

        builder=new AlertDialog.Builder(this,R.style.TransparentAlertDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.progress_dialog, null);
        ProgressBar progressBar = view.findViewById(R.id.progressdialog);

        // Set the progress tint color
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.formal), android.graphics.PorterDuff.Mode.SRC_IN);

        builder.setView(view);

        progressdialog = builder.create();
        progressdialog.setCancelable(false);
        progressdialog.show();
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
