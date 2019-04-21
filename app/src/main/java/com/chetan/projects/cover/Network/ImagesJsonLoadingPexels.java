package com.chetan.projects.cover.Network;

import android.util.Log;

import com.chetan.projects.cover.AAC.AppDatabase;
import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.WallpaperActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.chetan.projects.cover.Network.JsonRequestConstants.LOADING_TYPE_PEXELS;

public class ImagesJsonLoadingPexels {


    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static ArrayList<LoadedImageInfo> mLoadedImages = new ArrayList<>();


    public static void parsePexelsSearchResult(String result){

        result = convertStandardJSONString(result);


        try {
            JSONObject obj = new JSONObject(result);

            JSONArray array = obj.getJSONArray(JsonRequestConstants.PexelsAPI.JSON_READ_IMAGES);

            mLoadedImages.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);

                JSONObject o2 = o.getJSONObject(JsonRequestConstants.PexelsAPI.JSON_READ_IMAGE_URLS);
                String largeImageUrl = o2.getString(JsonRequestConstants.PexelsAPI.IMAGE_LARGE_URL);

                String previewUrl = o2.getString(JsonRequestConstants.PexelsAPI.IMAGE_PREVIEW_URL);
                String mediumUrl = o2.getString(JsonRequestConstants.PexelsAPI.IMAGE_MEDIUM_URL);

                String pageUrl = o.getString(JsonRequestConstants.PexelsAPI.JSON_READ_IMAGE_PAGE_URL);
                String userName = o.getString(JsonRequestConstants.PexelsAPI.JSON_READ_IMAGE_USER_NAME);
                String userId =  o.getString(JsonRequestConstants.PexelsAPI.JSON_READ_IMAGE_USER_ID);

                setValuesToList(largeImageUrl, mediumUrl, previewUrl, pageUrl, userName, userId, null);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void setValuesToList(String largeImageUrl, String mediumImageUrl, String previewUrl, String pageUrl, String userName, String userId, String tags) {

        mLoadedImages.add(new LoadedImageInfo(largeImageUrl, mediumImageUrl, previewUrl,
                pageUrl, userName, userId, tags,
                LOADING_TYPE_PEXELS));
    }


    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replace("\\\"", "\"");
        return data_json;
    }


    public static ArrayList<LoadedImageInfo> getLoadedImages() {
        return mLoadedImages;
    }


}
