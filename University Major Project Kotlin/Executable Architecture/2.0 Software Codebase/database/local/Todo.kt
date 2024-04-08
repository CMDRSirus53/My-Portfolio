package com.csu_itc303_team1.adhdtaskmanager.database.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.csu_itc303_team1.adhdtaskmanager.Priority


@Entity
data class Todo(
    var title: String,
    var description: String,
    var priority: Priority,
    var dueDate: String,
    var dueTime: String,
    val isCompleted: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)