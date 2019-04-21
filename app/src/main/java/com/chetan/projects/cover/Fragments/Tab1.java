package com.chetan.projects.cover.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.AAC.CategoriesEntry;
import com.chetan.projects.cover.Adapter.FolderAdapter;
import com.chetan.projects.cover.Firebase.Category;
import com.chetan.projects.cover.Network.ConnectionCheck;
import com.chetan.projects.cover.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1 extends Fragment {

    public static final String CHILD_CATEGORIES_VERSION_FIREBASE = "categories_version";
    public static final String CATEGORIES_VER_PREFERENCE = "categoriesVer";
    public static final String CATEGORY_VER_PREF_VERSION = "version";
    public static final String CHILD_CATEGORIES_FIREBASE = "categories";
    private RecyclerView mRecyclerView;
    private FolderAdapter mAdapter;
    private ProgressBar mProgressBar;
    private FirebaseDatabase mFirebaseDatabase;
    private List<CategoriesEntry> mCategoryList = new ArrayList<>();
    private AppDatabase db;
    private static final String LOG_TAG = Tab1.class.getSimpleName();


    public Tab1(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tab, container , false);

        setUpLayout(root);

        mAdapter = new FolderAdapter(getActivity(), mCategoryList);

        mRecyclerView.setAdapter(mAdapter);

        db = AppDatabase.getInstance(getActivity());

        if(savedInstanceState == null) {
            if(ConnectionCheck.isNetworkConnected(getActivity())){
                FirebaseLoading();
            }else{
                loadCategoriesFromDatabase();
            }
        }
        else{

            mProgressBar.setVisibility(View.GONE);
            loadCategoriesFromDatabase();
        }
        return root;
    }

    private void FirebaseLoading() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference mCategoryVersion = mFirebaseDatabase.getReference(CHILD_CATEGORIES_VERSION_FIREBASE);

        mCategoryVersion.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Long categoriesVer = (Long)dataSnapshot.getValue();

                Log.v(LOG_TAG , "get categories version from firebase: " + categoriesVer);

                SharedPreferences preferences = getActivity().getSharedPreferences(CATEGORIES_VER_PREFERENCE, MODE_PRIVATE);
                Long version = preferences.getLong(CATEGORY_VER_PREF_VERSION , 1);


                Log.v(LOG_TAG , "last categories version : " + version);

                if(version == categoriesVer){


                    mProgressBar.setVisibility(View.GONE);
                    Log.v(LOG_TAG , "last and new version both are same");
                    loadCategoriesFromDatabase();
                }else {

                    Toast toast = new Toast(getActivity());

                    toast.cancel();

                    toast.makeText(getActivity() , "Categories are updated!" + version , Toast.LENGTH_SHORT).show();

                    pushCategoriesToDatabase();

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(CATEGORIES_VER_PREFERENCE, MODE_PRIVATE).edit();
                    editor.putLong(CATEGORY_VER_PREF_VERSION, categoriesVer);
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

    private void pushCategoriesToDatabase() {
        DatabaseReference mCategoriesRef = mFirebaseDatabase.getReference(CHILD_CATEGORIES_FIREBASE);
        mCategoriesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category value = dataSnapshot.getValue(Category.class);

                CategoriesEntry entry = new CategoriesEntry(
                        value.getCategoryName(),
                        value.getLargeUrl(),
                        value.getMediumUrl(),
                        value.getPreviewUrl());

                db.categoriesDao().insertCategoryEntry(entry);

                mAdapter.add(entry);
                mAdapter.notifyDataSetChanged();

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

        mProgressBar.setVisibility(View.GONE);
    }

    private void loadCategoriesFromDatabase() {
        List<CategoriesEntry> entries = db.categoriesDao().loadAllCategoryEntries();

        mAdapter.addList(entries);
        mAdapter.notifyDataSetChanged();

        Log.v(LOG_TAG , "loading of categories from database is been done");

    }

    private void setUpLayout(View root) {

        mProgressBar = root.findViewById(R.id.progressBar_wallpaper);

        mRecyclerView = root.findViewById(R.id.recycleView_wallpaper);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(layoutManager);

        mProgressBar.setVisibility(View.VISIBLE);

        CoordinatorLayout coordinatorLayout = root.findViewById(R.id.root_wallpaper);
        coordinatorLayout.setBackgroundColor(getResources().getColor(android.R.color.white));

    }
}
