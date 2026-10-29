package com.duoc.rackinspector.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.duoc.rackinspector.data.model.ComponentItem
import com.duoc.rackinspector.data.model.RackAssessment
import com.duoc.rackinspector.data.model.User

@Database(entities = [User::class, RackAssessment::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun assessmentDao(): AssessmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rack_inspector_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}