package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class LoadedImageEntry {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private String largeImageUrl;
    private String mediumImageUrl;
    private String previewUrl;
    private String pageUrl;
    private String userName;
    private String userId;
    private String tags;


    public LoadedImageEntry(int id, String largeImageUrl, String mediumImageUrl, String previewUrl,
                            String pageUrl, String userName, String userId, String tags) {
        this.id = id;
        this.largeImageUrl = largeImageUrl;
        this.mediumImageUrl = mediumImageUrl;
        this.previewUrl = previewUrl;
        this.pageUrl = pageUrl;
        this.userName = userName;
        this.userId = userId;
        this.tags = tags;
    }


    @Ignore
    public LoadedImageEntry(String largeImageUrl, String mediumImageUrl, String previewUrl,
                            String pageUrl, String userName, String userId, String tags) {
        this.largeImageUrl = largeImageUrl;
        this.mediumImageUrl = mediumImageUrl;
        this.previewUrl = previewUrl;
        this.pageUrl = pageUrl;
        this.userName = userName;
        this.userId = userId;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getTags() {
        return tags;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
