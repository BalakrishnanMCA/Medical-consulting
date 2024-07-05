package com.example.medicalcare.MessagePackage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicalcare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    ArrayList<MessageRetriverClass> messageList;

    String sendernumber,receivernumber;
    String address,locationAddress,currentlocation,localAddress;
    Context context;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;




    public MessageAdapter(ArrayList<MessageRetriverClass> messageList, String sendernumber, String receivernumber, Context context) {
        this.messageList = messageList;
        this.sendernumber=sendernumber;
        this.receivernumber=receivernumber;
        this.context=context;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message_card,parent,false);


        return new MessageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        MessageRetriverClass messageRetriver = messageList.get(position);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (messageRetriver.getMessage() != null){
            if (Objects.equals(sendernumber, messageRetriver.getReceiverNumber())){

                holder.receivedMessage.setText(messageRetriver.getMessage());
                holder.sentimageLayout.setVisibility(View.GONE);
                holder.receiveImageLayout.setVisibility(View.GONE);
                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.locationReceivedLayout.setVisibility(View.GONE);
                holder.locationSentLayout.setVisibility(View.GONE);
            } else if (Objects.equals(receivernumber, messageRetriver.getReceiverNumber())){
                holder.sentMessage.setText(messageRetriver.getMessage());
                holder.sentimageLayout.setVisibility(View.GONE);
                holder.receiveImageLayout.setVisibility(View.GONE);
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.locationReceivedLayout.setVisibility(View.GONE);
                holder.locationSentLayout.setVisibility(View.GONE);

            }
        } else if(messageRetriver.getProfileImage() != null) {
            if (Objects.equals(sendernumber, messageRetriver.getReceiverNumber())){
                Picasso.get().load(messageRetriver.getProfileImage()).into(holder.receiveImage);
                holder.receiveImage.setOnClickListener(view -> {
                    Intent intent=new Intent(view.getContext(), ImageViewActivity.class);
                    intent.putExtra("imagesrc",messageRetriver.getProfileImage());
                    view.getContext().startActivity(intent);


                });
                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.sentimageLayout.setVisibility(View.GONE);
                holder.locationReceivedLayout.setVisibility(View.GONE);
                holder.locationSentLayout.setVisibility(View.GONE);


            } else if (Objects.equals(receivernumber, messageRetriver.getReceiverNumber())){
                Picasso.get().load(messageRetriver.getProfileImage()).into(holder.sentImage);
                holder.sentImage.setOnClickListener(view -> {
                    Intent intent=new Intent(view.getContext(), ImageViewActivity.class);
                    intent.putExtra("imagesrc",messageRetriver.getProfileImage());
                    view.getContext().startActivity(intent);


                });
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.receiveImageLayout.setVisibility(View.GONE);
                holder.locationReceivedLayout.setVisibility(View.GONE);
                holder.locationSentLayout.setVisibility(View.GONE);

            }


        }
        else if (messageRetriver.getLocation() !=null){
            String[] location=messageRetriver.getLocation().split(",");
            locationAddress=getAddressOfLocation(location[0],location[1]);
            if (Objects.equals(sendernumber, messageRetriver.getReceiverNumber())){
                holder.locationReceived.setText(locationAddress);
                holder.locationReceived.setOnClickListener(task->{
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            // redirect to setting to enable the gps option in settings
                            OnGPS();
                        } else {

                            localAddress = getCurrentLocation();
                            //get the current location of the user
                            if (localAddress != null && locationAddress != null) {
                                String uri = "https://www.google.com/maps/dir/?api=1&origin=" + localAddress + "&destination=" + messageRetriver.getLocation();
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri)).setPackage("com.google.android.apps.maps");
                                context.startActivity(mapIntent);
                            }else{
                                System.out.println("location not work");
                            }

                        }

                });


                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.sentimageLayout.setVisibility(View.GONE);
                holder.receiveImage.setVisibility(View.GONE);
                holder.locationSentLayout.setVisibility(View.GONE);


            } else if (Objects.equals(receivernumber, messageRetriver.getReceiverNumber())){
                holder.locationsent.setText(locationAddress);
                holder.locationsent.setOnClickListener(task->{
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            // redirect to setting to enable the gps option in settings
                            OnGPS();
                        } else {

                            localAddress = getCurrentLocation();

                            //get the current location of the user
                            if (localAddress != null) {
                                String uri = "https://www.google.com/maps/dir/?api=1&origin=" + localAddress + "&destination=" + messageRetriver.getLocation();
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                mapIntent.setPackage("com.google.android.apps.maps");
                                context.startActivity(mapIntent);
                            }

                        }
                });
                holder.receivedMessageLayout.setVisibility(View.GONE);
                holder.sentMessageLayout.setVisibility(View.GONE);
                holder.receiveImageLayout.setVisibility(View.GONE);
                holder.sentImage.setVisibility(View.GONE);
                holder.locationReceivedLayout.setVisibility(View.GONE);

            }
        }


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView receivedMessageLayout,sentMessageLayout,sentimageLayout,receiveImageLayout,locationReceivedLayout,locationSentLayout;
        TextView receivedMessage,sentMessage,locationReceived,locationsent;
        ImageView sentImage,receiveImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            receivedMessageLayout=itemView.findViewById(R.id.received_message_layout);
            sentMessageLayout=itemView.findViewById(R.id.sent_message_layout);
            receivedMessage=itemView.findViewById(R.id.received_message);
            sentMessage=itemView.findViewById(R.id.sent_message);
            sentimageLayout=itemView.findViewById(R.id.sent_image_layout);
            receiveImageLayout=itemView.findViewById(R.id.receive_image_layout);
            receiveImage=itemView.findViewById(R.id.receive_image);
            sentImage=itemView.findViewById(R.id.sent_image);
            locationReceivedLayout=itemView.findViewById(R.id.locationReceivedLayout);
            locationSentLayout=itemView.findViewById(R.id.locationSentLayout);
            locationsent=itemView.findViewById(R.id.locationSentAddress);
            locationReceived=itemView.findViewById(R.id.locationReceivedAddress);



        }
    }
    private String getAddressOfLocation( String latitudeStringvalue, String longitudeStringvalue) {

        double latitudevalue=Double.parseDouble(latitudeStringvalue);
        double longitudevalue=Double.parseDouble(longitudeStringvalue);

        try {

            Geocoder geocoder=new Geocoder(context, Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(latitudevalue,longitudevalue,1);
            if (addresses!=null&&addresses.size()>0){
                address=addresses.get(0).getAddressLine(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return address;
    }
    private String getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                double latitudeDoublevalue=location.getLatitude();
                                double longitudeDoublevalue=location.getLongitude();

                                //convert the double value to string value to store it in firebase

                                currentlocation= latitudeDoublevalue +","+ longitudeDoublevalue;


                            }
                        }
                    });

        }
        return currentlocation;
    }
    private void OnGPS() {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("Enable GPS location").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
