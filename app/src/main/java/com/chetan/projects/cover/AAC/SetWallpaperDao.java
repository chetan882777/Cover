package com.chetan.projects.cover.AAC;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface SetWallpaperDao {

    @Query("SELECT * FROM SetWallpaperEntry ORDER BY time DESC")
    List<SetWallpaperEntry> loadAllWallpaperEntries();

    @Insert
    void insertSetWallpaperEntry(SetWallpaperEntry wallpaperEntry);

    @Update( onConflict = OnConflictStrategy.ABORT)
    void updateWallpaperEntry(SetWallpaperEntry setwallpaperEntry);

    @Delete
    void deleteWallpaperEntry(SetWallpaperEntry setwallpaperEntry);
}
