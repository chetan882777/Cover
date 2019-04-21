package com.chetan.projects.cover.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.LoadedImageEntry;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.MainActivity;
import com.chetan.projects.cover.R;
import com.chetan.projects.cover.Service.CoverWallpaperSyncService;
import com.chetan.projects.cover.WallpaperDetailActivity;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PEXELS;
import static com.chetan.projects.cover.WallpaperDetailActivity.SEND_IMAGE_INFO;


public class NotificationUtils {

    private static final int SET_WALLPAPER_PENDING_INTENT_ID = 8827;
    private static final String WALLPAPER_CHANGED_NOTIFICATION_CHANNEL_ID = "wallpaper_changed_notification_channel_id";
    private static final int SET_WALLPAPER_NOTIFICATION_ID = 7727;
    public static final int ACTION_CROP_PENDING_INTENT_ID = 7777;
    public static final int ACTION_NEXT_WALLAPER_PENDING_INTENT_ID = 7778;
    public static final int ACTION_PREV_WALLAPER_PENDING_INTENT_ID = 7779;


    private static final int DOWNLOAD_WALLPAPER_PENDING_INTENT_ID = 7727;
    private static final String DOWNLOAD_WALLPAPER_NOTIFICATION_CHANNEL_ID = "download_wallpaper_notification_channel_id";
    private static final int DOWNLOAD_WALLPAPER_NOTIFICATION_ID = 7728;


    public static void remindBecauseWallpaperChanged(Context context, boolean actions){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    WALLPAPER_CHANGED_NOTIFICATION_CHANNEL_ID,
                    "wallpaper_changed_notification_channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context , WALLPAPER_CHANGED_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context , R.color.colorPrimary))
                .setLargeIcon(largeIcon(context))
                .setSmallIcon(R.drawable.ic_wallpaper_black_24dp)
                .setContentTitle("WallpaperChanged")
                .setContentText("new wallpaper have been set")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("new wallpaper have been set"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if(actions){
            notificationBuilder.addAction(switchToNextWallaperAction(context))
                    .addAction(switchToPrevWallaperAction(context));
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(SET_WALLPAPER_NOTIFICATION_ID , notificationBuilder.build());

    }

    public static void remindDownloadWallpaperChanged(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    DOWNLOAD_WALLPAPER_NOTIFICATION_CHANNEL_ID,
                    "wallpaper_changed_notification_channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context , DOWNLOAD_WALLPAPER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context , R.color.colorPrimary))
                .setLargeIcon(largeIcon(context))
                .setSmallIcon(R.drawable.ic_wallpaper_black_24dp)
                .setContentTitle("Wallpaper DOWNLOADING")
                .setContentText("new wallpaper have been set")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("new wallpaper have been set"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(DOWNLOAD_WALLPAPER_NOTIFICATION_ID , notificationBuilder.build());

    }


    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context , MainActivity.class);
        
        return PendingIntent.getActivity(context,
                SET_WALLPAPER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context){
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_wallpaper_black_24dp);
    }

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    private static NotificationCompat.Action cropNotficationAction(Context context){
        Intent cropNotificationIntent = new Intent(context, WallpaperDetailActivity.class);

        Log.v(" Notification utils -", "---------- intent created");
        LoadedImageEntry entry = AppDatabase.getInstance(context)
                .loadedImageDao()
                .loadLoadedImageEntry((int) (PreferenceUtils
                        .getCurrentWallpaperPositionPref(context) + 1 - 1));

        Log.v(" Notification utils -", "---------- entry obtained :" +entry.getPreviewUrl());

        LoadedImageInfo imageInfo = new LoadedImageInfo(entry.getLargeImageUrl(),
                entry.getMediumImageUrl(),
                entry.getPreviewUrl(),
                entry.getPageUrl(),
                entry.getUserName(),
                entry.getUserId(),
                entry.getTags(),
                LOADING_TYPE_PEXELS);

        Log.v(" Notification utils -", "---------- converted to loaded image info");


        String info = imageInfo.toString();

        Log.v(" Notification utils -", "---------- info obtained");

        cropNotificationIntent.putExtra(SEND_IMAGE_INFO, info);



        cropNotificationIntent.setAction(CoverWallpaperUtils.ACTION_CROP_NOTIFICATION);


        Log.v(" Notification utils -", "---------- intent set");
        PendingIntent cropPendingIntent = PendingIntent.getService( context,
                ACTION_CROP_PENDING_INTENT_ID,
                cropNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Log.v(" Notification utils -", "---------- pending intent created")
        ;
        NotificationCompat.Action dismissAction = new NotificationCompat.Action(0,
                "Crop",
                cropPendingIntent);

        return dismissAction;
    }



    private static NotificationCompat.Action switchToNextWallaperAction(Context context){
        Intent nextNotificationIntent = new Intent(context , CoverWallpaperSyncService.class);

        nextNotificationIntent.setAction(CoverWallpaperUtils.ACTION_SET_NEXT_WALLPAPER);

        PendingIntent dismissPendingIntent = PendingIntent.getService( context,
                ACTION_NEXT_WALLAPER_PENDING_INTENT_ID,
                nextNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action dismissAction = new NotificationCompat.Action(0,
                "Next",
                dismissPendingIntent);

        return dismissAction;
    }

    private static NotificationCompat.Action switchToPrevWallaperAction(Context context){
        Intent prevNotificationIntent = new Intent(context , CoverWallpaperSyncService.class);

        prevNotificationIntent.setAction(CoverWallpaperUtils.ACTION_SET_PREV_WALLPAPER);

        PendingIntent dismissPendingIntent = PendingIntent.getService( context,
                ACTION_PREV_WALLAPER_PENDING_INTENT_ID,
                prevNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action prevAction = new NotificationCompat.Action(0,
                "Prev",
                dismissPendingIntent);

        return prevAction;
    }
}
