package com.chetan.projects.cover.Service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.LoadedImageEntry;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.LoadedImagesUtils;
import com.chetan.projects.cover.Utilities.NotificationUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;
import com.chetan.projects.cover.WallpaperActivity;
import com.chetan.projects.cover.WallpaperDetailActivity;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PEXELS;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_NEXT_WALLPAPER;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_PREV_WALLPAPER;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_WALLPAPERS;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_HEIGHT;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_HEIGHT_DAFAULT;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_WIDTH;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_WIDTH_DEFAULT;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.RECENT_DOWNLOADS_WALLPAPERS;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.executeTask;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.executeTaskFromDownloads;
import static com.chetan.projects.cover.WallpaperDetailActivity.SEND_IMAGE_INFO;

public class CoverWallpaperSyncService extends IntentService {

    public CoverWallpaperSyncService() {
        super("CoverWallpaperSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if(action.equals(ACTION_SET_WALLPAPERS)) {

            Log.v("-------------" , "----------- action wallpapers");

            setWallapersExecution(intent, action);
        }else if(action.equals(ACTION_SET_NEXT_WALLPAPER)){
            setWallapersExecution(intent , action);
        }else if(action.equals(ACTION_SET_PREV_WALLPAPER)){
            setWallapersExecution(intent , action);
        }else if(action.equals(CoverWallpaperUtils.ACTION_RECENT_WALLPAPERS)){
            setWallapersExecution(intent, action);
        }else if (action.equals(CoverWallpaperUtils.ACTION_RECENT_DOWNLOADS_WALLPAPERS)){
            setDownloadsWallapersExecution(intent, action);
        }
    }



    private void setWallapersExecution(Intent intent, String action) {

        int width = intent.getIntExtra(ACTION_SET_WALLPAPER_SCREEN_WIDTH , ACTION_SET_WALLPAPER_SCREEN_WIDTH_DEFAULT);

        int height = intent.getIntExtra(ACTION_SET_WALLPAPER_SCREEN_HEIGHT, ACTION_SET_WALLPAPER_SCREEN_HEIGHT_DAFAULT);

        String server = intent.getStringExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SERVER);

        Log.v("-------------" , "----------- server :" + server);


        String searchString = intent.getStringExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING);

        //Log.v("-------------", "----------- info :" + info);

        //ArrayList<LoadedImageInfo> loadedImagesInfo = LoadedImagesUtils.createLoadedInagesInfo(
        //      intent.getStringArrayListExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPERS_INFO));

        executeTask(this, action , width , height , server, searchString);

        NotificationUtils.clearAllNotifications(this);
    }

    private void setDownloadsWallapersExecution(Intent intent, String action) {
        int width = intent.getIntExtra(ACTION_SET_WALLPAPER_SCREEN_WIDTH , ACTION_SET_WALLPAPER_SCREEN_WIDTH_DEFAULT);

        int height = intent.getIntExtra(ACTION_SET_WALLPAPER_SCREEN_HEIGHT, ACTION_SET_WALLPAPER_SCREEN_HEIGHT_DAFAULT);

        String[] filePaths = intent.getStringArrayExtra(RECENT_DOWNLOADS_WALLPAPERS);

        executeTaskFromDownloads(this, filePaths, width, height);

        NotificationUtils.clearAllNotifications(this);

    }
}
