package com.raitha.vartha.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tips")
data class Tip(
    @PrimaryKey val id: String = "",
    val cropName: String = "",
    val cropNameKn: String = "",
    val tipTitle: String = "",
    val tipTitleKn: String = "",
    val tipDescription: String = "",
    val tipDescriptionKn: String = "",
    val imageUrl: String = "",
    val weatherAlert: String? = null,
    val weatherAlertKn: String? = null,
    val aiRecommendation: String? = null,
    val aiRecommendationKn: String? = null,
    val date: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false
)
