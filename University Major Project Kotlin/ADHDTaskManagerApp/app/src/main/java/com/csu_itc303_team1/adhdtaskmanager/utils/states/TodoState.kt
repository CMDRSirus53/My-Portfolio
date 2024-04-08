package com.csu_itc303_team1.adhdtaskmanager.utils.states

import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Priority
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.SortType
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Todo

data class TodoState(
    var todos: List<Todo> = emptyList(),
    var title: String = "",
    var description: String = "",
    var priority: Priority = Priority.LOW,
    var dueDate: String = "",
    var dueTime: String = "",
    var userId: String = "",
    var isClicked: Boolean = false,
    var id: Int = 0,
    val sortType: SortType = SortType.BY_NOT_COMPLETED,
    val titleError: Boolean = false,
    val descriptionError: Boolean = false,
    val showDialog: Boolean = false,
    val showEditTodoDialog: Boolean = false,
    val showDateSelector: Boolean = false,
    val showTimeSelector: Boolean = false,
    val showEditDateSelector: Boolean = false,
    val showEditTimeSelector: Boolean = false,
    val completedTodos: List<Todo> = emptyList(),
    var showLottieAnimation: Boolean = false
)