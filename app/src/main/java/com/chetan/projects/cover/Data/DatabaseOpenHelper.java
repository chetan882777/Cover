package com.chetan.projects.cover.Data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chetan.projects.cover.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.provider.BaseColumns._ID;
import static com.chetan.projects.cover.Data.AutoWallpaperDatabaseTable.*;
import static com.chetan.projects.cover.Data.SearchDatabaseTable.COL_WORD;
import static com.chetan.projects.cover.Data.SearchDatabaseTable.COL_WORD_TYPE;
import static com.firebase.ui.auth.AuthUI.TAG;

public class DatabaseOpenHelper extends SQLiteOpenHelper {


    private static final String TAG = "SearchDatabase";
    private static final String _ID = "ID";
    private static final String DATABASE_NAME = "COVER_BASE_2";
    private static final int DATABASE_VERSION = 1;
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final String FTS_SEARCH_VIRTUAL_TABLE = "FTSsearch";

    private final Context mHelperContext;
    private SQLiteDatabase mDatabase;


    // create virtual table FTS using fts3( WORD TEXT  )
    private static final String FTS_TABLE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                    " USING fts3 (" +
                    COL_WORD +" " +COL_WORD_TYPE +" )";


    private static final String FTS_SEARCH_TABLE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_SEARCH_VIRTUAL_TABLE +
                    " USING fts3 (" +
                    _ID + " INTEGER PRIMARY KEY , " +
                    COL_WORD +" " +COL_WORD_TYPE +" )";

    private static final String AUTOSET_TABLE_CREATE= "CREATE TABLE " + TABLE_NAME +
            " (" + _ID + " INTEGER PRIMARY KEY , "+
            COL_STRINGED_BITMAP+ " TEXT, " +
            COL_LARGE_IMAGE_URL + " TEXT, " +
            COL_PREVIEW_IMAGE_URL + " TEXT, " +
            COL_MEDIUM_IMAGE_URL + " TEXT, " +
            COL_PAGE_URL + " TEXT, " +
            COL_USER_NAME + " TEXT, " +
            COL_USER_ID + " TEXT, " +
            COL_TAGS + " TEXT, " +
            COL_LOADING_TYPE + " TEXT )";



    DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        mDatabase.execSQL(FTS_TABLE_CREATE);
        mDatabase.execSQL(FTS_SEARCH_TABLE_CREATE);
        mDatabase.execSQL(AUTOSET_TABLE_CREATE);
        loadDictionary();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FTS_SEARCH_VIRTUAL_TABLE);

        onCreate(db);
    }





    private void loadDictionary() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadWords();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void loadWords() throws IOException {
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(mHelperContext);
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.words);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String string = line;

                long id = addWord(string.trim(), databaseOpenHelper);
                if (id < 0) {
                    Log.e(TAG, "unable to add word: " + string.trim());
                }
            }
        } finally {
            reader.close();
        }
    }

    public long addWord(String word, DatabaseOpenHelper databaseOpenHelper) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_WORD, word);

        mDatabase = databaseOpenHelper.getWritableDatabase();

        return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
    }

    public long addSearchWord(String word, DatabaseOpenHelper databaseOpenHelper) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(COL_WORD, ""+word);
        mDatabase = databaseOpenHelper.getWritableDatabase();
        return mDatabase.insert(FTS_SEARCH_VIRTUAL_TABLE, null, initialValues);
    }





}
