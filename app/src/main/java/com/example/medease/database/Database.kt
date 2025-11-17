package com.example.medease.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.medease.data.model.Appointment
import com.example.medease.database.dao.AppointmentDao

@Database(entities = [Appointment::class], version = 1)
abstract class TotalDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile private var INSTANCE: TotalDatabase? = null

        fun getInstance(context: Context): TotalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    TotalDatabase::class.java,
                    "total_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

