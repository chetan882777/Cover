package com.chetan.projects.cover.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.LoadedImageEntry;
import com.chetan.projects.cover.Utilities.GlideUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;

import java.util.List;

public class backgroundDataLoading {

    public static final int FLAG_AUTOSET_CURSOR_COUNT_NULL = -1;
    public static final int FLAG_LOADED_IMAGES_GREATER_TO_LAODED_INFO = -2;
    public static final int FLAG_AUTOSET_BITMAP_NULL = -3;
    public static final int FLAG_AUTOSET_IMAGE_ENTRY_FAILED = -4;

    public static class LoadedImageEntryLoading{

        public static LoadedImageInfo queryFromDatabaseById(Context context, int id) {

            AppDatabase db = AppDatabase.getInstance(context);

            int count = db.loadedImageDao().getLoadedImageCount();

                id = id%count;

                if(id == 0){
                    id++;
                }


            LoadedImageEntry imageInfo = db.loadedImageDao().loadLoadedImageEntry(id);

            return new LoadedImageInfo(imageInfo.getLargeImageUrl(),
                    imageInfo.getMediumImageUrl(),
                    imageInfo.getPreviewUrl(),
                    imageInfo.getPageUrl(),
                    imageInfo.getUserName(),
                    imageInfo.getUserId(),
                    imageInfo.getTags(),
                    null);
        }

        public static void pushDataToDatabase(Context context , List<LoadedImageInfo> mImagesInfo) {


            AppDatabase db = AppDatabase.getInstance(context);

            db.loadedImageDao().deleteAllLoadedImageEntry();
            int i = db.loadedImageDao().getLoadedImageCount();

            for (LoadedImageInfo info : mImagesInfo) {

                while(db.loadedImageDao().loadLoadedImageEntry(i) != null){

                    i++;
                }

                LoadedImageEntry entry = new LoadedImageEntry(
                        i,
                        info.getLargeImageUrl(),
                        info.getMediumImageUrl(),
                        info.getPreviewUrl(),
                        info.getPageUrl(),
                        info.getUserName(),
                        info.getUserId(),
                        info.getTags());

                i = i + 1;

                db.loadedImageDao().insertLoadedImageEntry(entry);
            }
        }
    }





    public static class AutosetImageEntryLoading{

        public static int pushStringedBitmapToDatabsae(final Context context, final int offset) {


            AsyncTask backgroundTask;

            final int[] flag = new int[1];

            final AppDatabase db = AppDatabase.getInstance(context);


            backgroundTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {


                    int loadedImageCount = db.loadedImageDao().getLoadedImageCount();
                    Log.v("backgroundDataLoading", "------------- loadedImageCount:" + loadedImageCount);

                    int offsetter = offset;

                    Log.v("backgroundDataLoading", "------------- offset:" + offset);

                    int position = 0;

                    position = (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context)+1-1);

                    if(position < loadedImageCount) {
                        while (offsetter > 0) {

                            offsetter = offsetter - 1;

                            position = position +1;


                            Log.v("backgroundDataLoading", "------------- offsetter:" + offsetter);


                            Log.v("backgroundDataLoading", "------------- position:" + position);


                            if (position >= loadedImageCount) {
                                Log.v("backgroundDataLoading", "------------- position > loadedImageCount");

                                flag[0] = FLAG_LOADED_IMAGES_GREATER_TO_LAODED_INFO;
                            }

                            Log.v("backgroundDataLoading", "------------- position:" + position);

                            LoadedImageEntry imageEntry = db.loadedImageDao().loadLoadedImageEntry(position);


                            try {
                                final String largeImageUrl = imageEntry.getLargeImageUrl();

                                Log.v("backgroundDataLoading", "------------- largeImageUrl:" + largeImageUrl);


                                Bitmap bitmap = GlideUtils.glideLoadImage(context, largeImageUrl);

                                if (bitmap == null) {
                                    flag[0] = FLAG_AUTOSET_BITMAP_NULL;
                                }

                                PreferenceUtils.setCurrentWallpaperGlideLoadPref(context,
                                        (int) (PreferenceUtils.getCurrentWallpaperGlideLoadPref(context)+1 -1));

                            } catch (Exception e) {
                                Log.v("backgroundDataLoading", "------------- largeImageUrl null exception");

                            }
                        }
                    }
                    return null;
                }
            };
            backgroundTask.execute();
            return flag[0];
        }

        public static void clearAllAutoSetImageEntries(final Context context){
            AsyncTask backgroundTask;

            backgroundTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {

                    AutoWallpaperDatabaseTable autoWallpaperDatabaseTable =
                            new AutoWallpaperDatabaseTable(context);

                    int i = autoWallpaperDatabaseTable.clearAllAutosetWallpapers();

                    if(i<0){
                        Log.e("BackgroundDataLoading" , "clearAllAutosetWallpapers failed");
                    }
                    return null;
                }
            };
            backgroundTask.execute();
        }
    }

}
