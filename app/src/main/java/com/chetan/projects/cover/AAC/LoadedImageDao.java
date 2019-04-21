package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface LoadedImageDao {

    @Query("SELECT * FROM LoadedImageEntry ORDER BY id")
    List<LoadedImageEntry> loadAllLoadedImageEntries();

    @Query("SELECT * FROM LoadedImageEntry WHERE  id = :id")
    LoadedImageEntry loadLoadedImageEntry(int id);

    @Query("SELECT COUNT(*) FROM LoadedImageEntry")
    int getLoadedImageCount();

    @Insert
    void insertLoadedImageEntry(LoadedImageEntry loadedImage);

    @Update( onConflict = OnConflictStrategy.ABORT)
    void updateLoadedImageEntry(LoadedImageEntry LoadedImageEntry);

    @Delete
    void deleteLoadedImageEntry(LoadedImageEntry LoadedImageEntry);

    @Query("DELETE FROM LoadedImageEntry")
    void deleteAllLoadedImageEntry();
}
