package com.khoihealth.app.goals.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val userId: String,
    val dailySteps: Int = 8000,
    val dailyCalories: Int = 500,
    val sleepHours: Float = 8f,
    val activeMinutes: Int = 30,
    val waterMl: Int = 2000,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE userId = :userId LIMIT 1")
    fun getGoals(userId: String): Flow<GoalEntity?>

    @Query("SELECT * FROM goals WHERE userId = :userId LIMIT 1")
    suspend fun getGoalsSync(userId: String): GoalEntity?
}
