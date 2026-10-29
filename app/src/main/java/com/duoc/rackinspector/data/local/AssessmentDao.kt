package com.duoc.rackinspector.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.duoc.rackinspector.data.model.RackAssessment

@Dao
interface AssessmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: RackAssessment)

    @Query("SELECT * FROM rack_assessments ORDER BY id DESC")
    suspend fun getAllAssessments(): List<RackAssessment>

    @Query("SELECT * FROM rack_assessments WHERE id = :id LIMIT 1")
    suspend fun getAssessmentById(id: Int): RackAssessment?

    @Update
    suspend fun updateAssessment(assessment: RackAssessment)

    @Delete
    suspend fun deleteAssessment(assessment: RackAssessment)

}