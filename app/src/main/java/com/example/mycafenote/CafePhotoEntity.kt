package com.example.mycafenote

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cafe_photos",
    foreignKeys = [
        ForeignKey(
            entity = CafeEntity::class,
            parentColumns = ["id"],
            childColumns = ["cafeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cafeId")]
)
data class CafePhotoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val cafeId: Int,

    val uri: String,

    val displayOrder: Int
)