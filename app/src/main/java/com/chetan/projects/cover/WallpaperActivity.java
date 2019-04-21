package com.chetan.projects.cover;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.CategoryResultEntry;
import com.chetan.projects.cover.AAC.CategoryVersionEntry;
import com.chetan.projects.cover.Adapter.WallpaperAdapter;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Firebase.CategoryVersion;
import com.chetan.projects.cover.Firebase.PexelsSearchResults;
import com.chetan.projects.cover.Fragments.Tab1;
import com.chetan.projects.cover.Network.ConnectionCheck;
import com.chetan.projects.cover.Network.ImagesJsonLoadingPexels;
import com.chetan.projects.cover.Network.ImagesJsonLoadingPixabay;
import com.chetan.projects.cover.Network.JsonRequestConstants;
import com.chetan.projects.cover.Service.CoverWallpaperSyncService;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;
import com.chetan.projects.cover.Utilities.LoadedImagesUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;
import com.chetan.projects.cover.Utilities.TimeUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PIXABAY;
import static com.chetan.projects.cover.Network.JsonRequestConstants.SEND_LOADING_TYPE;


public class WallpaperActivity extends AppCompatActivity {


    private static final String LOG_TAG = Tab1.class.getSimpleName();

    private static final String LAYOUT_MANAGER_STATE_KEY = "layout_manager_state";
    private static final String IMAGES_INFO_STATE_KEY = "images_info_state";
    private static final String PREVIEW_URL_STATE_KEY = "PREVIEW_URL_state";
    public static final String CHILD_PEXELS_SEARCH_RESULTS_FIREBASE = "pexels_search_results";
    public static final int DEFUALT_PAGE_NO = 1;
    public static final String CATEGORIES_LIST_VERSION_FIREBASE_CHILD = "categories_list_version";


    public static String SEND_FOLDER_INFO = "folderInfo";

    private String currentLoadingType;

    private WallpaperAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgress;
    private String mCategory;
    private GridLayoutManager mLayoutManager;
    private Parcelable mListState;
    private DisplayMetrics mDisplayMetrics;
    private ArrayList<String> mLargeUrl;
    private ArrayList<String> mPreviewUrl;

    private ArrayList<LoadedImageInfo> mImagesInfo;
    private ArrayList<String> mStringedImagesInfo;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallpaper);

        Intent intent = getIntent();
        mCategory = intent.getStringExtra(SEND_FOLDER_INFO);
        currentLoadingType = intent.getStringExtra(SEND_LOADING_TYPE);

        mProgress = findViewById(R.id.progressBar_wallpaper);

        mRecyclerView = findViewById(R.id.recycleView_wallpaper);
        mLayoutManager = new GridLayoutManager(this  , 3);
        mRecyclerView.setLayoutManager(mLayoutManager);



        setUpLayout();

        Log.v(LOG_TAG , "------------------ After set up layout called ");

        if(savedInstanceState != null) {

            Log.v(LOG_TAG , "------------------ Saved Instance not null");

            mListState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY);
            mLayoutManager.onRestoreInstanceState(mListState);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mStringedImagesInfo = savedInstanceState.getStringArrayList(IMAGES_INFO_STATE_KEY);
            if(mStringedImagesInfo != null) {
                mImagesInfo = LoadedImagesUtils.createLoadedInagesInfo(mStringedImagesInfo);
            }
            mAdapter = new WallpaperAdapter(this , mImagesInfo);

            mRecyclerView.setAdapter(mAdapter);

            mProgress.setVisibility(View.GONE);


        }else {

            boolean networkConnected = ConnectionCheck.isNetworkConnected(this);

            if (currentLoadingType.equals(LOADING_TYPE_PIXABAY)) {
                startNetwork(DEFUALT_PAGE_NO);
                if(!networkConnected){
                    Snackbar.make(mRecyclerView , "No connection", Snackbar.LENGTH_LONG).show();
                }
            }else{
                if(networkConnected){
                    LoadPexelsData();
                }else{
                    loadFromDatabase();
                    Snackbar.make(mRecyclerView , "No connection", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void LoadPexelsData() {


        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mCategoriesRef = mFirebaseDatabase.getReference(CATEGORIES_LIST_VERSION_FIREBASE_CHILD);

        String search = mCategory.toLowerCase();
        Query query = mCategoriesRef
                .orderByChild("name")
                .equalTo(search);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                CategoryVersion version = dataSnapshot.getValue(CategoryVersion.class);

                Log.v("WallpaperActivity", "----------- version obtained");

                if(version != null){

                    Log.v("WallpaperActivity", "----------- version not null");

                    Log.v("WallpaperActivity", "----------- version :" + version.getVersion());

                    List<CategoryVersionEntry> categoryVersionEntries = AppDatabase.getInstance(WallpaperActivity.this)
                            .categoryVersionDao().loadAllCategoryVersionEntries();

                    Log.v("WallpaperActivity", "----------- version list obtained size:" + categoryVersionEntries.size());


                    CategoryVersionEntry categoryVersionEntry = null;
                    for(CategoryVersionEntry entry : categoryVersionEntries){
                        if(entry.getName().equalsIgnoreCase(mCategory)){
                            categoryVersionEntry = entry;
                            Log.v("WallpaperActivity", "-----------category version obtained");

                        }
                    }

                    if(categoryVersionEntry != null){


                        Log.v("WallpaperActivity", "----------- categoryVersionEntry not null");

                        Log.v("WallpaperActivity", "----------- categoryVersion : "+ categoryVersionEntry.getVersion());
                        if(categoryVersionEntry.getVersion() == version.getVersion()){

                            Log.v("WallpaperActivity", "----------- both equal");
                            loadFromDatabase();
                        }
                        else{

                            Log.v("WallpaperActivity", "----------- version not equal");
                            pushToDatabase();
                        }
                    }else{

                        Log.v("WallpaperActivity", "----------- categoryVersionEntry null");
                        pushToDatabase();
                    }
                }else{

                    Log.v("WallpaperActivity", "----------- version nul");
                    pushToDatabase();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void pushToDatabase() {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mCategoriesRef = mFirebaseDatabase.getReference(CHILD_PEXELS_SEARCH_RESULTS_FIREBASE);

        String search = mCategory.toLowerCase();
        Query query = mCategoriesRef
                .orderByChild("search_string")
                .equalTo(search);


        Log.v("WallpaperActivity", "----------- query created");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.v("WallpaperActivity", "--------------- snapshot obtained");

                PexelsSearchResults value = dataSnapshot.getValue(PexelsSearchResults.class);


                Log.v("WallpaperActivity", "----------- PexelsSearchResult obtained");

                ImagesJsonLoadingPexels.parsePexelsSearchResult(value.getSearch_result_json());

                Log.v("WallpaperActivity", "----------- parse result");

                mImagesInfo = ImagesJsonLoadingPexels.getLoadedImages();
                mAdapter = new WallpaperAdapter(getApplicationContext(), mImagesInfo);
                mProgress.setVisibility(View.GONE);
                mRecyclerView.setAdapter(mAdapter);


                AppDatabase.getInstance(WallpaperActivity.this)
                        .categoryResultDao()
                        .insertCategoryResultEntry(new CategoryResultEntry(
                                mCategory.toLowerCase(),
                                value.getSearch_result_json(),
                                value.getUpload_date(),
                                value.getVersion()
                        ));


                Log.v("WallpaperActivity", "----------- result pushed to database");


                Log.v(LOG_TAG , "loading from firebase of categories done");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mCategoriesRef = mFirebaseDatabase.getReference(CATEGORIES_LIST_VERSION_FIREBASE_CHILD);

        Query query2 = mCategoriesRef
                .orderByChild("name")
                .equalTo(search);


        Log.v("WallpaperActivity", "----------- query created");

        query2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.v("WallpaperActivity", "--------------- snapshot obtained");

                CategoryVersion value = dataSnapshot.getValue(CategoryVersion.class);


                Log.v("WallpaperActivity", "----------- CategoryVersion value obtained");


                AppDatabase.getInstance(WallpaperActivity.this)
                        .categoryVersionDao()
                        .insertCategoryVersionEntry(new CategoryVersionEntry(
                                value.getName(),
                                value.getVersion()
                        ));


                Log.v("WallpaperActivity", "----------- category version result pushed to database");


                Log.v(LOG_TAG , "loading from firebase of categories done");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadFromDatabase() {
        AsyncTask<Void,Void,ArrayList<LoadedImageInfo>> task = new AsyncTask<Void, Void, ArrayList<LoadedImageInfo>>() {
            @Override
            protected ArrayList<LoadedImageInfo> doInBackground(Void... voids) {
                CategoryResultEntry categoryResultEntry = null;

                List<CategoryResultEntry> categoryResultEntries = AppDatabase
                        .getInstance(WallpaperActivity.this)
                        .categoryResultDao()
                        .loadAllCategoryResultEntries();
                Log.v("WallpaperActivity", "----------- category result list obtained size :" + categoryResultEntries.size());


                for(CategoryResultEntry entry : categoryResultEntries){
                    if(entry.getSearch_string().equalsIgnoreCase(mCategory)){
                        categoryResultEntry = entry;
                        Log.v("WallpaperActivity", "----------- category result obtained");

                    }
                }


                Log.v("WallpaperActivity", "----------- categoryResultEntry loaded");

                ImagesJsonLoadingPexels.parsePexelsSearchResult(categoryResultEntry.getSearch_result_json());


                Log.v("WallpaperActivity", "----------- parse result");

                ArrayList<LoadedImageInfo> loadedImageInfos = ImagesJsonLoadingPexels.getLoadedImages();


                Log.v("WallpaperActivity", "----------- list obtained size :" + loadedImageInfos.size());

                return loadedImageInfos;
            }

            @Override
            protected void onPostExecute(ArrayList<LoadedImageInfo> imageInfos) {
                mImagesInfo = imageInfos;
                mAdapter = new WallpaperAdapter(getApplicationContext(), mImagesInfo);
                mProgress.setVisibility(View.GONE);
                mRecyclerView.setAdapter(mAdapter);
            }
        };
        task.execute();
    }

    private void setUpLayout() {
        setTitle(mCategory);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    private void startNetwork(final int page) {
        AsyncTask<Void , Void , ImagesJsonLoadingPixabay> task = new AsyncTask<Void, Void, ImagesJsonLoadingPixabay>() {
            @Override
            protected ImagesJsonLoadingPixabay doInBackground(Void... voids) {
                ImagesJsonLoadingPixabay loading = new ImagesJsonLoadingPixabay(
                        JsonRequestConstants.PixabayAPI.CATEGORY_IMAGE_LOADING ,mCategory , page);
                return loading;
            }

            @Override
            protected void onPostExecute(ImagesJsonLoadingPixabay imagesJsonLoading) {

                 mImagesInfo = imagesJsonLoading.getLoadedImages();
                mAdapter = new WallpaperAdapter(getApplicationContext(), mImagesInfo );
                mProgress.setVisibility(View.GONE);
                mRecyclerView.setAdapter(mAdapter);

            }

        };
        task.execute();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mLayoutManager != null) {
            mListState = mLayoutManager.onSaveInstanceState();
            outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, mListState);
        }

        if(mImagesInfo != null) {
            mStringedImagesInfo = LoadedImagesUtils.createStringedImagesInfo(mImagesInfo);

            outState.putStringArrayList(IMAGES_INFO_STATE_KEY, mStringedImagesInfo);
        }
        super.onSaveInstanceState(outState);
    }




    private void setupFabClickedDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Randomly set wallpaper")
                .setMessage("Are you sure you want to set wallpapers randomly?")
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
    }

    private void radioSelectionDialog(Context context) {
        final Dialog dialog2 = new Dialog(context);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.radiobutton_dialog);
        List<String> stringList = new ArrayList<>();  // here is list

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

        Intent intent  = new Intent(WallpaperActivity.this , CoverWallpaperSyncService.class);
        intent.setAction(CoverWallpaperUtils.ACTION_SET_WALLPAPERS);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_WIDTH , width);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_HEIGHT , height);

        if(currentLoadingType.equals(LOADING_TYPE_PIXABAY)) {
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SERVER, CoverWallpaperUtils.ACTION_SET_WALLPAPER_OF_PIXABAY);
        }else{
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SERVER, CoverWallpaperUtils.ACTION_SET_WALLPAPER_OF_PEXELS);
        }
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING, mCategory);

        Log.v("-------------" , "----------- intent set");

        //AppDatabase.getInstance(this).loadedImageDao().deleteAllLoadedImageEntry();

        startService(intent);
    }

}
