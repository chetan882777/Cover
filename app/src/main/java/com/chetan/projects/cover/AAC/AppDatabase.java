package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;


// wallpaper entry for all detail viewed wallpapers
// and setWallpaper entry for viewed wallpapers set as wallpaper
@Database(entities = {SetWallpaperEntry.class, CategoriesEntry.class,
        LoadedImageEntry.class, CategoryVersionEntry.class, CategoryResultEntry.class},
        version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "coverBase";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Log.d(LOG_TAG , "Creating new database instance");
                sInstance = Room.databaseBuilder(context, AppDatabase.class,
                        AppDatabase.DATABASE_NAME)

                        // Queries should be done on separate thread
                        // here it is added for development
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Log.d(LOG_TAG , "Getting database instance");
        return sInstance;
    }


    // SetWallpaperEntry dao
    public abstract SetWallpaperDao setWallpaperDao();

    public abstract CategoriesDao categoriesDao();

    public abstract LoadedImageDao loadedImageDao();

    public abstract CategoryVersionDao categoryVersionDao();

    public abstract CategoryResultDao categoryResultDao();

}
