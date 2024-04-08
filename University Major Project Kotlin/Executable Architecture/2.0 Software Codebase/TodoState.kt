package com.csu_itc303_team1.adhdtaskmanager

import com.csu_itc303_team1.adhdtaskmanager.database.local.Todo

data class TodoState(
    val todos: List<Todo> = emptyList(),
    var title: String = "",
    var description: String = "",
    var priority: Priority = Priority.LOW,
    var dueDate: String = "",
    var dueTime: String = "",
    val sortType: SortType = SortType.BY_DATE,
    val showDialog: Boolean = false,
    val showEditTodoDialog: Boolean = false,
    val showDateSelector: Boolean = false,
    val showTimeSelector: Boolean = false,
)
