package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thumbnail_history")
data class ThumbnailHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val platform: String, // "YouTube" or "Facebook"
    val category: String, // e.g. "Tech", "Gaming", "Lifestyle", etc.
    val title: String, // Custom input text overlay
    val textOverlaySize: Float = 42f, // customizable size
    val textOverlayColor: String = "#FFFF00", // Default vibrant banner Yellow
    val textOutlineColor: String = "#000000", // Default shadow black
    val textOffsetY: Float = 0f, // vertical adjustment
    val textOffsetX: Float = 0f, // horizontal adjustment
    val filterBrightness: Float = 1.0f, // 0.5f to 1.5f
    val filterContrast: Float = 1.0f, // 0.5f to 1.5f
    val filterSaturation: Float = 1.0f, // 0.0f to 2.0f
    val filterBlur: Float = 0f, // 0dp to 20dp blur
    val imagePath: String, // Local storage file path for offline caching
    val timestamp: Long = System.currentTimeMillis()
)
