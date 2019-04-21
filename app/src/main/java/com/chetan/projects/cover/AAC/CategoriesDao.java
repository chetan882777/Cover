package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CategoriesDao {

    @Query("SELECT * FROM CategoriesEntry ORDER BY id")
    List<CategoriesEntry> loadAllCategoryEntries();


    @Insert
    void insertCategoryEntry(CategoriesEntry categoriesEntry);

    @Update
    void updateCategoryEntry(CategoriesEntry categoriesEntry);

    @Delete
    void deleteCategoryEntry(CategoriesEntry categoriesEntry);
}
