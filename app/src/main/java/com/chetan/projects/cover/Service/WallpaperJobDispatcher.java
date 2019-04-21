package com.chetan.projects.cover.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.chetan.projects.cover.Utilities.PreferenceUtils;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class WallpaperJobDispatcher {

    private static int INTERVAL_HOURS = 1;
    private static int INTERVAL_MINUTES = 15;

    private static int INTERVAL_TOTAL_MINUTES = INTERVAL_MINUTES +
            (int) TimeUnit.HOURS.toMinutes(INTERVAL_HOURS);

    private static int INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(INTERVAL_TOTAL_MINUTES);

    private static int DOWNLOAD_INTERVAL_SECONDS = 30;

    private static int INTERVAL_FLEX_SECONDS = (int) TimeUnit.MINUTES.toSeconds(1);

    private static int DOWNLOAD_INTERVAL_FLEX_SECONDS = (int) TimeUnit.MINUTES.toSeconds(15);

    private static final String WALLPAPER_JOB_TAG = "wallpaper_job_tag";

    private static final String DOWNLOAD_WALLPAPER_JOB_TAG = "download_wallpaper_job_tag";

    private static boolean sInitializedWallpaperDispatcher;
    private static boolean sInitializedDownloadWallpaperDispatcher;
    private static AlarmManager mAlarmManager;
    private static PendingIntent pIntentNextWallpaper;

    synchronized public static void scheduleWallpaperJobDispatcher(Context context){
        if(sInitializedWallpaperDispatcher) return;

        Log.v("WallpaperJobDispatch","-------- sInit:" + sInitializedWallpaperDispatcher);
        setInterval(context);
        INTERVAL_SECONDS = 60;

        Driver driver = new GooglePlayDriver(context);

        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        jobDispatcher.cancelAll();

        Job wallpaperJob = jobDispatcher.newJobBuilder()
                .setService(NextWallpaperFirebaseJobService.class)
                .setTag(WALLPAPER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(INTERVAL_SECONDS ,
                        INTERVAL_SECONDS + INTERVAL_FLEX_SECONDS ))
                .setReplaceCurrent(true)
                .build();
        jobDispatcher.schedule(wallpaperJob);

        sInitializedWallpaperDispatcher = true;
    }

    synchronized public static void scheduleDownloadWallpaperJobDispatcher(Context context){
        if(sInitializedDownloadWallpaperDispatcher) return;

        setInterval(context);

        Driver driver = new GooglePlayDriver(context);

        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        jobDispatcher.cancelAll();

        Job wallpaperJob = jobDispatcher.newJobBuilder()
                .setService(DownloadWallpaperFirebaseJobService.class)
                .setTag(DOWNLOAD_WALLPAPER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(DOWNLOAD_INTERVAL_SECONDS ,
                        DOWNLOAD_INTERVAL_SECONDS + DOWNLOAD_INTERVAL_FLEX_SECONDS ))
                .setReplaceCurrent(true)
                .build();
        jobDispatcher.schedule(wallpaperJob);

        sInitializedDownloadWallpaperDispatcher = true;
    }


    public static void setInterval(Context context){
        INTERVAL_SECONDS = (int)(PreferenceUtils.getWallpaperIntervalPref(context) +1 -1);
        Log.v("WallaperJobDisptach","---------------- intervalObtained:" + INTERVAL_SECONDS);
    }

    public static void cancelAllJob(Context context){
        Driver driver = new GooglePlayDriver(context);

        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        jobDispatcher.cancelAll();
        sInitializedWallpaperDispatcher = false;
    }

}
