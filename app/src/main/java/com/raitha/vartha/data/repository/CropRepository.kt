package com.raitha.vartha.data.repository

import com.raitha.vartha.data.local.RaithaDao
import com.raitha.vartha.models.Crop
import com.raitha.vartha.models.Tip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CropRepository(private val raithaDao: RaithaDao) {
    val allCrops: Flow<List<Crop>> = raithaDao.getAllCrops()

    fun getCropsByCategory(category: String): Flow<List<Crop>> {
        return raithaDao.getCropsByCategory(category)
    }

    suspend fun insertCrops(crops: List<Crop>) {
        raithaDao.insertCrops(crops)
    }

    suspend fun seedCropsIfEmpty() {
        android.util.Log.d("CropRepository", "Seeding crops and tips...")
        // Clear and re-seed to ensure consistency and correct image mapping
        val crops = getSeededCrops()
        raithaDao.insertCrops(crops)
        android.util.Log.d("CropRepository", "Inserted ${crops.size} crops")
        
        // Automatically generate tip cards for every crop to ensure 100% match
        val tips = crops.map { crop ->
            Tip(
                id = "tip_${crop.id}",
                cropName = crop.name,
                cropNameKn = crop.nameKn,
                tipTitle = "Yield Improvement for ${crop.name}",
                tipTitleKn = "${crop.nameKn} ಬೆಳೆಗೆ ಅಧಿಕ ಇಳುವರಿ ಸಲಹೆ",
                tipDescription = crop.yieldTips.firstOrNull() ?: "Follow modern farming techniques for maximum profit.",
                tipDescriptionKn = crop.yieldTipsKn.firstOrNull() ?: "ಗರಿಷ್ಠ ಲಾಭಕ್ಕಾಗಿ ಆಧುನಿಕ ಕೃಷಿ ತಂತ್ರಗಳನ್ನು ಅನುಸರಿಸಿ.",
                imageUrl = crop.imageName,
                weatherAlert = crop.weatherAdvice,
                weatherAlertKn = crop.weatherAdviceKn,
                aiRecommendation = crop.aiRecommendation,
                aiRecommendationKn = crop.aiRecommendationKn
            )
        }
        raithaDao.insertTips(tips)
        android.util.Log.d("CropRepository", "Inserted ${tips.size} tips")
    }

    private fun getSeededCrops(): List<Crop> {
        return listOf(
            // PULSES
            createCrop("p1", "Red Gram / Tur", "ತೊಗರಿ ಬೇಳೆ", "PULSES", "ಬೇಳೆಕಾಳುಗಳು", "crop_red_gram", "90-120 Days", "Well-drained sandy loam", "Moderate"),
            createCrop("p2", "Green Gram", "ಹೆಸರು ಬೇಳೆ", "PULSES", "ಬೇಳೆಕಾಳುಗಳು", "crop_green_gram", "60-75 Days", "Loamy soil", "Low"),
            createCrop("p3", "Bengal Gram", "ಕಡಲೆ ಬೇಳೆ", "PULSES", "ಬೇಳೆಕಾಳುಗಳು", "crop_bengal_gram", "90-110 Days", "Deep black soil", "Low"),
            createCrop("p4", "Black Gram", "ಉದ್ದಿನ ಬೇಳೆ", "PULSES", "ಬೇಳೆಕಾಳುಗಳು", "crop_black_gram", "70-80 Days", "Heavy clay soil", "Moderate"),

            // PLANTATION
            createCrop("pl1", "Coconut", "ತೆಂಗಿನಕಾಯಿ", "PLANTATION", "ತೋಟಗಾರಿಕೆ", "crop_coconut", "60-80 Years", "Laterite / Sandy", "Regular"),
            createCrop("pl2", "Arecanut", "ಅಡಿಕೆ", "PLANTATION", "ತೋಟಗಾರಿಕೆ", "crop_arecanut", "30-40 Years", "Alluvial / Laterite", "High"),
            createCrop("pl3", "Coffee", "ಕಾಫಿ", "PLANTATION", "ತೋಟಗಾರಿಕೆ", "crop_coffee", "40-50 Years", "Red / Lateritic", "Moderate"),
            createCrop("pl4", "Black Pepper", "ಕಾಳು ಮೆಣಸು", "PLANTATION", "ತೋಟಗಾರಿಕೆ", "crop_pepper", "20-25 Years", "Rich loamy soil", "Regular"),
            createCrop("pl5", "Rubber", "ರಬ್ಬರ್", "PLANTATION", "ತೋಟಗಾರಿಕೆ", "crop_rubber", "25-30 Years", "Deep well-drained", "Moderate"),

            // COMMERCIAL
            createCrop("cm1", "Cotton", "ಹತ್ತಿ", "COMMERCIAL", "ವಾಣಿಜ್ಯ", "crop_cotton", "160-180 Days", "Black Cotton Soil", "Moderate"),
            createCrop("cm2", "Sugarcane", "ಕಬ್ಬು", "COMMERCIAL", "ವಾಣಿಜ್ಯ", "crop_sugarcane", "10-12 Months", "Deep rich loamy", "Very High"),
            createCrop("cm3", "Tobacco", "ತಂಬಾಕು", "COMMERCIAL", "ವಾಣಿಜ್ಯ", "crop_tobacco", "100-120 Days", "Sandy to sandy loam", "Low"),
            createCrop("cm4", "Ginger", "ಶುಂಠಿ", "COMMERCIAL", "ವಾಣಿಜ್ಯ", "crop_ginger", "8-9 Months", "Well-drained sandy", "Moderate"),
            createCrop("cm5", "Turmeric", "ಅರಿಶಿನ", "COMMERCIAL", "ವಾಣಿಜ್ಯ", "crop_turmeric", "7-9 Months", "Sandy or clayey", "Moderate"),

            // FRUITS
            createCrop("f1", "Mango", "ಮಾವು", "FRUITS", "ಹಣ್ಣುಗಳು", "crop_mango", "40-50 Years", "Deep well-drained", "Low"),
            createCrop("f2", "Banana", "ಬಾಳೆಹಣ್ಣು", "FRUITS", "ಹಣ್ಣುಗಳು", "crop_banana", "12-15 Months", "Rich loamy soil", "High"),
            createCrop("f3", "Grapes", "ದ್ರಾಕ್ಷಿ", "FRUITS", "ಹಣ್ಣುಗಳು", "crop_grapes", "15-20 Years", "Sandy loam", "Moderate"),
            createCrop("f4", "Pomegranate", "ದಾಳಿಂಬೆ", "FRUITS", "ಹಣ್ಣುಗಳು", "crop_pomegranate", "20-25 Years", "Well-drained sandy", "Moderate"),
            createCrop("f5", "Papaya", "ಪಪ್ಪಾಯಿ", "FRUITS", "ಹಣ್ಣುಗಳು", "crop_papaya", "2-3 Years", "Well-drained loamy", "Regular"),

            // VEGETABLES
            createCrop("v1", "Tomato", "ಟೊಮೆಟೊ", "VEGETABLES", "ತರಕಾರಿಗಳು", "crop_tomato", "90-110 Days", "Sandy loam", "Moderate"),
            createCrop("v2", "Onion", "ಈರುಳ್ಳಿ", "VEGETABLES", "ತರಕಾರಿಗಳು", "crop_onion", "100-120 Days", "Sandy loam to clayey", "Moderate"),
            createCrop("v3", "Potato", "ಆಲೂಗಡ್ಡೆ", "VEGETABLES", "ತರಕಾರಿಗಳು", "crop_potato", "80-100 Days", "Loamy soil", "Moderate"),
            createCrop("v4", "Brinjal", "ಬದನೆಕಾಯಿ", "VEGETABLES", "ತರಕಾರಿಗಳು", "crop_brinjal", "120-150 Days", "Silt loam / Clay loam", "Regular"),
            createCrop("v5", "Chilli", "ಮೆಣಸಿನಕಾಯಿ", "VEGETABLES", "ತರಕಾರಿಗಳು", "crop_chilli", "150-180 Days", "Black soil / Loamy", "Moderate"),
            createCrop("v6", "Carrot", "ಕ್ಯಾರೆಟ್", "VEGETABLES", "ತರಕಾರಿಗಳು", "crop_carrot", "90-100 Days", "Deep sandy loam", "Regular"),

            // CEREALS
            createCrop("c1", "Paddy", "ಭತ್ತ", "CEREALS", "ಧಾನ್ಯಗಳು", "crop_paddy", "120-150 Days", "Clayey Loam", "High"),
            createCrop("c2", "Wheat", "ಗೋಧಿ", "CEREALS", "ಧಾನ್ಯಗಳು", "crop_wheat", "110-120 Days", "Well-drained loamy", "Moderate"),
            createCrop("c3", "Ragi", "ರಾಗಿ", "CEREALS", "ಧಾನ್ಯಗಳು", "crop_ragi", "100-120 Days", "Red / Loamy soil", "Low"),
            createCrop("c4", "Maize", "ಮೆಕ್ಕೆಜೋಳ", "CEREALS", "ಧಾನ್ಯಗಳು", "crop_maize", "90-110 Days", "Well-drained loamy", "Moderate"),
            createCrop("c5", "Jowar", "ಜೋಳ", "CEREALS", "ಧಾನ್ಯಗಳು", "crop_jowar", "110-120 Days", "Sandy loam", "Low")
        )
    }

    private fun createCrop(
        id: String, name: String, nameKn: String, category: String, categoryKn: String, 
        imageName: String, duration: String, soil: String, water: String
    ): Crop {
        return Crop(
            id = id,
            name = name,
            nameKn = nameKn,
            category = category,
            categoryKn = categoryKn,
            imageName = imageName,
            overview = "$name is an essential crop in the $category category, widely cultivated for its high economic value.",
            overviewKn = "$nameKn ಇದು $categoryKn ವಿಭಾಗದ ಪ್ರಮುಖ ಬೆಳೆಯಾಗಿದ್ದು, ಆರ್ಥಿಕವಾಗಿ ರೈತರಿಗೆ ಬಹಳ ಲಾಭದಾಯಕವಾಗಿದೆ.",
            season = "Kharif/Rabi",
            seasonKn = "ಖಾರಿಫ್/ರಬಿ",
            soilType = soil,
            soilTypeKn = "ಸೂಕ್ತ ಮಣ್ಣು: $soil",
            waterRequirement = water,
            waterRequirementKn = "ನೀರಿನ ಅವಶ್ಯಕತೆ: $water",
            duration = duration,
            durationKn = duration,
            harvestTime = "End of Season",
            harvestTimeKn = "ಋತುವಿನ ಅಂತ್ಯದಲ್ಲಿ",
            investment = "₹25,000 - ₹40,000",
            investmentKn = "₹25,000 - ₹40,000",
            revenue = "₹80,000 - ₹1,20,000",
            revenueKn = "₹80,000 - ₹1,20,000",
            profit = "₹50,000 - ₹80,000",
            profitKn = "₹50,000 - ₹80,000",
            pests = listOf("Stem Borer", "Aphids"),
            pestsKn = listOf("ಕಾಂಡ ಕೊರಕ", "ಸೇರುವೆ"),
            pesticides = listOf("Imidacloprid", "Neem Oil"),
            pesticidesKn = listOf("ಇಮಿಡಾಕ್ಲೋಪ್ರಿಡ್", "ಬೇವಿನ ಎಣ್ಣೆ"),
            diseases = listOf("Root Rot", "Leaf Spot"),
            diseasesKn = listOf("ಬೇರು ಕೊಳೆ ರೋಗ", "ಎಲೆ ಚುಕ್ಕೆ ರೋಗ"),
            fertilizers = listOf("Urea", "DAP", "Potash"),
            fertilizersKn = listOf("ಯೂರಿಯಾ", "ಡಿಎಪಿ", "ಪೊಟ್ಯಾಶ್"),
            yieldTips = listOf("Use certified seeds", "Timely weeding", "Soil testing is mandatory"),
            yieldTipsKn = listOf("ಪ್ರಮಾಣೀಕೃತ ಬೀಜ ಬಳಸಿ", "ಸರಿಯಾದ ಸಮಯದಲ್ಲಿ ಕಳೆ ತೆಗೆಯಿರಿ", "ಮಣ್ಣು ಪರೀಕ್ಷೆ ಅತ್ಯಗತ್ಯ"),
            weatherAdvice = "Avoid heavy irrigation during flowering stage.",
            weatherAdviceKn = "ಹೂಬಿಡುವ ಹಂತದಲ್ಲಿ ಹೆಚ್ಚಿನ ನೀರಾವರಿಯನ್ನು ತಪ್ಪಿಸಿ.",
            aiRecommendation = "Apply balanced NPK based on soil health card reports.",
            aiRecommendationKn = "ಮಣ್ಣಿನ ಆರೋಗ್ಯ ಕಾರ್ಡ್ ವರದಿಯ ಆಧಾರದ ಮೇಲೆ ಸಮತೋಲಿತ NPK ಗೊಬ್ಬರ ಬಳಸಿ."
        )
    }
}
