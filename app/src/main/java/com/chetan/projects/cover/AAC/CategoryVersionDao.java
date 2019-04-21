package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CategoryVersionDao {

    @Query("SELECT * FROM CategoryVersionEntry ORDER BY id")
    List<CategoryVersionEntry> loadAllCategoryVersionEntries();

    @Query("SELECT * FROM CategoryVersionEntry WHERE name = :name ORDER BY id LIMIT 1")
    CategoryVersionEntry loadCategoryVersionEntry(String name);

    @Query("DELETE FROM CategoryVersionEntry WHERE name = :name")
    void deleteCategoryVersionEntry(String name);

    @Query("DELETE FROM CategoryVersionEntry")
    void deleteAllCategoryVersionEntry();

    @Insert
    void insertCategoryVersionEntry(CategoryVersionEntry categoryVersionEntry);

    @Update( onConflict = OnConflictStrategy.ABORT)
    void updateCategoryVersionEntry(CategoryVersionEntry categoryVersionEntry);

    @Delete
    void deleteCategoryVersionEntry(CategoryVersionEntry categoryVersionEntry);
}
