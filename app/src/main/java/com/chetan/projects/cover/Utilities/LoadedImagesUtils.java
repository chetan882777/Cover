package com.chetan.projects.cover.Utilities;

import com.chetan.projects.cover.Data.LoadedImageInfo;
import com.chetan.projects.cover.WallpaperActivity;

import java.util.ArrayList;
import java.util.List;

public class LoadedImagesUtils {

    public static ArrayList<String> createStringedImagesInfo(List<LoadedImageInfo> mLoadedImages) {

        ArrayList<String> mStringedImagesInfo = new ArrayList();

        for(LoadedImageInfo info : mLoadedImages){
            mStringedImagesInfo.add(info.toString());
        }
        return mStringedImagesInfo;
    }

    public static ArrayList<LoadedImageInfo> createLoadedInagesInfo(List<String> mStringedImaagesInfo) {

        ArrayList<LoadedImageInfo> mLoadedImages = new ArrayList();

        for(String info: mStringedImaagesInfo){
            mLoadedImages.add(toLoadedImageInfo(info));
        }
        return mLoadedImages;
    }


    public static LoadedImageInfo toLoadedImageInfo(String info) {
        String[] stringedInfo = info.split(" ");
        return new LoadedImageInfo(stringedInfo[0],
                stringedInfo[1],
                stringedInfo[2],
                stringedInfo[3],
                stringedInfo[4],
                stringedInfo[5],
                stringedInfo[6],
                stringedInfo[7]);
    }
}
