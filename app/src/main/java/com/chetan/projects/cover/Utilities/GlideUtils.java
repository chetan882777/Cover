package com.chetan.projects.cover.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.auth.util.ui.BaselineTextInputLayout;

import java.util.concurrent.ExecutionException;

public class GlideUtils {

    public static Bitmap glideLoadImage(Context context, String largeImageurl) {

        Bitmap bitmap = null;
        Log.v("-------------", "----------- info : " + largeImageurl);
        try {
            bitmap = Glide.
                    with(context.getApplicationContext()).asBitmap().
                    load(largeImageurl).
                    submit().
                    get();
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v("-------------", "----------- bitmap obtained : " + bitmap);
        return bitmap;
    }

}
