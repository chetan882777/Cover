package com.chetan.projects.cover.Firebase;

public class Category {
    String name;
    String largeUrl;
    String mediumUrl;
    String previewUrl;

    public String getCategoryName() {
        return name;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    @Override
    public String toString() {
        return name + " \n\n"+ largeUrl + " \n\n" +
                mediumUrl + " \n\n " +
                previewUrl;
    }
}

