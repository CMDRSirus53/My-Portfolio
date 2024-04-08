package com.csu_itc303_team1.adhdtaskmanager.database.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase: RoomDatabase() {
    abstract val todoDao: TodoDao

}