package com.chetan.projects.cover;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.chetan.projects.cover.Service.CoverWallpaperSetter;
import com.chetan.projects.cover.Utilities.GlideUtils;
import com.ortiz.touchview.TouchImageView;

import java.io.File;

import static com.chetan.projects.cover.DownloadsActivity.REQUEST_CODE_STORAGE;

public class ZoomActivity extends AppCompatActivity {

    public static final String ZOOM_BITMAP = "zoom_bitmap";
    public static final String ZOOM_BITMAP_SENDER = "zoom_bitmap_sender";
    public static final String ZOOM_BITMAP_DOWNLOADS = "zoom_bitmap_downloads";
    public static final String ZOOM_BITMAP_STRING = "zoom_bitmap_string";
    public static final String ZOOM_PARENT_ACTIVITY = "zoom_parent_activity";
    public static final String ZOOM_PARENT_ACTIVITY_WALLPAPER_DETAIL = "zoom_parent_activity_wallpaper_detail";
    public static final String ZOOM_PARENT_ACTIVITY_DOWNLOADS = "zoom_parent_activity_downloads";
    private Bitmap mBitmap;
    private String mPath;
    private TouchImageView mImageView;
    private String mUrl;
    private String parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        setTitle("");
        // Hook up clicks on the thumbnail views.
        Intent intent = getIntent();

        if(intent != null){

            parentActivity = intent.getStringExtra(ZOOM_PARENT_ACTIVITY);
            String message = intent.getStringExtra(ZOOM_BITMAP_SENDER);

            processIntentMessage(message, intent);
        }

        mImageView = findViewById(R.id.zoom_imageView);

        mImageView.setImageBitmap(mBitmap);
    }

    private void processIntentMessage(String message, Intent intent) {

        switch (message){
            case ZOOM_BITMAP_STRING: {
                mUrl = intent.getStringExtra(ZOOM_BITMAP);
                urlBitmapSetup();
                return;
            }
            case ZOOM_BITMAP_DOWNLOADS: {
                mPath = intent.getStringExtra(ZOOM_BITMAP);
                pathBitmapSetup();
            }

            }
        }

    private void urlBitmapSetup() {
        loadBitmap(mUrl);
    }

    private void pathBitmapSetup(){
        loadBitmap(mPath);
    }

    private void loadBitmap(final String input) {
        AsyncTask<Void,Void,Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                return GlideUtils.glideLoadImage(ZoomActivity.this, input);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                mBitmap = bitmap;
                mImageView.setImageBitmap(mBitmap);
            }
        };
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mUrl == null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.zoom_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_wallpaper_zoom:
                setWallpaper();
                return true;
            case R.id.action_remove_zoom:
                deleteImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setWallpaper() {

        Toast.makeText(this, "Setting wallpaper", Toast.LENGTH_SHORT).show();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        CoverWallpaperSetter.scaleBitmapForWallpaper(mBitmap,height , width , this, null,false);
    }


    private void deleteImage() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestRuntimePermission();
        }
        else {
            File file = new File(mPath);
            boolean deleted = file.delete();
            if (deleted) {
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    deleteImage();


                } else {

                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void requestRuntimePermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_STORAGE);
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;

        if (parentActivity.equals(ZOOM_BITMAP_DOWNLOADS)) {
            i = new Intent(this, DownloadsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        } else if(parentActivity.equals(ZOOM_PARENT_ACTIVITY_WALLPAPER_DETAIL)) {
            i = new Intent(this, WallpaperDetailActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        return i;
    }
}




