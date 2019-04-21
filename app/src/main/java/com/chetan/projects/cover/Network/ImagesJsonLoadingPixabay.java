package com.chetan.projects.cover.Network;

import android.net.Uri;
import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.Network.JsonRequestConstants.PexelsAPI;
import com.chetan.projects.cover.Network.JsonRequestConstants.PixabayAPI;
import com.chetan.projects.cover.WallpaperActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PIXABAY;

public class ImagesJsonLoadingPixabay {


    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private String mResult = null;
    private static ArrayList<LoadedImageInfo> mLoadedImages = new ArrayList<>();

    public ImagesJsonLoadingPixabay(int loadingType , String category , int page) {
        Uri uri;

        switch (loadingType) {
            case PixabayAPI.LATEST_IMAGE_LOADING :uri = imageUriBuilderLatest();
                break;
            case PixabayAPI.POPULAR_IMAGE_LOADING :uri = imageUriBuilderPopular();
                break;

            default:uri = imageUriBuilderCategory(category , page);
        }
        Log.v("this" , "---------------------------------------- uri :" + uri);
        URL url = null;
        try{
            url = new URL(uri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        try {
            mResult = getResponseFromHttpUrl(url);
            Log.v(LOG_TAG , "----------------Response obtained : page :" + page);


        }catch (IOException e){        }

        if(mResult != null){
            parsePixabayJsonResult();
        }
    }


    private Uri imageUriBuilderCategory(String category , int page) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(JsonRequestConstants.PARAM_SCHEME)
                .authority(PixabayAPI.PIXABAY_BASE)
                .appendPath(PixabayAPI.API_PATH)
                .appendQueryParameter(PixabayAPI.KEY , PixabayAPI.KEY_VALUE)
                .appendQueryParameter(JsonRequestConstants.PARAM_QUERY , category)
                .appendQueryParameter(PixabayAPI.PAGE_NO , "" + page)
                .appendQueryParameter(PixabayAPI.IMAGE_PER_PAGE , PixabayAPI.IMAGE_PER_PAGE_RESULTS)
                .appendQueryParameter(PixabayAPI.IMAGE_EDITORS_CHOICE , ""+PixabayAPI.IMAGE_EDITORS_CHOICE_TRUE);
        return builder.build();

    }

    private Uri imageUriBuilderLatest() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(JsonRequestConstants.PARAM_SCHEME)
                .authority(PixabayAPI.PIXABAY_BASE)
                .appendPath(PixabayAPI.API_PATH)
                .appendQueryParameter(PixabayAPI.KEY , PixabayAPI.KEY_VALUE)
                .appendQueryParameter(PixabayAPI.IMAGE_PER_PAGE , PixabayAPI.IMAGE_PER_PAGE_RESULTS)
                .appendQueryParameter(PixabayAPI.IMAGE_EDITORS_CHOICE , ""+PixabayAPI.IMAGE_EDITORS_CHOICE_TRUE)
                .appendQueryParameter(PixabayAPI.IMAGE_ORDER , PixabayAPI.IMAGE_ORDER_LATEST);
        return builder.build();

    }
    private Uri imageUriBuilderPopular() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(JsonRequestConstants.PARAM_SCHEME)
                .authority(PixabayAPI.PIXABAY_BASE)
                .appendPath(PixabayAPI.API_PATH)
                .appendQueryParameter(PixabayAPI.KEY , PixabayAPI.KEY_VALUE)
                .appendQueryParameter(PixabayAPI.IMAGE_PER_PAGE , PixabayAPI.IMAGE_PER_PAGE_RESULTS)
                .appendQueryParameter(PixabayAPI.IMAGE_EDITORS_CHOICE , ""+PixabayAPI.IMAGE_EDITORS_CHOICE_TRUE)
                .appendQueryParameter(PixabayAPI.IMAGE_ORDER , PixabayAPI.IMAGE_ORDER_POPULAR);
        return builder.build();

    }


    private String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        Log.v("this" , "----------------------------------------connection done");
        try{
            InputStream stream = connection.getInputStream();

            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput){

                return scanner.next();
            }else{
                return null;
            }
        }finally {
            connection.disconnect();
        }
    }

    private void parsePixabayJsonResult() {
        try {

            mLoadedImages.clear();

            JSONObject obj = new JSONObject(mResult);

            JSONArray array = obj.getJSONArray(PixabayAPI.JSON_READ_IMAGE_HITS);

            for(int i =0 ; i< array.length() ; i++){
                JSONObject o = array.getJSONObject(i);
                String largeImageUrl = o.getString(PixabayAPI.JSON_READ_IMAGE_LARGE_URL);
                String previewUrl = o.getString(PixabayAPI.IMAGE_PREVIEW_URL);
                String pageUrl = o.getString(PixabayAPI.JSON_READ_IMAGE_PAGE_URL);
                String userName = o.getString(PixabayAPI.JSON_READ_IMAGE_USER_NAME);
                String userId = "" + o.getInt(PixabayAPI.JSON_READ_IMAGE_USER_ID);
                String type = o.getString(PixabayAPI.JSON_READ_IMAGE_TYPE);

                String tags = o.getString("tags");

                setValuesToList(largeImageUrl,largeImageUrl, previewUrl, pageUrl, userName  , userId, tags);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setValuesToList(String largeImageUrl, String mediumImageUrl,
                                        String previewUrl, String pageUrl, String userName, String userId, String tags) {
        mLoadedImages.add(new LoadedImageInfo(largeImageUrl, mediumImageUrl, previewUrl,
                pageUrl, userName, userId, tags,LOADING_TYPE_PIXABAY));
    }


    public String getResult(){
        return mResult;
    }

    public ArrayList<LoadedImageInfo> getLoadedImages(){ return mLoadedImages; }


}
