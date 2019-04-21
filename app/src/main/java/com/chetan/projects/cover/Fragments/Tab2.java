package com.chetan.projects.cover.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.CategoryResultEntry;
import com.chetan.projects.cover.Adapter.LatestImagesAdapter;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Firebase.PexelsSearchResults;
import com.chetan.projects.cover.Network.ImagesJsonLoadingPexels;
import com.chetan.projects.cover.R;
import com.chetan.projects.cover.Utilities.LoadedImagesUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment {


    private static final String LAYOUT_MANAGER_STATE_KEY = "layout_manager_state";
    private static final String IMAGES_INFO_STATE_KEY = "image_info_state";
    public static final String FEATURED_VER_PREFRENCE = "featured_ver_prefrence";
    public static final String FEATURED_VER_PREF_VERSION = "featured_ver_pref_version";
    public static final String FEATURED_VERSION_FIREBASE_CHILD = "featured_version";

    private ArrayList<String> mStringedImaagesInfo = new ArrayList<>();
    private ArrayList<String> mPreviewUrl;
    private LatestImagesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private Parcelable mListState;
    private GridLayoutManager mLayoutManager;
    private ArrayList<LoadedImageInfo> mLoadedImages = new ArrayList<>();
    private DisplayMetrics mDisplayMetrics;

    public Tab2() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mLayoutManager!=null) {
            mListState = mLayoutManager.onSaveInstanceState();
            outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, mListState);
        }
        mStringedImaagesInfo = LoadedImagesUtils.createStringedImagesInfo(mLoadedImages);

        outState.putStringArrayList(IMAGES_INFO_STATE_KEY, mStringedImaagesInfo);
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab, container , false);


        mDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        setUpLayout(rootView);

        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY);
            mLayoutManager.onRestoreInstanceState(mListState);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mStringedImaagesInfo = savedInstanceState.getStringArrayList(IMAGES_INFO_STATE_KEY);
            mLoadedImages = LoadedImagesUtils.createLoadedInagesInfo(mStringedImaagesInfo);

            mAdapter = new LatestImagesAdapter(getActivity(), mLoadedImages);
            mRecyclerView.setAdapter(mAdapter);


        }else { startNetwork(); }

        return rootView;
    }

    @Override
    public void onResume() {
        loadFeaturedFromDatabase();
        super.onResume();
    }

    private void startNetwork() {

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference mCategoryVersion = mFirebaseDatabase.getReference(FEATURED_VERSION_FIREBASE_CHILD);

        mCategoryVersion.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Long featuredVer = (Long)dataSnapshot.getValue();

                Log.v("MainActivity:tab2" , "--------------get categories version from firebase: " + featuredVer);

                SharedPreferences preferences = getActivity().getSharedPreferences(FEATURED_VER_PREFRENCE, MODE_PRIVATE);
                Long version = preferences.getLong(FEATURED_VER_PREF_VERSION , -1);


                Log.v("MainActivity:tab2" , "-----------------last categories version : " + version);

                if(version == featuredVer){


                    mProgressBar.setVisibility(View.GONE);
                    Log.v("MainActivity:tab2" , "-------------------last and new version both are same");
                    loadFeaturedFromDatabase();
                }else {

                    Toast toast = new Toast(getActivity());

                    toast.cancel();

                    toast.makeText(getActivity() , "Categories are updated!" + version , Toast.LENGTH_SHORT).show();

                    pushFeaturedToDatabase();

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(FEATURED_VER_PREFRENCE, MODE_PRIVATE).edit();
                    editor.putLong(FEATURED_VER_PREF_VERSION, featuredVer);
                    editor.apply();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //    CategoriesVer categoriesVer = dataSnapshot.getValue(CategoriesVer.class);
                //      int categoriesVersion = Integer.parseInt(categoriesVer.getVersion());

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

    private void pushFeaturedToDatabase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference reference = firebaseDatabase.getReference("featured");

        Query query = reference.orderByChild("search_string")
                .equalTo("featured");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PexelsSearchResults value = dataSnapshot.getValue(PexelsSearchResults.class);

                ImagesJsonLoadingPexels.parsePexelsSearchResult(value.getSearch_result_json());

                mLoadedImages = ImagesJsonLoadingPexels.getLoadedImages();
                mAdapter = new LatestImagesAdapter(getActivity(), mLoadedImages);
                mRecyclerView.setAdapter(mAdapter);

                AppDatabase
                        .getInstance(getActivity())
                        .categoryResultDao()
                        .insertCategoryResultEntry(new CategoryResultEntry(
                                value.getSearch_string(),
                                value.getSearch_result_json(),
                                value.getUpload_date(),
                                value.getVersion()
                        ));
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

    private void loadFeaturedFromDatabase() {
        List<CategoryResultEntry> categoryResultEntries = AppDatabase
                .getInstance(getActivity())
                .categoryResultDao()
                .loadAllCategoryResultEntries();

        CategoryResultEntry categoryResultEntry = null;

        for(CategoryResultEntry entry: categoryResultEntries){
            if(entry.getSearch_string().equalsIgnoreCase("featured")){
                categoryResultEntry = entry;
            }
        }

        if(categoryResultEntry != null){
            ImagesJsonLoadingPexels.parsePexelsSearchResult(categoryResultEntry.getSearch_result_json());

            mLoadedImages = ImagesJsonLoadingPexels.getLoadedImages();

            mAdapter = new LatestImagesAdapter(getActivity(), mLoadedImages);
            mRecyclerView.setAdapter(mAdapter);
        }


    }


    private void setUpLayout(View root) {

        mProgressBar = root.findViewById(R.id.progressBar_wallpaper);
        mProgressBar.setVisibility(View.VISIBLE);

        mRecyclerView = root.findViewById(R.id.recycleView_wallpaper);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        root.findViewById(R.id.progressBar_wallpaper).setVisibility(View.GONE);

        CoordinatorLayout coordinatorLayout = root.findViewById(R.id.root_wallpaper);
        coordinatorLayout.setBackgroundColor(getResources().getColor(android.R.color.white));

    }
}