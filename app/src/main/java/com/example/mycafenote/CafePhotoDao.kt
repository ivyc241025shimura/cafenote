package com.example.mycafenote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CafePhotoDao {

    @Insert
    suspend fun insertPhotos(
        photos: List<CafePhotoEntity>
    )

    @Query("""
        SELECT * FROM cafe_photos
        WHERE cafeId = :cafeId
        ORDER BY displayOrder ASC
    """)
    suspend fun getPhotosByCafeId(
        cafeId: Int
    ): List<CafePhotoEntity>

    @Query("""
        SELECT * FROM cafe_photos
        WHERE cafeId = :cafeId
        ORDER BY displayOrder ASC
        LIMIT 1
    """)
    suspend fun getFirstPhoto(
        cafeId: Int
    ): CafePhotoEntity?

    @Query("""
        SELECT * FROM cafe_photos
        WHERE displayOrder = 0
    """)
    suspend fun getFirstPhotos(): List<CafePhotoEntity>

    @Query("DELETE FROM cafe_photos WHERE cafeId = :cafeId")
    suspend fun deletePhotosByCafeId(
        cafeId: Int
    )
}