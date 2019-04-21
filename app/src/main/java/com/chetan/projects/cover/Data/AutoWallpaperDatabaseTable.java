package com.chetan.projects.cover.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chetan.projects.cover.AAC.LoadedImageEntry;

public class AutoWallpaperDatabaseTable {

    public static final String TABLE_NAME = "Autoset_wallpapers";

    private static final String _ID = "ID";
    public static final String COL_COUNT_ID = "COUNT(" + _ID + ")";
    public static final String COL_LARGE_IMAGE_URL = "large_image_url";
    public static final String COL_MEDIUM_IMAGE_URL = "medium_image_url";
    public static final String COL_PREVIEW_IMAGE_URL = "preview_image_url";
    public static final String COL_PAGE_URL = "page_url";
    public static final String COL_USER_NAME = "user_name";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_TAGS = "tags";
    public static final String COL_LOADING_TYPE = "loading_type";
    public static final String COL_STRINGED_BITMAP = "stringed_bitmap";
    private final DatabaseOpenHelper mDatabaseOpenHelper;
    private SQLiteDatabase mDatabase;

    public AutoWallpaperDatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public Cursor queryByLargeUrl(String largeUrl,String[] projection){

        String selection = COL_LARGE_IMAGE_URL  + " = ?";
        String[] selectionArgs = new String[]{largeUrl};
        mDatabase = mDatabaseOpenHelper.getReadableDatabase();
        return mDatabase.query(TABLE_NAME, projection, selection, selectionArgs,
                null, null, null);
    }

    public int clearAllAutosetWallpapers(){
        mDatabase = mDatabaseOpenHelper.getWritableDatabase();
        return mDatabase.delete(TABLE_NAME, null, null);
    }

    public long addAutosetWallpaper(LoadedImageInfo info,String bitmap){
        ContentValues values = new ContentValues();
        values.put(COL_STRINGED_BITMAP, bitmap);
        values.put(COL_LARGE_IMAGE_URL , info.getLargeImageUrl());
        values.put(COL_PREVIEW_IMAGE_URL , info.getPreviewUrl());
        values.put(COL_MEDIUM_IMAGE_URL , info.getMediumImageUrl());
        values.put(COL_PAGE_URL , info.getPageUrl());
        values.put(COL_USER_NAME , info.getUserName());
        values.put(COL_USER_ID , info.getUserId());
        values.put(COL_TAGS , info.getTags());
        values.put(COL_LOADING_TYPE , info.getLoadingType());

        mDatabase = mDatabaseOpenHelper.getWritableDatabase();

        return mDatabase.insert(TABLE_NAME, null, values);
    }

    public long addAutosetWallpaper(LoadedImageEntry info ,String bitmap, String laoding_type){
        LoadedImageInfo loadedImageInfo = new LoadedImageInfo(info.getLargeImageUrl(),
                info.getMediumImageUrl(),
                info.getPreviewUrl(),
                info.getPageUrl(),
                info.getUserName(),
                info.getUserId(),
                info.getTags(),
                laoding_type
                );
        return  addAutosetWallpaper(loadedImageInfo, bitmap);
    }

    public Cursor getAutosetWallpaperCount(){

        mDatabase = mDatabaseOpenHelper.getReadableDatabase();

        String[] projection = new String[]{COL_COUNT_ID};
        return mDatabase.query(TABLE_NAME, projection ,
                null, null , null, null , null);
    }
}
