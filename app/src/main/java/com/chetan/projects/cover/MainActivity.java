package com.chetan.projects.cover;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.chetan.projects.cover.Adapter.SearchListAdapter;
import com.chetan.projects.cover.Data.SearchDatabaseTable;
import com.chetan.projects.cover.Fragments.Tab1;
import com.chetan.projects.cover.Fragments.Tab2;
import com.chetan.projects.cover.Fragments.Tab3;
import com.chetan.projects.cover.Fragments.TabAdapter;
import com.chetan.projects.cover.Network.ConnectionCheck;
import com.chetan.projects.cover.Service.CoverWallpaperSyncService;
import com.chetan.projects.cover.Utilities.CoverWallpaperUtils;
import com.chetan.projects.cover.Utilities.DisplayUtils;
import com.chetan.projects.cover.Utilities.PreferenceUtils;
import com.chetan.projects.cover.Utilities.SearchUtils;
import com.chetan.projects.cover.Utilities.TimeUtils;
import com.chetan.projects.cover.Utilities.startServiceFabUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.chetan.projects.cover.Data.SearchDatabaseTable.COL_WORD;
import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PIXABAY;
import static com.chetan.projects.cover.Network.JsonRequestConstants.SEND_LOADING_TYPE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SearchDatabaseTable db;
    private SearchListAdapter mSearchListAdapter;


    // fire base auth vars
    private static final int RC_SIGN_IN = 343;
    public static final String ANONYMOUS = "anonymous";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TabAdapter mTabAdapter;
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private String mUsername;
    private ListView mListView;
    private FloatingActionButton mFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        FirebaseSetup();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        setContentView(R.layout.activity_main);

        db = new SearchDatabaseTable(this);

        mListView = findViewById(R.id.search_options_listView);

        mFab = findViewById(R.id.floatingActionButton_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager = findViewById(R.id.viewpager);

        mTabAdapter = new TabAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mTabAdapter);
        viewPager.setOffscreenPageLimit(3);


        mTabLayout.setupWithViewPager(viewPager);

        connectionSetup();

    }


    private boolean connectionSetup() {
        if(!ConnectionCheck.isNetworkConnected(this)){
            Snackbar no_connection = Snackbar.make(mFab, "No Connection", Snackbar.LENGTH_LONG);
            no_connection.show();
        }else{
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupFabClickedDialog(MainActivity.this);
                }
            });
        }
        return true;
    }



    @Override
    protected void onResume() {
        connectionSetup();
        super.onResume();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void FirebaseSetup() {

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //    Toast.makeText(MainActivity.this, "Hello " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    mUsername = user.getDisplayName();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false, true)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build()))
                                    .setTosAndPrivacyPolicyUrls("https://superapp.example.com/terms-of-service.html",
                                            "https://superapp.example.com/privacy-policy.html")
                                    .setTheme(R.style.LoginTheme)
                                    .build(),
                            RC_SIGN_IN);

                }

            }
        };

    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in

                if (response.getProviderType().equals(new AuthUI.IdpConfig.GoogleBuilder().build().getProviderId())) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                }

            } else {
                Toast.makeText(this, "Sign In canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {
            return true;

        } else if (id == R.id.nav_setted_wallpapers) {
            Intent intent = new Intent(this, RecentWallpapersActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_downloaded) {

            Intent intent = new Intent(this, DownloadsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.folder_menu, menu);

        final SearchUtils searchUtils = new SearchUtils(this);

        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        MenuItem menuItemSearch = menu.findItem(R.id.search);

        menuItemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                mListView.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.GONE);
                viewPager.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mListView.setVisibility(View.GONE);
                mTabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                return true;
            }
        });

        searchView.setInputType(InputType.TYPE_CLASS_TEXT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                db.insertInSearchList(query);
                Intent intent = new Intent(MainActivity.this, WallpaperActivity.class);
                intent.putExtra(WallpaperActivity.SEND_FOLDER_INFO, query);
                intent.putExtra(SEND_LOADING_TYPE, LOADING_TYPE_PIXABAY);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                AsyncTask<Void, Void, Cursor[]> task = new AsyncTask<Void, Void, Cursor[]>() {
                    @Override
                    protected Cursor[] doInBackground(Void... voids) {

                        return searchUtils.setOnClickState(db, true, newText);
                    }

                    @Override
                    protected void onPostExecute(Cursor[] cursors) {
                        setMatch(searchUtils);
                    }
                };
                task.execute();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.show_wallpaper_entries:
                Intent intent = new Intent(this, EntrieActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void setMatch(SearchUtils searchUtils) {

        final Cursor cursors[] = searchUtils.getCursors();
        Log.v("" + this, "--------- sizeInActivity:" + searchUtils.getListSize());

        if (searchUtils.getListSize() > 0) {
            mSearchListAdapter = new SearchListAdapter
                    (this, searchUtils.getResult(), searchUtils.getDrawables());

            mListView.setAdapter(mSearchListAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    boolean isSearchedString = false;
                    Cursor cursor;
                    if (cursors[0] != null && position < cursors[0].getCount()) {
                        cursor = cursors[0];
                        cursor.moveToPosition(position);
                        isSearchedString = true;
                    } else {
                        cursor = cursors[1];
                        cursor.moveToPosition(position);
                    }
                    String s = cursor.getString(cursor.getColumnIndex(COL_WORD));

                    if (!isSearchedString) {
                        db.insertInSearchList(s);
                    }
                    Intent intent = new Intent(MainActivity.this, WallpaperActivity.class);
                    intent.putExtra(WallpaperActivity.SEND_FOLDER_INFO, s);
                    intent.putExtra(SEND_LOADING_TYPE, LOADING_TYPE_PIXABAY);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });
        }

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

                    }
                }
            }
        });
    }

    private int getInterval(RadioButton btn) {
        int id = btn.getId();
        switch (id) {
            case R.id.radioBtn_interval_30_min:
                return  2*CoverWallpaperUtils.INTERVAL_15_MIN;
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

        DisplayUtils.setDisplayResolution(width, height);
        Log.v("-------------", "----------- screen resolution :" + height + " x " + width);

        PreferenceUtils.setWallpaperIntervalPref(this, interval);

        Log.v("MainActivity", "----------- interval :" + interval);

        int pagePosition = viewPager.getCurrentItem();
        Intent intent = new Intent(MainActivity.this, CoverWallpaperSyncService.class);
        intent.setAction(CoverWallpaperUtils.ACTION_SET_WALLPAPERS);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_WIDTH, width);
        intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SCREEN_HEIGHT, height);

        if (pagePosition == 0) {
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SERVER, CoverWallpaperUtils.ACTION_SET_WALLPAPER_OF_PEXELS);
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING, CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING_DEFAULT);
        } else if (pagePosition == 1) {
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SERVER, CoverWallpaperUtils.ACTION_SET_WALLPAPER_OF_PIXABAY);
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING, CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING_FEATURED);
        } else if (pagePosition == 2) {
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SERVER, CoverWallpaperUtils.ACTION_SET_WALLPAPER_OF_PIXABAY);
            intent.putExtra(CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING, CoverWallpaperUtils.ACTION_SET_WALLPAPER_SEARCH_STRING_NEW);
        }
        Log.v("-------------", "----------- intent set");

        startService(intent);
    }
}