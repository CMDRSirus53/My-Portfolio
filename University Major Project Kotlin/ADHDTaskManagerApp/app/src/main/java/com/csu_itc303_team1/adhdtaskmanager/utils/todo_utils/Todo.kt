package com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Todo(
    var title: String,
    var description: String,
    var priority: Priority,
    var dueDate: String,
    var dueTime: String,
    val isCompleted: Boolean = false,
    var completionDate: String = "",
    var isClicked: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userID: String
)