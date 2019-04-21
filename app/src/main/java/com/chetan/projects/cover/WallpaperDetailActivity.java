package com.chetan.projects.cover;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.SetWallpaperEntry;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Service.CoverWallpaperSetter;
import com.chetan.projects.cover.Service.WallpaperJobDispatcher;
import com.chetan.projects.cover.Utilities.BitmapUtils;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.GlideUtils;
import com.chetan.projects.cover.Utilities.LoadedImagesUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PEXELS;
import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PIXABAY;


public class WallpaperDetailActivity extends AppCompatActivity {


    public static final int REQUEST_CODE_STORAGE_CROP_WALLPAPER = 7000;
    public static final int REQUEST_CODE_STORAGE_DOWNLOAD_WALLPAPER = 7001;
    public static String SEND_IMAGE_INFO = "imageClicked";

    private static String LOG_TAG = WallpaperDetailActivity.class.getSimpleName();

    private ImageView imageView ;

    private Bitmap mResourceBitmap;
    private View progressBar;
    private TextView infoTextView;
    private LoadedImageInfo mLoadedImageInfo;
    private FloatingActionButton cropFab;
    private FloatingActionButton setWallpaperFab;
    private FloatingActionButton downloadFab;
    private Bitmap mBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setStatusBarGradiant(this);

        setContentView(R.layout.activity_wallpaper_detail);

        setUpLayout();
        getValues();
        setValues();


    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.brand_gradient_shape);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void setUpLayout() {

        setTitle("");

        ConstraintLayout constraintLayout = findViewById(R.id.root_wallpaper_detail);
        constraintLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        imageView = findViewById(R.id.imageView_wallpaper_detail);
        progressBar = findViewById(R.id.progresBar_wallpaper_detail);
        infoTextView = findViewById(R.id.textView_info_wallpaper_detail);

        progressBar.setVisibility(View.VISIBLE);

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.setExitFadeDuration(400);
        animationDrawable.setExitFadeDuration(400);
        animationDrawable.start();

        cropFab = findViewById(R.id.floatingActionButton_crop_detail);
        setWallpaperFab = findViewById(R.id.floatingActionButton_setWallpaper_detail);
        downloadFab = findViewById(R.id.floatingActionButton_download_detail);

        cropFab.setEnabled(false);
        setWallpaperFab.setEnabled(false);
        downloadFab.setEnabled(false);

    }

    private void getValues() {
        Intent intent = getIntent();

        String imageInfo = intent.getStringExtra(SEND_IMAGE_INFO);

        mLoadedImageInfo = LoadedImagesUtils.toLoadedImageInfo(imageInfo);

        Log.v("WallpaperDetaileActiv","-------------------- largUrl:" + mLoadedImageInfo.getLargeImageUrl());
        Log.v("WallpaperDetaileActiv","-------------------- mediumUrl:" + mLoadedImageInfo.getMediumImageUrl());
        Log.v("WallpaperDetaileActiv","-------------------- previewUrl:" + mLoadedImageInfo.getPreviewUrl());

     }



    private void setValues() {

        RequestBuilder<Drawable> load = Glide
                .with(this)
                .load(mLoadedImageInfo.getPreviewUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Palette.from(((BitmapDrawable) resource).getBitmap())
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(Palette palette) {
                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                        if (textSwatch == null) {
                                            return;
                                        }

                                        int rgb = textSwatch.getRgb();

                                        int alpha = Color.alpha(rgb);
                                        int red = Color.red(rgb);
                                        int green = Color.green(rgb);
                                        int blue = Color.blue(rgb);

                                        alpha *= 1.00;

                                        rgb = Color.argb(alpha, red, green, blue);

                                        infoTextView.setBackgroundColor(rgb);

                                        Log.v("WallpaperDetailActivity", "---------------previewLoaded");


                                    }
                                });
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade(700));

        Glide.with(this)
                .load(mLoadedImageInfo.getLargeImageUrl())
                .thumbnail(load)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageLoadedSetup();
                        return false;
                    }
                })
                .into(imageView);


        LoadLargeImage();

        String pageUrl = mLoadedImageInfo.getPageUrl();

        boolean pixabay = pageUrl.contains("pixabay");

        if(pixabay) {
            infoTextView.setText("Info : Photo by "+ mLoadedImageInfo.getUserName() + " on Pixabay");
        }else{
            infoTextView.setText("Info : Photo by "+ mLoadedImageInfo.getUserName() + " on Pexels");
        }
    }

    private void LoadLargeImage() {

        Log.v("WallpaperDetailActivity", "--------------loadLargeImage");
        AsyncTask<Void,Void,Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = GlideUtils.glideLoadImage(WallpaperDetailActivity.this,
                        mLoadedImageInfo.getLargeImageUrl());
                mBitmap = bitmap;
                return bitmap;
            }
        };
        task.execute();
    }



    private void imageLoadedSetup() {
        cropFab.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 int permissionCheck = ContextCompat.checkSelfPermission(
                         WallpaperDetailActivity.this,
                         Manifest.permission.WRITE_EXTERNAL_STORAGE);

                 if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                     requestRuntimePermission(REQUEST_CODE_STORAGE_CROP_WALLPAPER);
                 }
                 else {
                     cropImage();
                 }
             }
        });
        cropFab.setEnabled(true);

        setWallpaperFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setWallpaper();
            }
        });

        setWallpaperFab.setEnabled(true);

        downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(
                        WallpaperDetailActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                    requestRuntimePermission(REQUEST_CODE_STORAGE_DOWNLOAD_WALLPAPER);
                }
                else {
                    downloadImage();
                }
            }
        });
        downloadFab.setEnabled(true);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(WallpaperDetailActivity.this,ZoomActivity.class);
                intent.putExtra(ZoomActivity.ZOOM_BITMAP , mLoadedImageInfo.getLargeImageUrl());
                intent.putExtra(ZoomActivity.ZOOM_PARENT_ACTIVITY , ZoomActivity.ZOOM_PARENT_ACTIVITY_WALLPAPER_DETAIL);
                intent.putExtra(ZoomActivity.ZOOM_BITMAP_SENDER, ZoomActivity.ZOOM_BITMAP_STRING);
                startActivity(intent);
            }
        });
    }

    private void downloadImage() {


        Toast.makeText(WallpaperDetailActivity.this,"Downloading image",Toast.LENGTH_SHORT).show();

        AsyncTask<Void,Void,Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = mBitmap;
                try{

                    String path = Environment.getExternalStorageDirectory().toString() + "/CoverDownloads";

                    File dir = new File(path);
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    Log.v("WallpaperDetailActiv","-------- path:" + path);
                    OutputStream fOut = null;


                    File file = new File(path, "Cover_"+new Date().getTime() +".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    fOut = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush(); // Not really required
                    fOut.close(); // do not forget to close the stream

                    addImageToGallery(path);

                    Log.v("WallpaperDetailActiv","-------- file :" + file.getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Toast.makeText(WallpaperDetailActivity.this, "Image downloaded" , Toast.LENGTH_SHORT).show();
            }
        };
        task.execute();
    }

    public void addImageToGallery(final String filePath) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Toast.makeText(WallpaperDetailActivity.this,"Image Downloaded",Toast.LENGTH_SHORT).show();
    }



    private void cropImage() {
        Toast.makeText(WallpaperDetailActivity.this,"Loading ..",Toast.LENGTH_SHORT).show();

        CropImage.activity(BitmapUtils.bitmapToUriConverter(WallpaperDetailActivity.this,
                mBitmap))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(WallpaperDetailActivity.this);
    }

    private void setWallpaper() {


        Toast.makeText(WallpaperDetailActivity.this,"Setting wallpaper",Toast.LENGTH_SHORT).show();

        final Bitmap bitmap = mBitmap;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Set wallpaper")
                    .setPositiveButton("Lock screen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bitmap scaledbitmap = CoverWallpaperUtils.scaleBitmapForWallpaper(bitmap , height , width);

                            dispatchLockScreenWallpaper(scaledbitmap);

                        }
                    })
                    .setNegativeButton("Home screen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dispatchWallpaper(bitmap, height, width);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {

            dispatchWallpaper(bitmap, height, width);
        }
       }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchLockScreenWallpaper(Bitmap scaledbitmap) {

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

        try {
            wallpaperManager.setBitmap(scaledbitmap, null, true, WallpaperManager.FLAG_LOCK);
            CoverWallpaperSetter.setWallpaperToDatabase(this, mLoadedImageInfo);
            WallpaperJobDispatcher.cancelAllJob(WallpaperDetailActivity.this);

        } catch (IOException e) {

        }
    }

    private void dispatchWallpaper(Bitmap bitmap, int height, int width) {
        Log.v("WallpaperDetailActivity", "------------------- height " + height);
        CoverWallpaperSetter.scaleBitmapForWallpaper(bitmap, height, width, WallpaperDetailActivity.this, mLoadedImageInfo, false);

        WallpaperJobDispatcher.cancelAllJob(WallpaperDetailActivity.this);
    }

    private void requestRuntimePermission(int requectCode) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requectCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    mBitmap = bitmap;
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_CROP_WALLPAPER: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    cropImage();

                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_CODE_STORAGE_DOWNLOAD_WALLPAPER:{
                   if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    downloadImage();

                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_menu , menu);

        if(mLoadedImageInfo.getLoadingType().equals(LOADING_TYPE_PEXELS)) {
            MenuItem item = menu.findItem(R.id.action_detail_explore);
            item.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_detail_explore:
                exploreMoreAboutWallpaper();
                return true;

            case R.id.action_detail_share:
                shareWallpaper();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareWallpaper() {

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("image/png")
                .setStream(Uri.parse(mLoadedImageInfo.getPageUrl()))
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    private void exploreMoreAboutWallpaper() {
        Uri uri = Uri.parse(mLoadedImageInfo.getPageUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
