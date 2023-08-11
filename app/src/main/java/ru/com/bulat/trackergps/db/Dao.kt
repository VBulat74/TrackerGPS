package ru.com.bulat.trackergps.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    suspend fun insertTrack(trackItem: TrackItem)

    @Query("SELECT * FROM tracks")
    fun getAllTracks() :Flow<List<TrackItem>>
}