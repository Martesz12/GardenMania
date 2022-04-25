package com.example.gardenmania;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANEL_ID = "shop_notification_chanel";
    private static final int NOTIFICATION_ID = 0;
    private NotificationManager mManager;
    private Context mContext;

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChanel();
    }

    private void createChanel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }
        NotificationChannel chanel = new NotificationChannel(CHANEL_ID,
                "GardenMania Notification", NotificationManager.IMPORTANCE_DEFAULT);
        chanel.enableLights(true);
        chanel.enableVibration(true);
        chanel.setLightColor(Color.CYAN);
        chanel.setDescription("Notifications from GardenMania");
        this.mManager.createNotificationChannel(chanel);
    }

    public void send(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANEL_ID)
                .setContentTitle("GardenMania")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_icon_foreground);
        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }
}
