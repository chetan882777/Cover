package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CategoryResultEntry {

    @PrimaryKey(autoGenerate = true)
    int id;

    String search_string;
    String search_result_json;
    String upload_date;
    Long version;

    public CategoryResultEntry(int id, String search_string, String search_result_json, String upload_date, Long version) {
        this.id = id;
        this.search_string = search_string;
        this.search_result_json = search_result_json;
        this.upload_date = upload_date;
        this.version = version;
    }

    @Ignore
    public CategoryResultEntry(String search_string, String search_result_json, String upload_date, Long version) {
        this.search_string = search_string;
        this.search_result_json = search_result_json;
        this.upload_date = upload_date;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearch_string() {
        return search_string;
    }

    public void setSearch_string(String search_string) {
        this.search_string = search_string;
    }

    public String getSearch_result_json() {
        return search_result_json;
    }

    public void setSearch_result_json(String search_result_json) {
        this.search_result_json = search_result_json;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
