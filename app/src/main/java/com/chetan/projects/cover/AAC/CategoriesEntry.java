package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CategoriesEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    String name;
    String largeUrl;
    String mediumUrl;
    String previewUrl;

    @Ignore
    public CategoriesEntry(String name, String largeUrl, String mediumUrl, String previewUrl) {
        this.name = name;
        this.largeUrl = largeUrl;
        this.mediumUrl = mediumUrl;
        this.previewUrl = previewUrl;
    }

    public CategoriesEntry(int id, String name, String largeUrl, String mediumUrl, String previewUrl) {
        this.id = id;
        this.name = name;
        this.largeUrl = largeUrl;
        this.mediumUrl = mediumUrl;
        this.previewUrl = previewUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
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
}
