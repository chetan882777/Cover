package com.chetan.projects.cover.Utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.SetWallpaperEntry;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Firebase.PexelsSearchResults;
import com.chetan.projects.cover.Network.ConnectionCheck;
import com.chetan.projects.cover.Network.ImagesJsonLoadingPexels;
import com.chetan.projects.cover.Network.ImagesJsonLoadingPixabay;
import com.chetan.projects.cover.Network.JsonRequestConstants;
import com.chetan.projects.cover.Service.CoverWallpaperSetter;
import com.chetan.projects.cover.Service.WallpaperJobDispatcher;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.chetan.projects.cover.Data.backgroundDataLoading.AutosetImageEntryLoading.clearAllAutoSetImageEntries;
import static com.chetan.projects.cover.Data.backgroundDataLoading.AutosetImageEntryLoading.pushStringedBitmapToDatabsae;
import static com.chetan.projects.cover.Data.backgroundDataLoading.LoadedImageEntryLoading.pushDataToDatabase;
import static com.chetan.projects.cover.Data.backgroundDataLoading.LoadedImageEntryLoading.queryFromDatabaseById;
import static com.chetan.projects.cover.Utilities.PreferenceUtils.getCurrentWallpaperPositionPref;
import static com.chetan.projects.cover.Utilities.PreferenceUtils.initCurrentWallpaperPref;
import static com.chetan.projects.cover.Utilities.PreferenceUtils.setCurrentWallpaperPositionPref;
import static com.chetan.projects.cover.Utilities.ValueUtils.IntervalOffsetUtils.getOffset;
import static com.chetan.projects.cover.WallpaperActivity.CHILD_PEXELS_SEARCH_RESULTS_FIREBASE;
import static com.firebase.ui.auth.AuthUI.TAG;

public class CoverWallpaperUtils {

    public static final String ACTION_SET_WALLPAPER = "action_set_wallpaper";

    public static final String ACTION_SET_WALLPAPERS = "action_set_wallpapers";

    public static final String ACTION_SET_NEXT_WALLPAPER = "action_set_next_wallpaper";

    public static final String ACTION_SET_PREV_WALLPAPER = "action_set_prew_wallpaper";

    public static final String ACTION_DOWNLOAD_WALLPAPERS = "action_download_wallpapers";

    public static final String ACTION_RECENT_WALLPAPERS = "action_recent_wallpapers";

    public static final String ACTION_RECENT_DOWNLOADS_WALLPAPERS = "action_recent_download_wallpapers";

    public static final String RECENT_DOWNLOADS_WALLPAPERS = "action_recent_download_wallpapers";

    public static final String ACTION_CROP_NOTIFICATION = "action_dismiss_notification";

    public static final String ACTION_SET_WALLPAPER_INFO = "action_set_wallpaper_info";

    public static final String ACTION_SET_WALLPAPERS_INFO = "action_set_wallpapers_info";

    public static final String ACTION_SET_WALLPAPER_SCREEN_HEIGHT = "action_set_paper_screen_height";
    public static final String ACTION_SET_WALLPAPER_SCREEN_WIDTH = "action_set_paper_screen_width";

    public static final int ACTION_SET_WALLPAPER_SCREEN_HEIGHT_DAFAULT = 1280;
    public static final int ACTION_SET_WALLPAPER_SCREEN_WIDTH_DEFAULT = 720;
    public static final String CURRENT_WALLPAPER_POSITION_PREF = "current_wallpaper_position";
    public static final String CURRENT_WALLPAPER_POSITION_PREF_VALUE = "current_wallpaper_position_value";


    public static final String CURRENT_WALLPAPER_TEMP_POSITION_PREF = "current_wallpaper_temp_position";
    public static final String CURRENT_WALLPAPER_TEMP_POSITION_PREF_VALUE = "current_wallpaper_temp_position_value";

    public static final String CURRENT_WALLPAPER_INTERVAL_PREF = "current_wallpaper_interval";
    public static final String CURRENT_WALLPAPER_INTERVAL_PREF_VALUE = "current_wallpaper_interval_value";


    public static final String CURRENT_WALLPAPER_GLIDE_LOAD_PREF = "current_wallpaper_glide_load";
    public static final String CURRENT_WALLPAPER_GLIDE_LOAD_PREF_VALUE = "current_wallpaper_glide_load_value";

    public static final String ACTION_SET_WALLPAPER_OF_PEXELS = "action_Set_Wallpapers_of_pexels";
    public static final String ACTION_SET_WALLPAPER_OF_PIXABAY = "action_Set_Wallpapers_of_pixabay";

    public static final String ACTION_SET_WALLPAPER_SERVER = "action_Set_Wallpapers_server";


    public static final int OFFSET_DEFAULT = 0;


    public static final String ACTION_SET_WALLPAPER_SEARCH_STRING = "action_Set_Wallpapers_search_string";
    public static final String ACTION_SET_WALLPAPER_SEARCH_STRING_DEFAULT = "flower";
    public static final String ACTION_SET_WALLPAPER_SEARCH_STRING_FEATURED = "featured";
    public static final String ACTION_SET_WALLPAPER_SEARCH_STRING_NEW = "new";
    public static final int INTERVAL_5_MIN = 300;
    public static final int INTERVAL_15_MIN = 900;
    public static final int INTERVAL_1_HOUR = 3600;
    public static final int INTERVAL_3_HOUR = 10800;
    private static ArrayList<LoadedImageInfo> mImagesInfo;
    private static int mWidth;
    private static int mHeight;

    public static void executeTask(Context context, String action , int width , int height, String server, String searchString){

        mWidth = width;
        mHeight = height;

        if(action.equals(ACTION_SET_WALLPAPERS)) {

            WallpaperJobDispatcher.cancelAllJob(context);

            clearAllAutoSetImageEntries(context);

            PreferenceUtils.initCurrentWallpaperGlideLoadPref(context);

            PreferenceUtils.initCurrentTempWallpaperPref(context);

            if(server.equals(ACTION_SET_WALLPAPER_OF_PEXELS)){
                loadPexelsData(context, searchString);
            }else if(server.equals(ACTION_SET_WALLPAPER_OF_PIXABAY)){
                loadPixabayData(context, searchString);
            }




        }else if(action.equals(ACTION_SET_NEXT_WALLPAPER)){

            actionNextWallpaper(context,width,height);


        }else if (action.equals(ACTION_SET_PREV_WALLPAPER)){

            actionPrevWallpaper(context , width , height);
        }else if(action.equals(ACTION_RECENT_WALLPAPERS)){
            actionRecentWallpapers(context);
        }
    }

    public static void executeTask(Context context, String action , int width , int height){

        mWidth = width;
        mHeight = height;

        if(action.equals(ACTION_SET_WALLPAPERS)) {
            return;
        }else if(action.equals(ACTION_SET_NEXT_WALLPAPER)){

            actionNextWallpaper(context, width, height);


        }else if (action.equals(ACTION_SET_PREV_WALLPAPER)){

            actionPrevWallpaper(context, width, height);

        }
    }

    public static void executeTaskFromDownloads(Context context, String[] fileNames , int width, int height){
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            WallpaperJobDispatcher.cancelAllJob(context);
        }
        else {
           // loadDownloads();
        }
    }

    private static void actionPrevWallpaper(Context context, int width, int height) {

        Long position = getCurrentWallpaperPositionPref(context);

        int newPosition = 0;
        if(position > 0) {
            newPosition = (int) (position - 1);
        }else {
            newPosition = 40;
        }
        setCurrentWallpaperPositionPref(context , newPosition);

        LoadedImageInfo imageInfo = queryFromDatabaseById(context, newPosition);

        CoverWallpaperSetter.setWallpapers(context ,ACTION_SET_PREV_WALLPAPER, imageInfo , width , height);
    }


    private static void actionNextWallpaper(Context context, int width, int height) {
        Long position = getCurrentWallpaperPositionPref(context);

        int newPosition = (int) (position + 1);
        setCurrentWallpaperPositionPref(context , newPosition);

        LoadedImageInfo imageInfo = queryFromDatabaseById(context, newPosition);


        CoverWallpaperSetter.setWallpapers(context ,ACTION_SET_NEXT_WALLPAPER, imageInfo , width , height);

        if(ConnectionCheck.isNetworkConnected(context))
            pushStringedDataToDatabase(context);
    }

    private static void actionRecentWallpapers(Context context){
        List<SetWallpaperEntry> setWallpaperEntries = AppDatabase
                .getInstance(context)
                .setWallpaperDao()
                .loadAllWallpaperEntries();
        List<LoadedImageInfo> imageInfos = new ArrayList<>();
        for(SetWallpaperEntry entry: setWallpaperEntries){
            imageInfos.add(new LoadedImageInfo(entry.getLargeImageUrl(),
                    entry.getMediumImageUrl(),
                    entry.getPreviewUrl(),
                    entry.getPageUrl(),
                    entry.getUserName(),
                    entry.getUserId(),
                    entry.getTags(),
                    entry.getLoadingType()));
        }
        pushDataToDatabase(context , imageInfos);

        CoverWallpaperSetter.setWallpapers(context,ACTION_SET_WALLPAPERS, imageInfos.get(0) , mWidth , mHeight);

        WallpaperJobDispatcher.scheduleWallpaperJobDispatcher(context);
        pushStringedBitmapToDatabsae(context , getOffset(context));

    }

    private static void pushStringedDataToDatabase(Context context){
        pushStringedBitmapToDatabsae(context , 4);
    }



    private static void loadPexelsData(final Context context, final String mCategory) {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mCategoriesRef = mFirebaseDatabase.getReference(CHILD_PEXELS_SEARCH_RESULTS_FIREBASE);
        mCategoriesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                PexelsSearchResults value = dataSnapshot.getValue(PexelsSearchResults.class);


                if(mCategory.equalsIgnoreCase(value.getSearch_string())) {
                    ImagesJsonLoadingPexels.parsePexelsSearchResult(value.getSearch_result_json());

                    mImagesInfo = ImagesJsonLoadingPexels.getLoadedImages();

                    pushDataToDatabase(context , mImagesInfo);

                    initCurrentWallpaperPref(context);

                    LoadedImageInfo imageInfo = mImagesInfo.get(0);

                    CoverWallpaperSetter.setWallpapers(context,ACTION_SET_WALLPAPERS, imageInfo , mWidth , mHeight);


                    WallpaperJobDispatcher.scheduleWallpaperJobDispatcher(context);
                    pushStringedBitmapToDatabsae(context , getOffset(context));


                    Log.v(TAG , "loading from firebase of categories done");
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private static void loadPixabayData(final Context context, final String mCategory) {
        AsyncTask<Void, Void, ArrayList<LoadedImageInfo>> task;

        task = new AsyncTask<Void, Void, ArrayList<LoadedImageInfo>>() {
            @Override
            protected ArrayList<LoadedImageInfo> doInBackground(Void... voids) {

                int search = 1;

                Log.v("CoverWallpaperUtils", "---------- mCategory:" + mCategory);

                if(mCategory.equals(ACTION_SET_WALLPAPER_SEARCH_STRING_NEW)){
                    search = JsonRequestConstants.PixabayAPI.LATEST_IMAGE_LOADING;
                    Log.v("CoverWallpaperUtils", "---------- Lastest loading");


                }else if(mCategory.equals(ACTION_SET_WALLPAPER_SEARCH_STRING_FEATURED)){
                    search = JsonRequestConstants.PixabayAPI.POPULAR_IMAGE_LOADING;

                    Log.v("CoverWallpaperUtils", "---------- Popular loading");

                }else{
                    search = JsonRequestConstants.PixabayAPI.CATEGORY_IMAGE_LOADING;
                    Log.v("CoverWallpaperUtils", "---------- Category loading");

                }
                ImagesJsonLoadingPixabay jsonLoadingPixabay = new ImagesJsonLoadingPixabay(
                        search,
                        mCategory,
                        0);

                ArrayList<LoadedImageInfo> loadedImages = jsonLoadingPixabay.getLoadedImages();
                return loadedImages;
            }

            @Override
            protected void onPostExecute(ArrayList<LoadedImageInfo> loadedImageInfos) {
                mImagesInfo = loadedImageInfos;

                pushDataToDatabase(context , mImagesInfo);

                initCurrentWallpaperPref(context);

                LoadedImageInfo imageInfo = mImagesInfo.get(0);

                CoverWallpaperSetter.setWallpapers(context, ACTION_SET_WALLPAPERS, imageInfo , mWidth , mHeight);

                WallpaperJobDispatcher.scheduleWallpaperJobDispatcher(context);
                pushStringedBitmapToDatabsae(context , getOffset(context));
            }
        };
        task.execute();

    }


    public static Bitmap scaleBitmapForWallpaper(Bitmap bitmap, int height, int width) {

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

            return loadedBitmap;

        }else{

            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
    }
}

