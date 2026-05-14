package com.raitha.vartha.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.raitha.vartha.data.local.RaithaDao
import com.raitha.vartha.models.Tip
import com.raitha.vartha.models.Crop
import com.raitha.vartha.models.SuccessStory
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onEach

class FirebaseRepository(private val raithaDao: RaithaDao) {
    private val firestore = FirebaseFirestore.getInstance()

    fun getDailyTips(): Flow<List<Tip>> = callbackFlow {
        val subscription = firestore.collection("tips")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error fetching tips", error)
                    close(error)
                    return@addSnapshotListener
                }
                val tips = snapshot?.toObjects(Tip::class.java) ?: emptyList()
                Log.d("FirebaseRepository", "Fetched ${tips.size} tips from Firestore")
                trySend(tips)
            }
        awaitClose { subscription.remove() }
    }.onEach { tips ->
        if (tips.isNotEmpty()) {
            raithaDao.insertTips(tips)
        }
    }

    fun getCrops(): Flow<List<Crop>> = callbackFlow {
        val subscription = firestore.collection("crops")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error fetching crops", error)
                    close(error)
                    return@addSnapshotListener
                }
                val crops = snapshot?.toObjects(Crop::class.java) ?: emptyList()
                Log.d("FirebaseRepository", "Fetched ${crops.size} crops from Firestore")
                trySend(crops)
            }
        awaitClose { subscription.remove() }
    }.onEach { crops ->
        if (crops.isNotEmpty()) {
            raithaDao.insertCrops(crops)
        }
    }

    fun getSuccessStories(): Flow<List<SuccessStory>> = callbackFlow {
        val subscription = firestore.collection("success_stories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error fetching stories", error)
                    close(error)
                    return@addSnapshotListener
                }
                val stories = snapshot?.toObjects(SuccessStory::class.java) ?: emptyList()
                Log.d("FirebaseRepository", "Fetched ${stories.size} stories from Firestore")
                trySend(stories)
            }
        awaitClose { subscription.remove() }
    }.onEach { stories ->
        if (stories.isNotEmpty()) {
            raithaDao.insertSuccessStories(stories)
        }
    }

    suspend fun seedTipsIfEmpty() {
        android.util.Log.d("FirebaseRepository", "Forcing fresh data seed...")
        
        // Seed initial tips if none exist
        if (raithaDao.getTipsCount() == 0) {
            raithaDao.insertTips(getInitialTips())
        }
        
        // ALWAYS clear and re-seed stories for now to ensure they appear
        raithaDao.deleteAllSuccessStories()
        val stories = getInitialStories()
        raithaDao.insertSuccessStories(stories)
        android.util.Log.d("FirebaseRepository", "Force-inserted ${stories.size} success stories")
    }

    private fun getInitialTips(): List<Tip> {
        return listOf(
            // CEREALS
            Tip("t1", "Paddy", "ಭತ್ತ", "Nursery Care", "ನರ್ಸರಿ ಆರೈಕೆ", "Apply 1kg Urea and 1kg Super Phosphate per cent of nursery area.", "ನರ್ಸರಿ ಪ್ರದೇಶದ ಪ್ರತಿ ಸೆಂಟಿಗೆ 1 ಕೆಜಿ ಯೂರಿಯಾ ಮತ್ತು 1 ಕೆಜಿ ಸೂಪರ್ ಫಾಸ್ಫೇಟ್ ಅನ್ನು ಅನ್ವಯಿಸಿ.", "https://images.unsplash.com/photo-1536630596251-b01c62559263", "Heavy rain. Ensure drainage.", "ಭಾರಿ ಮಳೆ. ಒಳಚರಂಡಿ ಖಚಿತಪಡಿಸಿಕೊಳ್ಳಿ.", "Monitor for Blast disease.", "ಬ್ಲಾಸ್ಟ್ ರೋಗವನ್ನು ಗಮನಿಸಿ."),
            Tip("t2", "Wheat", "ಗೋಧಿ", "Irrigation", "ನೀರಾವರಿ", "Critical stage for irrigation is Crown Root Initiation (21 days after sowing).", "ನೀರಾವರಿಗೆ ನಿರ್ಣಾಯಕ ಹಂತವೆಂದರೆ ಕ್ರೌನ್ ರೂಟ್ ಇನಿಶಿಯೇಶನ್ (ಬಿತ್ತನೆಯ 21 ದಿನಗಳ ನಂತರ).", "https://images.unsplash.com/photo-1501430654243-c936cc5e1d73", "Dry weather. Increase water.", "ಒಣ ಹವೆ. ನೀರು ಹೆಚ್ಚಿಸಿ.", "Watch for Rust fungus."),
            Tip("t3", "Ragi", "ರಾಗಿ", "Nutrients", "ಪೋಷಕಾಂಶಗಳು", "Apply 50:40:25 kg NPK per hectare for rainfed ragi.", "ಮಳೆ ಆಶ್ರಿತ ರಾಗಿಗೆ ಪ್ರತಿ ಹೆಕ್ಟೇರಿಗೆ 50:40:25 ಕೆಜಿ NPK ಅನ್ವಯಿಸಿ.", "https://images.unsplash.com/photo-1596568910080-60b54024343d", "Cloudy. Avoid urea.", "ಮೋಡ ಕವಿದ ವಾತಾವರಣ. ಯೂರಿಯಾ ತಪ್ಪಿಸಿ."),
            Tip("t4", "Maize", "ಮೆಕ್ಕೆಜೋಳ", "Pest Control", "ಕೀಟ ನಿಯಂತ್ರಣ", "Use pheromone traps for Fall Armyworm management.", "ಫಾಲ್ ಆರ್ಮಿ ವರ್ಮ್ ನಿರ್ವಹಣೆಗೆ ಫೆರೋಮೋನ್ ಬಲೆಗಳನ್ನು ಬಳಸಿ.", "https://images.unsplash.com/photo-1470114755716-4d271970b86a", "Normal.", "ಸಾಮಾನ್ಯ.", "Check whorls for larvae."),
            Tip("t5", "Bajra", "ಸಜ್ಜೆ", "Seed Treatment", "ಬೀಜೋಪಚಾರ", "Treat seeds with Azospirillum to fix atmospheric nitrogen.", "ವಾತಾವರಣದ ಸಾರಜನಕವನ್ನು ಸ್ಥಿರಗೊಳಿಸಲು ಅಜೋಸ್ಪೈರಿಲಮ್‌ನೊಂದಿಗೆ ಬೀಜೋಪಚಾರ ಮಾಡಿ.", "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09"),
            Tip("t6", "Jowar", "ಜೋಳ", "Sowing", "ಬಿತ್ತನೆ", "Optimum sowing time for Rabi Jowar is September to October.", "ರಬಿ ಜೋಳದ ಬಿತ್ತನೆಗೆ ಸೂಕ್ತ ಸಮಯ ಸೆಪ್ಟೆಂಬರ್‌ನಿಂದ ಅಕ್ಟೋಬರ್."),
            
            // PULSES
            Tip("t7", "Green Gram", "ಹೆಸರು ಬೇಳೆ", "Yellow Mosaic", "ಹಳದಿ ಮೊಸಾಯಿಕ್", "Remove infected plants immediately to prevent spread of virus.", "ವೈರಸ್ ಹರಡುವುದನ್ನು ತಡೆಯಲು ಸೋಂಕಿತ ಗಿಡಗಳನ್ನು ತಕ್ಷಣ ತೆಗೆದುಹಾಕಿ.", "https://images.unsplash.com/photo-1585994192701-f1661bc162e0"),
            Tip("t8", "Black Gram", "ಉದ್ದಿನ ಬೇಳೆ", "Flower Drop", "ಹೂವು ಉದುರುವುದು", "Spray NAA 40ppm to reduce flower and pod shedding.", "ಹೂವು ಮತ್ತು ಕಾಯಿ ಉದುರುವುದನ್ನು ಕಡಿಮೆ ಮಾಡಲು NAA 40ppm ಸಿಂಪಡಿಸಿ."),
            Tip("t9", "Bengal Gram", "ಕಡಲೆ ಬೇಳೆ", "Wilt Management", "ಬಾಡು ರೋಗ ನಿರ್ವಹಣೆ", "Deep summer ploughing helps in reducing soil-borne pathogens.", "ಬೇಸಿಗೆಯ ಆಳವಾದ ಉಳುಮೆಯು ಮಣ್ಣಿನಿಂದ ಹರಡುವ ರೋಗಕಾರಕಗಳನ್ನು ಕಡಿಮೆ ಮಾಡಲು ಸಹಾಯ ಮಾಡುತ್ತದೆ."),
            Tip("t10", "Red Gram", "ತೊಗರಿ ಬೇಳೆ", "Spacing", "ಅಂತರ", "Follow 60x20 cm spacing for short duration varieties.", "ಅಲ್ಪಾವಧಿಯ ತಳಿಗಳಿಗೆ 60x20 ಸೆಂ.ಮೀ ಅಂತರವನ್ನು ಅನುಸರಿಸಿ."),

            // VEGETABLES
            Tip("t11", "Tomato", "ಟೊಮೆಟೊ", "Staking", "ಆಧಾರ ನೀಡುವುದು", "Provide stakes to prevent fruit rot and soil contact.", "ಹಣ್ಣು ಕೊಳೆಯುವುದನ್ನು ಮತ್ತು ಮಣ್ಣಿನ ಸಂಪರ್ಕವನ್ನು ತಡೆಯಲು ಆಧಾರ ನೀಡಿ.", "https://images.unsplash.com/photo-1546473427-e1bc63ac3773"),
            Tip("t12", "Onion", "ಈರುಳ್ಳಿ", "Storage", "ಸಂಗ್ರಹಣೆ", "Dry bulbs in shade for 3-4 days before storage to increase shelf life.", "ಶೆಲ್ಫ್ ಅವಧಿಯನ್ನು ಹೆಚ್ಚಿಸಲು ಸಂಗ್ರಹಿಸುವ ಮೊದಲು 3-4 ದಿನಗಳ ಕಾಲ ನೆರಳಿನಲ್ಲಿ ಈರುಳ್ಳಿಯನ್ನು ಒಣಗಿಸಿ.", "https://images.unsplash.com/photo-1508747703725-719777637510"),
            Tip("t13", "Potato", "ಆಲೂಗಡ್ಡೆ", "Earthing Up", "ಮಣ್ಣು ಏರಿಸುವುದು", "Carry out earthing up 30 days after planting for better tuber growth.", "ಉತ್ತಮ ಗೆಡ್ಡೆಗಳ ಬೆಳವಣಿಗೆಗಾಗಿ ನಾಟಿ ಮಾಡಿದ 30 ದಿನಗಳ ನಂತರ ಮಣ್ಣು ಏರಿಸಿ.", "https://images.unsplash.com/photo-1518977676601-b53f02ac6d31"),
            Tip("t14", "Brinjal", "ಬದನೆಕಾಯಿ", "Shoot Borer", "ಕುಡಿ ಕೊರಕ", "Clip and destroy infected shoots regularly.", "ಸೋಂಕಿತ ಕುಡಿಗಳನ್ನು ನಿಯಮಿತವಾಗಿ ಕತ್ತರಿಸಿ ನಾಶಪಡಿಸಿ."),
            Tip("t15", "Chilli", "ಮೆಣಸಿನಕಾಯಿ", "Leaf Curl", "ಎಲೆ ಮುದುರು ರೋಗ", "Control thrips using Imidacloprid to manage leaf curl virus.", "ಎಲೆ ಮುದುರು ವೈರಸ್ ನಿರ್ವಹಿಸಲು ಇಮಿಡಾಕ್ಲೋಪ್ರಿಡ್ ಬಳಸಿ ಥ್ರಿಪ್ಸ್ ನಿಯಂತ್ರಿಸಿ."),
            Tip("t16", "Carrot", "ಗಜ್ಜರಿ", "Thinning", "ತೆಳುಗೊಳಿಸುವಿಕೆ", "Thin out plants to 5cm distance for uniform root size.", "ಏಕರೂಪದ ಗಾತ್ರದ ಬೇರುಗಳಿಗಾಗಿ ಗಿಡಗಳನ್ನು 5 ಸೆಂ.ಮೀ ಅಂತರಕ್ಕೆ ತೆಳುಗೊಳಿಸಿ."),
            Tip("t17", "Beans", "ಹುರುಳಿಕಾಯಿ", "Mulching", "ಹೊದಿಕೆ", "Apply organic mulch to retain moisture during pod formation.", "ಕಾಯಿ ಕಟ್ಟುವ ಹಂತದಲ್ಲಿ ತೇವಾಂಶವನ್ನು ಉಳಿಸಿಕೊಳ್ಳಲು ಸಾವಯವ ಹೊದಿಕೆಯನ್ನು ಅನ್ವಯಿಸಿ."),

            // FRUITS
            Tip("t18", "Mango", "ಮಾವಿನ ಹಣ್ಣು", "Hopper Control", "ಜಿಗಿ ಕೀಟ ನಿಯಂತ್ರಣ", "Spray Carbaryl during flowering to control mango hoppers.", "ಮಾವಿನ ಜಿಗಿ ಕೀಟಗಳನ್ನು ನಿಯಂತ್ರಿಸಲು ಹೂಬಿಡುವ ಹಂತದಲ್ಲಿ ಕಾರ್ಬರಿಲ್ ಸಿಂಪಡಿಸಿ.", "https://images.unsplash.com/photo-1553279768-865429fa0078"),
            Tip("t19", "Banana", "ಬಾಳೆಹಣ್ಣು", "Desuckering", "ಪಿಳ್ಳೆಗಳನ್ನು ತೆಗೆಯುವುದು", "Remove unwanted suckers once in 45 days for better bunch weight.", "ಗೊನೆಯ ತೂಕ ಹೆಚ್ಚಿಸಲು 45 ದಿನಗಳಿಗೊಮ್ಮೆ ಬೇಡದ ಪಿಳ್ಳೆಗಳನ್ನು ತೆಗೆದುಹಾಕಿ.", "https://images.unsplash.com/photo-1571771894821-ad9b5886464a"),
            Tip("t20", "Papaya", "ಪಪ್ಪಾಯಿ", "Ring Spot", "ರಿಂಗ್ ಸ್ಪಾಟ್", "Uproot and burn virus-affected plants to prevent spread.", "ಹರಡುವಿಕೆಯನ್ನು ತಡೆಯಲು ವೈರಸ್ ಪೀಡಿತ ಗಿಡಗಳನ್ನು ಕಿತ್ತು ಸುಟ್ಟುಹಾಕಿ."),
            Tip("t21", "Watermelon", "ಕಲ್ಲಂಗಡಿ", "Fruit Setting", "ಹಣ್ಣು ಕಟ್ಟುವುದು", "Place honeybee boxes to improve pollination and fruit set.", "ಪರಾಗಸ್ಪರ್ಶ ಮತ್ತು ಹಣ್ಣು ಕಟ್ಟುವುದನ್ನು ಸುಧಾರಿಸಲು ಜೇನುನೊಣದ ಪೆಟ್ಟಿಗೆಗಳನ್ನು ಇರಿಸಿ."),
            Tip("t22", "Guava", "ಸೀಬೆಹಣ್ಣು", "Pruning", "ಸವರುವಿಕೆ", "Prune current season's growth to induce fresh flowering.", "ಹೊಸ ಹೂವುಗಳನ್ನು ಉತ್ತೇಜಿಸಲು ಈ ಹಂತದ ಬೆಳವಣಿಗೆಯನ್ನು ಸವರಿ."),
            Tip("t23", "Grapes", "ದ್ರಾಕ್ಷಿ", "Downy Mildew", "ಡೌನಿ ಮಿಲ್ಡ್ಯೂ", "Spray Bordeaux mixture periodically to prevent fungal attacks.", "ಶಿಲೀಂಧ್ರಗಳ ದಾಳಿಯನ್ನು ತಡೆಯಲು ನಿಯಮಿತವಾಗಿ ಬೋರ್ಡೋ ಮಿಶ್ರಣವನ್ನು ಸಿಂಪಡಿಸಿ."),

            // PLANTATION
            Tip("t24", "Coconut", "ತೆಂಗಿನಕಾಯಿ", "Red Palm Weevil", "ಕೆಂಪು ಮೂತಿ ಹುಳು", "Use Pheromone traps to catch adult weevils effectively.", "ವಯಸ್ಕ ಹುಳುಗಳನ್ನು ಪರಿಣಾಮಕಾರಿಯಾಗಿ ಹಿಡಿಯಲು ಫೆರೋಮೋನ್ ಬಲೆಗಳನ್ನು ಬಳಸಿ.", "https://images.unsplash.com/photo-1584346133934-a3afd2a33c4c"),
            Tip("t25", "Areca Nut", "ಅಡಿಕೆ", "Koleroga", "ಕೊಳೆರೋಗ", "Spray 1% Bordeaux mixture before monsoon starts.", "ಮುಂಗಾರು ಆರಂಭವಾಗುವ ಮೊದಲು 1% ಬೋರ್ಡೋ ಮಿಶ್ರಣವನ್ನು ಸಿಂಪಡಿಸಿ."),
            Tip("t26", "Coffee", "ಕಾಫಿ", "Berry Borer", "ಬೆರ್ರಿ ಬೋರರ್", "Use broca traps and picking mats during harvest.", "ಕೊಯ್ಲಿನ ಸಮಯದಲ್ಲಿ ಬ್ರೋಕಾ ಬಲೆಗಳು ಮತ್ತು ಹಣ್ಣು ಆರಿಸುವ ಚಾಪೆಗಳನ್ನು ಬಳಸಿ."),
            Tip("t27", "Tea", "ಚಹಾ", "Plucking", "ಕುಯ್ಲು", "Standard plucking interval is 7-10 days during peak season.", "ಗರಿಷ್ಠ ಋತುವಿನಲ್ಲಿ ಕುಯ್ಲಿನ ಅಂತರವು 7-10 ದಿನಗಳು."),

            // COMMERCIAL
            Tip("t28", "Sugarcane", "ಕಬ್ಬು", "Detrashing", "ಗೆಣ್ಣು ಸುಲಿಯುವುದು", "Remove dry leaves at 5th and 7th month to avoid pests.", "ಕೀಟಗಳನ್ನು ತಪ್ಪಿಸಲು 5 ಮತ್ತು 7 ನೇ ತಿಂಗಳಲ್ಲಿ ಒಣಗಿದ ಎಲೆಗಳನ್ನು ತೆಗೆದುಹಾಕಿ.", "https://images.unsplash.com/photo-1596044193941-2771077f84a5"),
            Tip("t29", "Cotton", "ಹತ್ತಿ", "Bollworm", "ಕಾಯಿ ಕೊರಕ", "Spray Spinosad for managing Pink Bollworm infestation.", "ಗುಲಾಬಿ ಕಾಯಿ ಕೊರಕ ನಿರ್ವಹಣೆಗಾಗಿ ಸ್ಪಿನೋಸಾದ್ ಸಿಂಪಡಿಸಿ."),
            Tip("t30", "Groundnut", "ನೆಲಗಡಲೆ", "Peg Formation", "ಗೂಟ ಕಟ್ಟುವ ಹಂತ", "Do not disturb soil during pegging stage (45 days).", "ಗೂಟ ಕಟ್ಟುವ ಹಂತದಲ್ಲಿ (45 ದಿನಗಳು) ಮಣ್ಣನ್ನು ಕದಲಿಸಬೇಡಿ."),
            Tip("t31", "Sunflower", "ಸೂರ್ಯಕಾಂತಿ", "Hand Pollination", "ಕೈ ಪರಾಗಸ್ಪರ್ಶ", "Rub the heads with palm or cloth to improve seed setting.", "ಬೀಜ ಕಟ್ಟುವುದನ್ನು ಸುಧಾರಿಸಲು ಸೂರ್ಯಕಾಂತಿ ಹೂವಿನ ಮೇಲೆ ಹಸ್ತ ಅಥವಾ ಬಟ್ಟೆಯಿಂದ ಉಜ್ಜಿ.")
        )
    }

    private fun getInitialStories(): List<SuccessStory> {
        return listOf(
            SuccessStory(
                id = "s1",
                farmerName = "Mallesh",
                district = "Mandya",
                districtKn = "ಮಂಡ್ಯ",
                cropType = "Paddy",
                cropTypeKn = "ಭತ್ತ",
                story = "Used SR-26 variety and saved 30% water with drip irrigation.",
                storyKn = "SR-26 ತಳಿಯನ್ನು ಬಳಸಿದೆ ಮತ್ತು ಹನಿ ನೀರಾವರಿಯೊಂದಿಗೆ 30% ನೀರನ್ನು ಉಳಿಸಿದೆ.",
                imageUrl = "crop_paddy",
                yieldIncrease = "25% increase",
                issueFaced = "Severe water scarcity and low yield.",
                solutionApplied = "SR-26 variety and drip irrigation.",
                aiAdvantage = "Real-time moisture alerts from AI.",
                detailedStory = "Mallesh, a dedicated farmer from Mandya, faced a daunting challenge when his region was hit by a severe drought. His traditional paddy cultivation was on the brink of failure as the water table dropped significantly.\n\nDetermined to find a solution, Mallesh started researching modern agricultural techniques and discovered the SR-26 paddy variety. This variety is known for its resilience and lower water requirements. He also decided to invest in a drip irrigation system.\n\nBy following the precision scheduling provided by the Raitha-Varta AI assistant, he was able to manage his limited water resources effectively. The AI provided specific timings for irrigation and the exact amount of fertilizer needed.\n\nThe results were beyond his expectations. Not only did he save over 30% of water, but his yield also increased by 25%. His success has inspired other farmers in Mandya to adopt sustainable practices.\n\nMallesh now feels more confident about the future of farming. He plans to expand his drip irrigation system to his other plots and continues to rely on AI-driven insights.\n\nToday, Mallesh's farm is a model for sustainable paddy cultivation. His journey from the brink of failure to success is a testament to the power of innovation in agriculture."
            ),
            SuccessStory(
                id = "s2",
                farmerName = "Suresh",
                district = "Kolar",
                districtKn = "ಕೋಲಾರ",
                cropType = "Tomato",
                cropTypeKn = "ಟೊಮೆಟೊ",
                story = "Managed Early Blight using organic fungicides.",
                storyKn = "ಸಾವಯವ ಶಿಲೀಂಧ್ರನಾಶಕಗಳನ್ನು ಬಳಸಿ ಅರ್ಲಿ ಬ್ಲೈಟ್ ಅನ್ನು ನಿರ್ವಹಿಸಿದೆ.",
                imageUrl = "crop_tomato",
                yieldIncrease = "Recovered 90% crop",
                issueFaced = "Early Blight disease threatened to destroy the entire tomato harvest.",
                solutionApplied = "Applied organic fungicides and improved spacing.",
                aiAdvantage = "Gemini AI identified the disease from a photo.",
                detailedStory = "Suresh, a hardworking farmer in Kolar, noticed dark spots spreading rapidly across his tomato leaves. Within days, the healthy green field began to look wilted and diseased.\n\nDesperate to save his investment, he used the Raitha-Varta 'Ask Expert' feature. He uploaded a high-resolution photo of the infected leaves. The Gemini-powered AI immediately identified the issue as Early Blight.\n\nInstead of general chemicals, the AI suggested a targeted organic fungicide treatment and advised Suresh to prune the lower leaves to improve airflow. Suresh followed the advice meticulously.\n\nThe spread of the disease stopped within four days. The plants began to show new, healthy growth, and Suresh was able to recover nearly 90% of his expected harvest.\n\nThis was a turning point for him, as he realized that technology could provide precise, eco-friendly solutions. Suresh's tomatoes were eventually sold at a premium price.\n\nHe has now become an advocate for organic disease management in his village. He continues to use the app to monitor his crops and stay ahead of potential pest attacks."
            ),
            SuccessStory(
                id = "s3",
                farmerName = "Ramappa",
                district = "Tumakuru",
                districtKn = "ತುಮಕೂರು",
                cropType = "Coconut",
                cropTypeKn = "ತೆಂಗಿನಕಾಯಿ",
                story = "Integrated pest management saved my old coconut grove.",
                storyKn = "ಸಮಗ್ರ ಕೀಟ ನಿರ್ವಹಣೆಯು ನನ್ನ ಹಳೆಯ ತೆಂಗಿನ ತೋಟವನ್ನು ಉಳಿಸಿತು.",
                imageUrl = "crop_coconut",
                yieldIncrease = "Yield doubled",
                issueFaced = "Infestation of Red Palm Weevil was killing 30-year-old trees.",
                solutionApplied = "Implemented pheromone traps and botanical oil injections.",
                aiAdvantage = "AI analysis helped in identifying specific pest cycles.",
                detailedStory = "Ramappa from Tumakuru inherited a 30-year-old coconut grove that was the primary source of income for his family. However, he noticed several trees dying from the top.\n\nUpon inspection, he found deep holes and chewed fiber, signs of a lethal Red Palm Weevil infestation. Ramappa turned to Raitha-Varta for a more modern approach.\n\nFollowing the AI-generated plan, he installed pheromone traps and began using botanical oil injections for the heavily infested trees. He also cleared the breeding grounds for the Rhinoceros Beetle.\n\nThe app's tracking feature helped him maintain a consistent schedule for trap maintenance. Slowly but surely, the health of the grove began to return.\n\nWithin a year, not only did the tree deaths stop, but the overall yield of the grove doubled. The nuts were larger and healthier than they had been in years.\n\nRamappa’s success story spread across the district, highlighting that even old plantations can be revitalized with the right knowledge and tools."
            ),
            SuccessStory(
                id = "s4",
                farmerName = "Gowramma",
                district = "Haveri",
                districtKn = "ಹಾವೇರಿ",
                cropType = "Ragi",
                cropTypeKn = "ರಾಗಿ",
                story = "Zero budget natural farming helped me grow healthy ragi.",
                storyKn = "ಶೂನ್ಯ ಬಂಡವಾಳ ನೈಸರ್ಗಿಕ ಕೃಷಿಯು ನನಗೆ ಆರೋಗ್ಯಕರ ರಾಗಿ ಬೆಳೆಯಲು ಸಹಾಯ ಮಾಡಿತು.",
                imageUrl = "crop_ragi",
                yieldIncrease = "Profit up 40%",
                issueFaced = "Rising costs of chemical fertilizers and pesticides.",
                solutionApplied = "Adopted ZBNF techniques using Jeevamrutha.",
                aiAdvantage = "App provided detailed recipes for natural inputs.",
                detailedStory = "Gowramma, a small-scale farmer in Haveri, was struggling with the economics of ragi cultivation. Despite a decent harvest, high input costs left her with very little profit.\n\nLooking for a way out, she started using Raitha-Varta to guide her implementation of natural farming. The app provided step-by-step instructions on how to prepare 'Jeevamrutha'.\n\nGowramma completely stopped buying expensive chemicals. Initially, her neighbors were skeptical, but she stayed the course, using the app's AI to troubleshoot soil health issues.\n\nBy the end of the first season, Gowramma noticed a remarkable change. Her soil was softer, and the ragi crop was robust and deep green. Her overall profit increased by 40%.\n\nThe quality of her natural ragi also attracted health-conscious buyers. Her journey has empowered many women in her self-help group to reconsider their farming methods.\n\nGowramma is now a local expert on natural farming. She believes that returning to nature, supported by modern technology, is the best path forward."
            ),
            SuccessStory(
                id = "s5",
                farmerName = "Naveen",
                district = "Belagavi",
                districtKn = "ಬೆಳಗಾವಿ",
                cropType = "Sugarcane",
                cropTypeKn = "ಕಬ್ಬು",
                story = "Increased sugar content by optimized harvesting time.",
                storyKn = "ಕೊಯ್ಲಿನ ಸಮಯವನ್ನು ಉತ್ತಮಗೊಳಿಸುವ ಮೂಲಕ ಸಕ್ಕರೆ ಅಂಶವನ್ನು ಹೆಚ್ಚಿಸಿದೆವು.",
                imageUrl = "crop_sugarcane",
                yieldIncrease = "15% more profit",
                issueFaced = "Low sugar recovery and fluctuating market prices.",
                solutionApplied = "Used AI to determine the exact peak maturity for harvest.",
                aiAdvantage = "Harvesting window predictions based on satellite data.",
                detailedStory = "Naveen, a sugarcane farmer from Belagavi, often struggled with low sugar recovery rates. He used to harvest based on general local trends, which wasn't always accurate.\n\nHe started using Raitha-Varta to monitor his crop's growth. The app integrated satellite data to track the maturity of his sugarcane blocks individually.\n\nWhen the AI expert notified him of the peak sugar content window, Naveen organized his labor for an immediate harvest. This was two weeks earlier than his usual schedule.\n\nThe sugar mill reported a significantly higher recovery rate for his crop. This translated directly into a 15% increase in his final payout.\n\nNaveen also used the app to plan his next planting cycle, choosing varieties recommended for his soil type. He now uses the market price tracker to decide when to sell.\n\nHis data-driven approach has made him one of the most successful sugarcane farmers in his cooperative society."
            ),
            SuccessStory(
                id = "s6",
                farmerName = "Anitha",
                district = "Shimoga",
                districtKn = "ಶಿವಮೊಗ್ಗ",
                cropType = "Arecanut",
                cropTypeKn = "ಅಡಿಕೆ",
                story = "Multi-cropping with pepper doubled my income.",
                storyKn = "ಮೆಣಸಿನೊಂದಿಗೆ ಮಿಶ್ರ ಬೆಳೆ ಬೆಳೆದು ನನ್ನ ಆದಾಯವನ್ನು ದ್ವಿಗುಣಗೊಳಿಸಿದೆ.",
                imageUrl = "crop_arecanut",
                yieldIncrease = "100% income growth",
                issueFaced = "Dependence on a single crop made income unstable.",
                solutionApplied = "Intercropped arecanut with black pepper vines.",
                aiAdvantage = "Nutrient management plan for multiple crops.",
                detailedStory = "Anitha from Shimoga had a traditional arecanut plantation. While it provided a steady income, she wanted to maximize the potential of her land.\n\nThrough Raitha-Varta, she learned about the benefits of multi-cropping. The AI suggested black pepper as a perfect companion for her arecanut trees.\n\nThe app provided a comprehensive nutrient management plan that catered to both crops simultaneously. It helped her manage the additional fertilizer requirements efficiently.\n\nWithin three years, the pepper vines began to yield significantly. Now, her income from pepper almost matches her income from arecanut, effectively doubling her total earnings.\n\nAnitha's farm is now a vibrant ecosystem. She has also added a few beehives as suggested by the app to improve pollination.\n\nShe encourages other women in her community to look at their farms as multi-layered assets rather than just single-crop fields."
            ),
            SuccessStory(
                id = "s7",
                farmerName = "Basavaraj",
                district = "Gadag",
                districtKn = "ಗದಗ",
                cropType = "Cotton",
                cropTypeKn = "ಹತ್ತಿ",
                story = "Saved crop from Pink Bollworm using AI alerts.",
                storyKn = "AI ಎಚ್ಚರಿಕೆಗಳನ್ನು ಬಳಸಿಕೊಂಡು ಗುಲಾಬಿ ಕಾಯಿ ಕೊರಕದಿಂದ ಬೆಳೆಯನ್ನು ಉಳಿಸಿದೆ.",
                imageUrl = "crop_cotton",
                yieldIncrease = "Saved 80% yield",
                issueFaced = "Sudden outbreak of Pink Bollworm in neighboring farms.",
                solutionApplied = "Early preventive spraying based on app alerts.",
                aiAdvantage = "Regional pest outbreak notifications.",
                detailedStory = "Basavaraj, a cotton farmer in Gadag, was worried when he heard about Pink Bollworm destroying crops in the next village. It's a pest that can ruin a harvest overnight.\n\nHe received an urgent notification from the Raitha-Varta app about the high risk in his area. The app suggested immediate preventive measures and specific bio-pesticides.\n\nBasavaraj didn't wait. He followed the app's instructions and applied the recommended sprays. He also installed pheromone traps as a secondary defense.\n\nWhile many neighboring farms suffered heavy losses, Basavaraj's crop remained largely unaffected. He was able to harvest a full, high-quality yield.\n\nThe app also helped him find a reliable buyer who offered a fair price for his pest-free cotton. He now checks the app daily for any new alerts.\n\nHis experience has shown the village that being proactive with technology can save a whole season's hard work."
            ),
            SuccessStory(
                id = "s8",
                farmerName = "Laxmi",
                district = "Chikmagalur",
                districtKn = "ಚಿಕ್ಕಮಗಳೂರು",
                cropType = "Coffee",
                cropTypeKn = "ಕಾಫಿ",
                story = "Improved coffee quality through better processing.",
                storyKn = "ಉತ್ತಮ ಸಂಸ್ಕರಣೆಯ ಮೂಲಕ ಕಾಫಿ ಗುಣಮಟ್ಟವನ್ನು ಸುಧಾರಿಸಿದೆವು.",
                imageUrl = "crop_coffee",
                yieldIncrease = "Premium price achieved",
                issueFaced = "Getting low prices despite good yield due to average quality.",
                solutionApplied = "Modified fermentation and drying process based on AI tips.",
                aiAdvantage = "Processing guidelines for specialty coffee.",
                detailedStory = "Laxmi, who manages a small coffee estate in Chikmagalur, was frustrated that her coffee was always graded as 'average' at the auction.\n\nShe used the Raitha-Varta app to learn about specialty coffee processing. The AI provided detailed guides on optimal fermentation times and drying temperatures.\n\nLaxmi invested in better drying mats and closely monitored the moisture levels using the app's guidance. She also improved the sorting process for the cherries.\n\nThe next batch of coffee she sent for grading received a 'Premium' rating. This allowed her to sell her produce for 30% more than the previous year.\n\nShe is now focused on sustainable estate management and uses the app to track her soil's organic carbon levels. Her goal is to achieve organic certification.\n\nLaxmi believes that focusing on quality rather than just quantity is the key to long-term success in coffee farming."
            ),
            SuccessStory(
                id = "s9",
                farmerName = "Manjunath",
                district = "Davangere",
                districtKn = "ದಾವಣಗೆರೆ",
                cropType = "Maize",
                cropTypeKn = "ಮೆಕ್ಕೆಜೋಳ",
                story = "Precision fertilization saved costs and increased yield.",
                storyKn = "ನಿಖರವಾದ ರಸಗೊಬ್ಬರ ಬಳಕೆಯು ವೆಚ್ಚವನ್ನು ಉಳಿಸಿತು ಮತ್ತು ಇಳುವರಿಯನ್ನು ಹೆಚ್ಚಿಸಿತು.",
                imageUrl = "crop_maize",
                yieldIncrease = "20% cost reduction",
                issueFaced = "High cost of fertilizers and declining soil fertility.",
                solutionApplied = "Soil test based precision application.",
                aiAdvantage = "Customized fertilizer schedule.",
                detailedStory = "Manjunath from Davangere was spending a fortune on fertilizers for his maize crop. Despite the high cost, his soil health seemed to be deteriorating every year.\n\nThe Raitha-Varta app encouraged him to get a soil test done. He uploaded the results to the app, and the AI generated a customized fertilization plan.\n\nIt turned out he was over-applying certain nutrients while neglecting others. By following the AI's precision schedule, he reduced his fertilizer use by 20%.\n\nSurprisingly, even with less fertilizer, his maize grew taller and the cobs were larger. His soil started showing signs of better health with more organic activity.\n\nManjunath saved significant money and got a better harvest. He now uses the app to plan his crop rotation, ensuring the soil remains productive for years to come.\n\nHe has become a role model for 'Smart Farming' in his community, showing that more is not always better when it comes to inputs."
            ),
            SuccessStory(
                id = "s10",
                farmerName = "Ravi",
                district = "Bagalkot",
                districtKn = "ಬಾಗಲಕೋಟೆ",
                cropType = "Pomegranate",
                cropTypeKn = "ದಾಳಿಂಬೆ",
                story = "Controlled Bacterial Blight with timely intervention.",
                storyKn = "ಸಕಾಲಿಕ ಹಸ್ತಕ್ಷೇಪದೊಂದಿಗೆ ಬ್ಯಾಕ್ಟೀರಿಯಾದ ಬ್ಲೈಟ್ ಅನ್ನು ನಿಯಂತ್ರಿಸಿದೆವು.",
                imageUrl = "crop_pomegranate",
                yieldIncrease = "Saved entire orchard",
                issueFaced = "Bacterial Blight threatened the livelihood of my family.",
                solutionApplied = "Strict sanitation and recommended bactericide schedule.",
                aiAdvantage = "Early detection and expert guidance.",
                detailedStory = "Ravi, a pomegranate grower in Bagalkot, was devastated when he saw the first signs of Bacterial Blight in his orchard. Many of his friends had lost their entire orchards to this disease.\n\nHe immediately consulted the Raitha-Varta AI expert. The app emphasized strict sanitation and provided a rigorous schedule for applying specific bactericides and immunity boosters.\n\nRavi followed the protocol to the letter. He also used the app to monitor weather conditions, as high humidity can trigger the spread of the disease.\n\nHis hard work paid off. The disease was contained to a few trees, and the rest of the orchard remained healthy. He was able to harvest high-quality fruit that was exported.\n\nNow, Ravi uses the app's predictive features to stay ahead of any disease threats. He also shares his learnings with other farmers through the app's community features.\n\nRavi's success has given hope to many other pomegranate growers in the region who were planning to give up on the crop."
            )
        )
    }

}
