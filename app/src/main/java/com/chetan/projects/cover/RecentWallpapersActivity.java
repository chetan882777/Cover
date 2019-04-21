package com.chetan.projects.cover;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.SetWallpaperEntry;
import com.chetan.projects.cover.Adapter.WallpaperAdapter;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Service.CoverWallpaperSyncService;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PIXABAY;

public class RecentWallpapersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private GridLayoutManager mLayoutManager;
    private List<LoadedImageInfo> mLoadedImageInfos;
    private WallpaperAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_wallpapers);
        setTitle("Recent Wallpapers");

        mLoadedImageInfos = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab_recent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupFabClickedDialog(RecentWallpapersActivity.this);
            }
        });

        recyclerView = findViewById(R.id.recyclerView_recent);
        progressBar = findViewById(R.id.progressBar_recent);

        mLayoutManager = new GridLayoutManager(this  , 3);
        recyclerView.setLayoutManager(mLayoutManager);

        loadData();
    }


    private void loadData() {
        AsyncTask<Void,Void,List<LoadedImageInfo>> task = new AsyncTask<Void, Void, List<LoadedImageInfo>>() {
            @Override
            protected List<LoadedImageInfo> doInBackground(Void... voids) {
                AppDatabase instance = AppDatabase.getInstance(RecentWallpapersActivity.this);

                List<LoadedImageInfo> infos = new ArrayList<>();
                List<SetWallpaperEntry> setWallpaperEntries = instance.setWallpaperDao().loadAllWallpaperEntries();
                for(SetWallpaperEntry entry: setWallpaperEntries){
                    infos.add(new LoadedImageInfo(entry.getLargeImageUrl(),
                            entry.getMediumImageUrl(),
                            entry.getPreviewUrl(),
                            entry.getPageUrl(),
                            entry.getUserName(),
                            entry.getUserId(),
                            entry.getTags(),
                            entry.getLoadingType()));
                }
                return infos;
            }

            @Override
            protected void onPostExecute(List<LoadedImageInfo> imageInfos) {
                mLoadedImageInfos = imageInfos;
                mAdapter = new WallpaperAdapter(RecentWallpapersActivity.this,
                        mLoadedImageInfos);

                recyclerView.setAdapter(mAdapter);

                progressBar.setVisibility(View.GONE);
            }
        };
        task.execute();
    }

    private void setupFabClickedDialog(final Context context) {
        if(mLoadedImageInfos.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Set recent wallpapers")
                    .setMessage("Are you sure you want to set recent wallpapers randomly?")
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
            Toast.makeText(this,"No recent wallpapers", Toast.LENGTH_SHORT).show();
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
        intent.setAction(CoverWallpaperUtils.ACTION_RECENT_WALLPAPERS);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_WIDTH , width);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_HEIGHT , height);


        startService(intent);
    }
}
