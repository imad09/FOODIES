package com.foodies.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;

public class FoodiesBase extends Application {
    public static final String foodies_admin_channel = "foodies_admin_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationProcess();
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
    private void NotificationProcess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel foodies_admin_ch = new NotificationChannel(
                    foodies_admin_channel,"foodies_channel_1", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager)getSystemService(NotificationManager.class);
            manager.createNotificationChannel(foodies_admin_ch);
        }
    }
}
