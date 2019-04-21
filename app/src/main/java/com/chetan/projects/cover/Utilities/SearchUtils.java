package com.chetan.projects.cover.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.chetan.projects.cover.Data.SearchDatabaseTable;
import com.chetan.projects.cover.R;

import java.util.ArrayList;
import java.util.List;

import static com.chetan.projects.cover.Data.SearchDatabaseTable.COL_WORD;

public class SearchUtils {
    private Context mContext;
    private Cursor[] mCursors;
    private List<Drawable> mDrawables;
    private List<String> mResult;
    private int mListSize;

    public SearchUtils(Context context){
        mContext = context;
    }

    public Cursor[] setOnClickState(final SearchDatabaseTable db , final boolean isBoth , final String query) {
                Cursor[] cursors;

                if(isBoth && !(query.isEmpty() || query.equals("") || query.equals(" "))){
                    cursors = new Cursor[2];
                    cursors[0] = db.getSearchedWordMatches(query, new String[]{COL_WORD});
                    cursors[1] = db.getWordMatches(query, new String[]{COL_WORD});

                }else{
                    cursors = new Cursor[1];
                    cursors[0] = db.getAllSearchedWordMatches(new String[]{COL_WORD});
                }
                mCursors = cursors;
                setSearchCursors();
                return cursors;
        }

    private void setSearchCursors() {
        mResult = new ArrayList<>();
        mDrawables = new ArrayList<>();
        int i = 0;
        int j;
        Cursor cursor;
        mListSize = 0;
        Log.v("" + mContext , "--------- Cursors:"+mCursors.length);
        for( j =0 ; j < mCursors.length ; j++) {
            cursor = mCursors[j];
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    mListSize = mListSize + cursor.getCount();
                    String s = cursor.getString(cursor.getColumnIndex(COL_WORD));

                    if( j!=0 && i<=10 ){
                        mResult.add(s);
                        mDrawables.add(mContext.getDrawable(R.drawable.ic_search_black_24dp));

                        Log.v("" + mContext , "------------------- j != 0 ");
                        Log.v("" + mContext , "------------------- Result add:"+s);
                        Log.v("" + mContext , "--------- ----------text:"+mResult.get(i));
                        Log.v("" + mContext , "-------------------- drawable:"+mDrawables.get(i));

                    }
                    if(j == 0) {
                        mResult.add(s);
                        mDrawables.add(mContext.getDrawable(R.drawable.ic_recent_search_white));

                        Log.v("" + mContext, "---------- j == 0 ");
                        Log.v("" + mContext, "----------- Result add:" + s);
                        Log.v("" + mContext, "--------- text:" + mResult.get(i));
                        Log.v("" + mContext, "--------- drawable:" + mDrawables.get(i));


                    }


                    i = i + 1;
                }
            }
            Log.v("" + mContext , "--------- size:"+mListSize);

        }
    }

    public int getListSize(){
        return mListSize;
    }

    public List<Drawable> getDrawables() {
        return mDrawables;
    }

    public List<String> getResult() {
        return mResult;
    }

    public Cursor[] getCursors(){return mCursors; }
}
