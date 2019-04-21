package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SetWallpaperEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String largeImageUrl;
    private String mediumImageUrl;
    private String previewUrl;
    private String pageUrl;
    private String userName;
    private String userId;
    private String tags;
    private String time;
    private String loadingType;

    public SetWallpaperEntry(int id, String largeImageUrl, String mediumImageUrl, String previewUrl,
                             String pageUrl, String userName, String userId, String tags, String time,
                             String loadingType) {
        this.id = id;
        this.largeImageUrl = largeImageUrl;
        this.mediumImageUrl = mediumImageUrl;
        this.previewUrl = previewUrl;
        this.pageUrl = pageUrl;
        this.userName = userName;
        this.userId = userId;
        this.tags = tags;
        this.time = time;
        this.loadingType = loadingType;
    }

    @Ignore
    public SetWallpaperEntry(String largeImageUrl, String mediumImageUrl, String previewUrl,
                             String pageUrl, String userName, String userId, String tags, String time,
                             String laodingType) {
        this.largeImageUrl = largeImageUrl;
        this.mediumImageUrl = mediumImageUrl;
        this.previewUrl = previewUrl;
        this.pageUrl = pageUrl;
        this.userName = userName;
        this.userId = userId;
        this.tags = tags;
        this.time = time;
        this.loadingType = laodingType;
    }

    public int getId() {
        return id;
    }

    public String getLoadingType() {
        return loadingType;
    }

    public void setLoadingType(String loadingType) {
        this.loadingType = loadingType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
