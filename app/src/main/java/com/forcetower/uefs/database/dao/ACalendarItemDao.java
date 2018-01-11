package com.forcetower.uefs.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.database.entities.ACalendarItem;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Dao
public interface ACalendarItemDao {
    @Query("SELECT * FROM ACalendarItem")
    LiveData<List<ACalendarItem>> getCalendar();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(ACalendarItem... items);
    @Delete
    void deleteItem(ACalendarItem item);
    @Query("DELETE FROM ACalendarItem")
    void deleteCalendar();
}