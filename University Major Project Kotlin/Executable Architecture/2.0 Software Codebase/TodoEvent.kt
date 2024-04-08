package com.csu_itc303_team1.adhdtaskmanager

import com.csu_itc303_team1.adhdtaskmanager.database.local.Todo

sealed interface TodoEvent {

    // Data class to update todo
    data class updateTodo(val todo: Todo): TodoEvent

    object saveTodo: TodoEvent
    data class setTitle(val title: String): TodoEvent
    data class setDescription(val description: String): TodoEvent
    data class setPriority(val priority: Priority): TodoEvent
    data class setDueDate(val dueDate: String): TodoEvent
    data class setDueTime(val dueTime: String): TodoEvent
    data class deleteTodo(val todo: Todo): TodoEvent

    data class toggleCompleted(val todo: Todo): TodoEvent
    // Data class to sort by sort type
    data class sortBy(val sortType: SortType): TodoEvent

    object showDialog: TodoEvent
    object hideDialog: TodoEvent

    object showEditTodoDialog: TodoEvent
    object hideEditTodoDialog: TodoEvent

    object showDateSelector: TodoEvent
    object hideDateSelector: TodoEvent

    object showTimeSelector: TodoEvent
    object hideTimeSelector: TodoEvent

}