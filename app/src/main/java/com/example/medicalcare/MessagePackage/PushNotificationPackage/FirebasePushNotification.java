package com.example.medicalcare.MessagePackage.PushNotificationPackage;

import static com.example.medicalcare.MessagePackage.MessageModule.isMessageModuleActive;
import static com.example.medicalcare.MessagePackage.MessageModule.loadmessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.medicalcare.MessagePackage.MessageModule;
import com.example.medicalcare.R;
import com.example.medicalcare.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebasePushNotification extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "MyNotificationChannel";

    String receivedMessage,sendernum,sender,title,body,profileImage,name;

    private final static String TAG="receicver";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d(TAG, "From: " + message.getFrom());

        if (message.getData().size()>0){
            receivedMessage = message.getData().get("message");
            sendernum = message.getData().get("userNum");
            sender=message.getData().get("sender");
            profileImage=message.getData().get("profileImage");
            name=message.getData().get("senderName");

        }
        if (message.getNotification() != null){
            title = message.getNotification().getTitle();
            body = message.getNotification().getBody();
        }
        if (!isMessageModuleActive()){
            showNotification();
        }
    }
    private void showNotification() {
        // Create a notification channel (for Android O and above)
        createNotificationChannel();

        Intent intent = new Intent(this, StartActivity.class); // Change YourActivity to the activity you want to open
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add any extras to the intent if needed
        intent.putExtra("userNum", sendernum);
        intent.putExtra("sender", sender);
        intent.putExtra("profileImage", profileImage);
        intent.putExtra("senderName", name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.child) // Set the small icon here
                .setAutoCancel(true) // Dismisses the notification when clicked
                .setContentIntent(pendingIntent); // Set the intent for when the notification is clicked

        // Get the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyNotificationChannel";
            String description = "This is my notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Customize your channel further if needed (e.g., set the LED color)
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Refreshed token: " , token);
    }


}
