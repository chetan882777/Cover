package com.chetan.projects.cover.Service;

import android.content.Context;
import android.os.AsyncTask;

import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;
import com.chetan.projects.cover.Utilities.NotificationUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class DownloadWallpaperFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                Context context = DownloadWallpaperFirebaseJobService.this;

                CoverWallpaperUtils.executeTask(context,
                        CoverWallpaperUtils.ACTION_DOWNLOAD_WALLPAPERS,
                        DisplayUtils.getDisplayWidth(),
                        DisplayUtils.getDisplayHeight()               );
                NotificationUtils.remindDownloadWallpaperChanged(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job , false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
