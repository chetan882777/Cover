package com.chetan.projects.cover.Data;

public class LoadedImageInfo {

    private String largeImageUrl;
    private String mediumImageUrl;
    private String previewUrl;
    private String pageUrl;
    private String userName;
    private String userId;
    private String tags;
    private String loadingType;

    public String getLoadingType() {
        return loadingType;
    }

    public LoadedImageInfo(String largeImageUrl, String mediumImageUrl, String previewUrl,
                           String pageUrl, String userName, String userId, String tags, String loadingType) {
        this.largeImageUrl = largeImageUrl;
        this.mediumImageUrl = mediumImageUrl;
        this.previewUrl = previewUrl;
        this.pageUrl = pageUrl;
        this.userName = userName;
        this.userId = userId;
        this.tags = tags;
        this.loadingType = loadingType;
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

    @Override
    public String toString() {
        return largeImageUrl + " " +
                mediumImageUrl + " " +
                previewUrl + " " +
                pageUrl + " " +
                userName + " " +
                userId + " " +
                tags + " " + loadingType;
    }

}
