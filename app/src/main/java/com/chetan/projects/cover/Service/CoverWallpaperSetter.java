package com.chetan.projects.cover.Service;

import android.app.WallpaperManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.SetWallpaperEntry;
import com.chetan.projects.cover.Data.AutoWallpaperDatabaseTable;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Network.ConnectionCheck;
import com.chetan.projects.cover.Utilities.BitmapUtils;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.GlideUtils;
import com.chetan.projects.cover.Utilities.NotificationUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;
import com.chetan.projects.cover.WallpaperDetailActivity;

import java.util.Date;

import static com.chetan.projects.cover.Data.backgroundDataLoading.LoadedImageEntryLoading.queryFromDatabaseById;


public class CoverWallpaperSetter {



    synchronized public static void setWallpapers(Context context ,String action, LoadedImageInfo info , int width , int height) {
        LoadBitmap(context ,action, info , width , height);
    }

    private static void LoadBitmap(final Context context,final String action, final LoadedImageInfo info , final int width , final int height) {

        final AsyncTask<Void , Void , Bitmap> task;

        task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {

                LoadedImageInfo imageInfo = info;
                Bitmap bitmap1 = null;
                while(bitmap1 == null) {

                    bitmap1 = GlideUtils.glideLoadImage(context, imageInfo.getLargeImageUrl());

                        if(ConnectionCheck.isNetworkConnected(context ) &&
                            PreferenceUtils.getCurrentWallpaperGlideLoadPref(context)
                                    < PreferenceUtils.getCurrentWallpaperPositionPref(context))
                    {

                        PreferenceUtils.setCurrentWallpaperGlideLoadPref(context ,
                                (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context)+ 1 -1));
                    }

                    if (bitmap1 == null) {
                        Log.v("CoverWalpaperSetter", "---------------- bitmap null inside task");
                            imageInfo = getNextLoadedImageInfo(action, imageInfo, context);

                    } else if (bitmap1.getHeight() == 0 || bitmap1.getWidth() == 0) {

                        imageInfo = getNextLoadedImageInfo(action, imageInfo, context);

                    }

                    PreferenceUtils.setCurrentWallpaperGlideLoadPref(context,
                            (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context) + 1));


                }
                return bitmap1;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

                scaleBitmapForWallpaper(bitmap, height, width, context, info, true);
            }
        };

        task.execute();

    }

    public static void scaleBitmapForWallpaper(Bitmap bitmap, int height, int width, Context context, LoadedImageInfo info, boolean actions) {
        int heightDiff;

        int newWidth = bitmap.getWidth();

        int heightToSet = height;

        if(bitmap.getHeight() > height && bitmap.getWidth() > width){


            float reduction = height/bitmap.getHeight();

            newWidth = (int) (bitmap.getWidth()*reduction);


            if(newWidth <= 0){

                newWidth = bitmap.getWidth();
                heightToSet = bitmap.getHeight();
            }
        }else{
            heightToSet = bitmap.getHeight();
        }

        if(heightToSet > 0 && newWidth > 0) {

            Bitmap loadedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, heightToSet, true);

            setWallpaper(context, loadedBitmap, actions);


            if(info != null) {
                setWallpaperToDatabase(context, info);
            }
        }else{
            LoadBitmap(context, null, info , width , height);

        }
    }

    public static void setWallpaperToDatabase(final Context context, final LoadedImageInfo mLoadedImageInfo) {
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase.getInstance(context).setWallpaperDao()
                        .insertSetWallpaperEntry(new SetWallpaperEntry(mLoadedImageInfo.getLargeImageUrl(),
                                mLoadedImageInfo.getMediumImageUrl(),
                                mLoadedImageInfo.getPreviewUrl(),
                                mLoadedImageInfo.getPageUrl(),
                                mLoadedImageInfo.getUserName(),
                                mLoadedImageInfo.getUserId(),
                                mLoadedImageInfo.getTags(),
                                ""+new Date().getTime(),
                                mLoadedImageInfo.getLoadingType()));
                return null;
            }
        };
        task.execute();
    }


    private static LoadedImageInfo getNextLoadedImageInfo(String action, LoadedImageInfo imageInfo, Context context) {
        Log.v("coverWallpaperSetter", " bitmap null");

        if(!ConnectionCheck.isNetworkConnected(context)){

            Log.v("coverWallaperSetter" , "no connection");


            if(PreferenceUtils.getCurrentWallpaperGlideLoadPref(context)
                            >= PreferenceUtils.getCurrentWallpaperPositionPref(context))
            {
                PreferenceUtils.setCurrentWallpaperPositionPref(context ,
                        (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context)+ 1));
                imageInfo = queryFromDatabaseById(context, (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context) + 1 - 1));

            }else{

                if(PreferenceUtils.getCurrentWallpaperTempPositionPref(context) <=
                        PreferenceUtils.getCurrentWallpaperGlideLoadPref(context)) {
                    PreferenceUtils.setCurrentWallpaperTempPositionPref(context,
                            (int) (PreferenceUtils.getCurrentWallpaperTempPositionPref(context) + 1));

                }else{
                    PreferenceUtils.initCurrentTempWallpaperPref(context);
                }

                imageInfo = queryFromDatabaseById(context, (int) (PreferenceUtils.getCurrentWallpaperTempPositionPref(context) + 1 - 1));

            }

        }else {

            PreferenceUtils.setCurrentWallpaperPositionPref(context,
                    (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context) + 1));

            imageInfo = queryFromDatabaseById(context, (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context) + 1 - 1));
        }
        return imageInfo;
    }



    public static void setWallpaper(Context context, Bitmap bitmap, boolean actions) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        try {
            wallpaperManager.setBitmap(bitmap);

            NotificationUtils.remindBecauseWallpaperChanged(context, actions);

        }catch (Exception e){

            Log.v("coverWallaperSetter" , "----------- failed to set");
            Toast.makeText(context, "Failed to set wallpaper!", Toast.LENGTH_SHORT).show();
        }

    }
}
