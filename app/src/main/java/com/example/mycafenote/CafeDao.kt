package com.example.mycafenote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CafeDao {

    @Insert
    suspend fun insert(cafe: CafeEntity): Long

    @Update
    suspend fun update(cafe: CafeEntity)

    @Query("SELECT * FROM cafes ORDER BY visitYear DESC, visitMonth DESC, visitDay DESC")
    suspend fun getAllCafes(): List<CafeEntity>

    @Query("SELECT * FROM cafes WHERE id = :id")
    suspend fun getCafeById(id: Int): CafeEntity?

    @Query("DELETE FROM cafes WHERE id = :id")
    suspend fun deleteCafeById(id: Int)
}