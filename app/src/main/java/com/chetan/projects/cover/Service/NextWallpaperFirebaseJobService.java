package com.chetan.projects.cover.Service;

import android.app.job.JobParameters;
import android.content.Context;
import android.os.AsyncTask;

import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;
import com.firebase.jobdispatcher.JobService;

public class NextWallpaperFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                Context context = NextWallpaperFirebaseJobService.this;

                CoverWallpaperUtils.executeTask(context,
                        CoverWallpaperUtils.ACTION_SET_NEXT_WALLPAPER,
                        DisplayUtils.getDisplayWidth(),
                        DisplayUtils.getDisplayHeight()
                );
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
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if(mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
