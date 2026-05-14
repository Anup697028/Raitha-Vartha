package com.raitha.vartha.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "success_stories")
data class SuccessStory(
    @PrimaryKey val id: String = "",
    val farmerName: String = "",
    val district: String = "",
    val districtKn: String = "",
    val cropType: String = "",
    val cropTypeKn: String = "",
    val story: String = "",
    val storyKn: String = "",
    val imageUrl: String = "",
    val yieldIncrease: String = "",
    // Detailed fields for Story Detail Screen
    val detailedStory: String = "",
    val issueFaced: String = "",
    val solutionApplied: String = "",
    val aiAdvantage: String = ""
) : Serializable
