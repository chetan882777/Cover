package com.chetan.projects.cover.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;

import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.INTERVAL_15_MIN;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.INTERVAL_1_HOUR;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.INTERVAL_3_HOUR;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.INTERVAL_5_MIN;
import static com.chetan.projects.cover.Utilities.CoverWallpaperUtils.OFFSET_DEFAULT;

public class ValueUtils {

    public static class IntervalOffsetUtils {

        private static int offset = OFFSET_DEFAULT;

        public static int getOffset(final Context context) {

            int glideLoadPosiotion = (int) (PreferenceUtils.getCurrentWallpaperGlideLoadPref(context) + 1 -1);
            int interval = (int) (PreferenceUtils.getWallpaperIntervalPref(context) +1 -1);
            int position = (int) (PreferenceUtils.getCurrentWallpaperPositionPref(context) +1 -1);

            int loadAhead = glideLoadPosiotion - position;

            if(interval < INTERVAL_5_MIN){


                if(loadAhead < 5)
                    offset = 10;
                else if(loadAhead >= 5 && loadAhead < 15)
                    offset = 7;
                else if(loadAhead >= 15 && loadAhead < 30)
                    offset = 4;
                else if(loadAhead > 30 && loadAhead < 50)
                    offset = 2;
                else
                    offset = 0;

            }else if(interval < INTERVAL_15_MIN){


                if(loadAhead >= 0 && loadAhead < 5)
                    offset = 8;
                else if(loadAhead >= 5 && loadAhead < 15)
                    offset = 6;
                else if(loadAhead >= 15 && loadAhead < 30)
                    offset = 3;
                else
                    offset = 0;


            }else if(interval < INTERVAL_1_HOUR){


                if(loadAhead >= 0 && loadAhead < 5)
                    offset = 5;
                else if(loadAhead >= 5 && loadAhead < 15)
                    offset = 3;
                else
                    offset = 0;


            }else if(interval < INTERVAL_3_HOUR){


                if(loadAhead >= 0 && loadAhead < 5)
                    offset = 4;
                else if(loadAhead >= 5 && loadAhead < 15)
                    offset = 2;
                else
                    offset = 0;


            }else if(interval < 4* INTERVAL_3_HOUR){


                if(loadAhead >= 0 && loadAhead < 5)
                    offset = 3;
                else if(loadAhead >= 5 && loadAhead < 10)
                    offset = 2;
                else
                    offset = 0;


            }else{


                if(loadAhead >= 0 && loadAhead < 5)
                    offset = 2;

                if(loadAhead >= 5 && loadAhead < 10)
                    offset = 1;
                else
                    offset = 0;
            }

            return offset;
        }
    }

    public static class ImageStackEmpty{

        public static int getStackEmptyWallaperPosiotion(Context context){
           int position = 0;

           return  position;
        }
    }
}
