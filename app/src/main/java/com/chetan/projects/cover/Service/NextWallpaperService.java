package com.chetan.projects.cover.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;

public class NextWallpaperService extends IntentService {
    public NextWallpaperService() {
        super("NextWallpaperService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = this;

        CoverWallpaperUtils.executeTask(context,
                CoverWallpaperUtils.ACTION_SET_NEXT_WALLPAPER,
                DisplayUtils.getDisplayWidth(),
                DisplayUtils.getDisplayHeight());

    }
}
