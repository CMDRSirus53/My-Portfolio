package com.csu_itc303_team1.adhdtaskmanager.utils.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.csu_itc303_team1.adhdtaskmanager.utils.database_dao.TodoDao
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Todo

@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase: RoomDatabase() {

    abstract val todoDao: TodoDao
}