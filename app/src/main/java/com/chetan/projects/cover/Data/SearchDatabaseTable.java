package com.chetan.projects.cover.Data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.chetan.projects.cover.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SearchDatabaseTable {

    //The columns we'll include in the dictionary table
    public static final String COL_WORD = "WORD";
    public static final String COL_WORD_TYPE = "TEXT";

    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final String FTS_SEARCH_VIRTUAL_TABLE = "FTSsearch";

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public SearchDatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }


    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_WORD + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};
        return query(selection, selectionArgs, columns);
    }
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, COL_WORD);

        if (cursor == null) {
            return null;
        }
        return cursor;
    }



    public Cursor getAllSearchedWordMatches(String[] columns) {
        String selection = null;
        String[] selectionArgs = null;
        return querySearch(selection, selectionArgs, columns);
    }
    public Cursor getSearchedWordMatches( String query, String[] columns) {
        String selection = COL_WORD + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};
        return querySearch(selection, selectionArgs, columns);
    }
    private Cursor querySearch(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_SEARCH_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                    columns, selection, selectionArgs, null, null, null);
        // todo(1): need to fix sorting here;


        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        cursor.moveToPrevious();
        return cursor;
    }
    public long insertInSearchList(String query){
       return mDatabaseOpenHelper.addSearchWord(query, mDatabaseOpenHelper);
    }





}
