package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CategoryVersionEntry {

    @PrimaryKey(autoGenerate = true)
    int id;

    String name;
    long version;

    public CategoryVersionEntry(int id, String name, long version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    @Ignore
    public CategoryVersionEntry(String name, long version) {
        this.name = name;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
