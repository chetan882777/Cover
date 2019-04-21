package com.chetan.projects.cover.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.chetan.projects.cover.Adapter.LatestImagesAdapter;
import com.chetan.projects.cover.Adapter.NpaLayoutManager;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Network.ImagesJsonLoadingPixabay;
import com.chetan.projects.cover.Network.JsonRequestConstants;
import com.chetan.projects.cover.R;
import com.chetan.projects.cover.Utilities.LoadedImagesUtils;
import com.google.firebase.database.core.utilities.Utilities;

import java.util.ArrayList;

import static com.chetan.projects.cover.Adapter.NpaLayoutManager.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab3 extends Fragment {


    private static final String LAYOUT_MANAGER_STATE_KEY = "layout_manager_state";
    private static final String LARGE_URL_STATE_KEY = "LARGE_URL_state";
    private static final String PREVIEW_URL_STATE_KEY = "PREVIEW_URL_state";

    private static final String IMAGES_INFO_STATE_KEY = "image_info_state";



    private ArrayList<String> mStringedImaagesInfo = new ArrayList<>();
    private ArrayList<LoadedImageInfo> mLoadedImages = new ArrayList<>();
    private ListView mListView;
    private RecyclerView mRecyclerView;
    private DisplayMetrics mDisplayMetrics;
    private ArrayList<String> mLargeUrl;
    private ArrayList<String> mPreviewUrl;
    private LatestImagesAdapter mAdapter;
    private ProgressBar mProgressBar;
    private Parcelable mListState;
    private GridLayoutManager mLayoutManager;

    public Tab3() {
        // Required empty public constructor
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mLayoutManager!= null) {
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

    private void startNetwork() {
        AsyncTask<Void , Void , ImagesJsonLoadingPixabay> task = new AsyncTask<Void, Void, ImagesJsonLoadingPixabay>() {
            @Override
            protected ImagesJsonLoadingPixabay doInBackground(Void... voids) {
                ImagesJsonLoadingPixabay loading = new ImagesJsonLoadingPixabay(
                        JsonRequestConstants.PixabayAPI.LATEST_IMAGE_LOADING ,
                        null ,1);
                return loading;
            }

            @Override
            protected void onPostExecute(ImagesJsonLoadingPixabay imagesJsonLoading) {

                mLoadedImages = imagesJsonLoading.getLoadedImages();

                mAdapter = new LatestImagesAdapter(getContext(), mLoadedImages);
                //   mProgress.setVisibility(View.GONE);
                mRecyclerView.setAdapter(mAdapter);
                mProgressBar.setVisibility(View.GONE);
            }

        };
        task.execute();


    }


    private void setUpLayout(View root) {

        mListView = root.findViewById(R.id.search_options_listView);


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
