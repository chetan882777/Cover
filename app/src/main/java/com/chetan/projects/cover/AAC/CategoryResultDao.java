package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CategoryResultDao {

    @Query("SELECT * FROM CategoryResultEntry ORDER BY id")
    List<CategoryResultEntry> loadAllCategoryResultEntries();

    @Query("SELECT * FROM CategoryResultEntry WHERE search_string = :name ORDER BY id")
    CategoryResultEntry loadCategoryResultEntry(String name);

    @Query("DELETE FROM CategoryResultEntry WHERE search_string = :name")
    void deleteCategoryResultEntry(String name);

    @Query("DELETE FROM CategoryResultEntry")
    void deleteAllCategoryResultEntry();

    @Insert
    void insertCategoryResultEntry(CategoryResultEntry categoryResultEntry);

    @Update( onConflict = OnConflictStrategy.ABORT)
    void updateCategoryVersionEntry(CategoryResultEntry categoryResultEntry);

    @Delete
    void deleteCategoryVersionEntry(CategoryResultEntry categoryResultEntry);
}
