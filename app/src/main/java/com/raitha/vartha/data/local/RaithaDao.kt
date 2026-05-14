package com.raitha.vartha.data.local

import androidx.room.*
import com.raitha.vartha.models.Crop
import com.raitha.vartha.models.Tip
import com.raitha.vartha.models.SuccessStory
import kotlinx.coroutines.flow.Flow

@Dao
interface RaithaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrops(crops: List<Crop>)

    @Query("SELECT * FROM crops")
    fun getAllCrops(): Flow<List<Crop>>

    @Query("SELECT COUNT(*) FROM crops")
    suspend fun getCropsCount(): Int

    @Query("SELECT * FROM crops WHERE category = :category")
    fun getCropsByCategory(category: String): Flow<List<Crop>>

    @Query("SELECT * FROM crops WHERE id = :cropId")
    fun getCropById(cropId: String): Flow<Crop?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<Tip>)

    @Query("SELECT * FROM tips ORDER BY date DESC")
    fun getAllTips(): Flow<List<Tip>>

    @Query("SELECT COUNT(*) FROM tips")
    suspend fun getTipsCount(): Int

    @Query("SELECT * FROM tips WHERE isSaved = 1")
    fun getSavedTips(): Flow<List<Tip>>

    @Update
    suspend fun updateTip(tip: Tip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuccessStories(stories: List<SuccessStory>)

    @Query("SELECT COUNT(*) FROM success_stories")
    suspend fun getStoriesCount(): Int

    @Query("DELETE FROM success_stories")
    suspend fun deleteAllSuccessStories()

    @Query("SELECT * FROM success_stories")
    fun getAllSuccessStories(): Flow<List<SuccessStory>>
}
