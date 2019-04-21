package com.chetan.projects.cover;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chetan.projects.cover.Adapter.DownloadsAdapter;
import com.chetan.projects.cover.Service.CoverWallpaperSyncService;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;

import java.io.File;

public class DownloadsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_STORAGE = 1;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager mLayoutManager;
    private String[] mFileNames;
    private DownloadsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_wallpapers);

        fab = (FloatingActionButton) findViewById(R.id.fab_recent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // setupFabClickedDialog(DownloadsActivity.this);
            }
        });

        recyclerView = findViewById(R.id.recyclerView_recent);
        progressBar = findViewById(R.id.progressBar_recent);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestRuntimePermission();
        }
        else {
            loadDownloads();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void loadDownloads() {

        File path = new File(Environment.getExternalStorageDirectory(),"CoverDownloads");
        if(path.exists()) {
            mFileNames = path.list();
            mAdapter = new DownloadsAdapter(this,mFileNames);
            recyclerView.setAdapter(mAdapter);
            progressBar.setVisibility(View.GONE);
        }

    }

    private void requestRuntimePermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_STORAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    loadDownloads();

                } else {

                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    private void setupFabClickedDialog(final Context context) {
        if(mFileNames.length > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Set recent wallpapers")
                    .setMessage("Are you sure you want download images to set wallpapers randomly?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            radioSelectionDialog(context);

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }else{
            Toast.makeText(this,"No downloads found", Toast.LENGTH_SHORT).show();
        }
    }

    private void radioSelectionDialog(Context context) {
        final Dialog dialog2 = new Dialog(context);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.radiobutton_dialog);

        setupRadioButtonClicks(dialog2);
        dialog2.show();
    }

    private void setupRadioButtonClicks(final Dialog dialog2) {
        RadioGroup rg = (RadioGroup) dialog2.findViewById(R.id.radio_group);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Log.e("selected RadioButton->", btn.getText().toString());

                        int interval = getInterval(btn);
                        scheduleWallpaperSettingService(interval);

                        dialog2.dismiss();
                    }
                }
            }
        });
    }

    private int getInterval(RadioButton btn) {
        int id = btn.getId();
        switch (id) {
            case R.id.radioBtn_interval_30_min:
                return 2 * CoverWallpaperUtils.INTERVAL_15_MIN;
            case R.id.radioBtn_interval_1_hour:
                return CoverWallpaperUtils.INTERVAL_1_HOUR;
            case R.id.radioBtn_3_hour:
                return 3 * CoverWallpaperUtils.INTERVAL_1_HOUR;
            case R.id.radioBtn_6_hour:
                return 6 * CoverWallpaperUtils.INTERVAL_1_HOUR;
            case R.id.radioBtn_1_day:
                return 24 * CoverWallpaperUtils.INTERVAL_1_HOUR;
            case R.id.radioBtn_2_day:
                return 48 * CoverWallpaperUtils.INTERVAL_1_HOUR;
            default:
                return 0;
        }
    }




    private void scheduleWallpaperSettingService(int interval) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

        DisplayUtils.setDisplayResolution(width , height);
        Log.v("-------------" , "----------- screen resolution :" + height + " x " + width);

        PreferenceUtils.setWallpaperIntervalPref(this , interval);

        Intent intent  = new Intent(this , CoverWallpaperSyncService.class);
        intent.setAction(CoverWallpaperUtils.ACTION_RECENT_DOWNLOADS_WALLPAPERS);
        intent.putExtra(CoverWallpaperUtils.RECENT_DOWNLOADS_WALLPAPERS, mFileNames);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_WIDTH , width);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_HEIGHT , height);


        startService(intent);
    }
}
