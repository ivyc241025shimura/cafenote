package com.example.mycafenote

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cafes")
data class CafeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val cafeName: String,
    val address: String,

    val visitYear: Int,
    val visitMonth: Int,
    val visitDay: Int,

    val rating: Int,

    val food: String,
    val memo: String,

    val favorite: Boolean,

    val photo: String? = null
)