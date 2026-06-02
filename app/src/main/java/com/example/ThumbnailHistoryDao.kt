package com.example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailHistoryDao {
    @Query("SELECT * FROM thumbnail_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ThumbnailHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ThumbnailHistory): Long

    @Delete
    suspend fun delete(history: ThumbnailHistory)

    @Query("DELETE FROM thumbnail_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM thumbnail_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ThumbnailHistory?
}
