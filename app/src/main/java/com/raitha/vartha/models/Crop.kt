package com.raitha.vartha.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "crops")
data class Crop(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val nameKn: String = "",
    val category: String = "",
    val categoryKn: String = "",
    val imageName: String = "", // Used for local drawable mapping
    val overview: String = "",
    val overviewKn: String = "",
    val season: String = "",
    val seasonKn: String = "",
    val soilType: String = "",
    val soilTypeKn: String = "",
    val climate: String = "",
    val climateKn: String = "",
    val waterRequirement: String = "",
    val waterRequirementKn: String = "",
    val duration: String = "",
    val durationKn: String = "",
    val harvestTime: String = "",
    val harvestTimeKn: String = "",
    val investment: String = "",
    val investmentKn: String = "",
    val revenue: String = "",
    val revenueKn: String = "",
    val profit: String = "",
    val profitKn: String = "",
    val pests: List<String> = emptyList(),
    val pestsKn: List<String> = emptyList(),
    val pesticides: List<String> = emptyList(),
    val pesticidesKn: List<String> = emptyList(),
    val diseases: List<String> = emptyList(),
    val diseasesKn: List<String> = emptyList(),
    val fertilizers: List<String> = emptyList(),
    val fertilizersKn: List<String> = emptyList(),
    val yieldTips: List<String> = emptyList(),
    val yieldTipsKn: List<String> = emptyList(),
    val weatherAdvice: String = "",
    val weatherAdviceKn: String = "",
    val irrigationMethods: List<String> = emptyList(),
    val irrigationMethodsKn: List<String> = emptyList(),
    val marketDemand: String = "",
    val marketDemandKn: String = "",
    val harvestingTechniques: String = "",
    val harvestingTechniquesKn: String = "",
    val aiRecommendation: String = "",
    val aiRecommendationKn: String = ""
) : Parcelable
