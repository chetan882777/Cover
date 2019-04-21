package com.chetan.projects.cover.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_GLIDE_LOAD_PREF;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_GLIDE_LOAD_PREF_VALUE;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_INTERVAL_PREF;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_INTERVAL_PREF_VALUE;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_POSITION_PREF;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_POSITION_PREF_VALUE;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_TEMP_POSITION_PREF;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.CURRENT_WALLPAPER_TEMP_POSITION_PREF_VALUE;

public class PreferenceUtils {

    public static void initCurrentWallpaperPref(Context context) {
        Long position = getCurrentWallpaperPositionPref(context);
        if(position != 0) {
            setCurrentWallpaperPositionPref(context ,0 );
        }
    }

    public static void setCurrentWallpaperPositionPref(Context context,int position) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CURRENT_WALLPAPER_POSITION_PREF, MODE_PRIVATE).edit();

        editor.putLong(CURRENT_WALLPAPER_POSITION_PREF_VALUE, position);
        editor.apply();
    }

    @NonNull
    public static Long getCurrentWallpaperPositionPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(CURRENT_WALLPAPER_POSITION_PREF, MODE_PRIVATE);
        return preferences.getLong(CURRENT_WALLPAPER_POSITION_PREF_VALUE, 0);
    }


    public static void setWallpaperIntervalPref(Context context,int seconds) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CURRENT_WALLPAPER_INTERVAL_PREF, MODE_PRIVATE).edit();

        editor.putLong(CURRENT_WALLPAPER_INTERVAL_PREF_VALUE, seconds);
        editor.apply();
    }

    @NonNull
    public static Long getWallpaperIntervalPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(CURRENT_WALLPAPER_INTERVAL_PREF, MODE_PRIVATE);
        return preferences.getLong(CURRENT_WALLPAPER_INTERVAL_PREF_VALUE, 0);
    }


    public static void initCurrentWallpaperGlideLoadPref(Context context) {
        Long position = getCurrentWallpaperGlideLoadPref(context);
        if(position != 0) {
            setCurrentWallpaperGlideLoadPref(context ,0 );
        }
    }

    public static void setCurrentWallpaperGlideLoadPref(Context context,int position) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CURRENT_WALLPAPER_GLIDE_LOAD_PREF, MODE_PRIVATE).edit();

        editor.putLong(CURRENT_WALLPAPER_GLIDE_LOAD_PREF_VALUE, position);
        editor.apply();
    }

    @NonNull
    public static Long getCurrentWallpaperGlideLoadPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(CURRENT_WALLPAPER_GLIDE_LOAD_PREF, MODE_PRIVATE);
        return preferences.getLong(CURRENT_WALLPAPER_GLIDE_LOAD_PREF_VALUE, 0);
    }


    public static void initCurrentTempWallpaperPref(Context context) {
        Long position = getCurrentWallpaperPositionPref(context);
        if(position != 0) {
            setCurrentWallpaperTempPositionPref(context ,0 );
        }
    }

    public static void setCurrentWallpaperTempPositionPref(Context context,int position) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CURRENT_WALLPAPER_TEMP_POSITION_PREF, MODE_PRIVATE).edit();

        editor.putLong(CURRENT_WALLPAPER_TEMP_POSITION_PREF_VALUE, position);
        editor.apply();
    }

    @NonNull
    public static Long getCurrentWallpaperTempPositionPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(CURRENT_WALLPAPER_TEMP_POSITION_PREF, MODE_PRIVATE);
        return preferences.getLong(CURRENT_WALLPAPER_TEMP_POSITION_PREF_VALUE, 0);
    }

}
