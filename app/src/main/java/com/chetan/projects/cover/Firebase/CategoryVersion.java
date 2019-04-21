package com.chetan.projects.cover.Firebase;

public class CategoryVersion {
    String name;
    long version;

    public String getName() {
        return name;
    }

    public long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return name + " " + version;
    }
}