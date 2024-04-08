package com.csu_itc303_team1.adhdtaskmanager.ui.todo_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.csu_itc303_team1.adhdtaskmanager.utils.database_dao.TodoDao
import com.csu_itc303_team1.adhdtaskmanager.utils.states.TodoState
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Priority
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.SortType
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Todo
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.TodoEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TodoViewModel(
    private val todoDao: TodoDao
): ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    private val _sortType = MutableStateFlow(SortType.BY_DATE_TIME)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _todos = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.BY_DATE_TIME -> todoDao.sortByDueDateAndTime()
                SortType.BY_PRIORITY -> todoDao.sortByPriority()
                SortType.BY_COMPLETED -> todoDao.sortByCompleted()
                SortType.BY_NOT_COMPLETED -> todoDao.sortByNotCompleted()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val state = combine(_state, _sortType, _todos) { state, sortType, todos ->
        state.copy(
            todos = todos,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodoState())

    fun onEvent(event: TodoEvent) {
        when (event) {
            // Todo Title Error
            is TodoEvent.titleError -> {
                _state.update {
                    it.copy(
                        titleError = event.error
                    )
                }
            }
            is TodoEvent.ToggleLottieAnimation -> {
                _state.value = _state.value.copy(showLottieAnimation = event.show)
            }
            is TodoEvent.descriptionError -> {
                _state.update {
                    it.copy(
                        descriptionError = event.error
                    )
                }
            }
            // Update Todo
            is TodoEvent.updateTodo -> {
                viewModelScope.launch {
                    todoDao.updateTodo(
                        event.todo.copy(
                            title = state.value.title,
                            description = state.value.description,
                            priority = state.value.priority,
                            dueDate = state.value.dueDate,
                            dueTime = state.value.dueTime,
                            userID = state.value.userId
                        )
                    )
                }
            }
            // Toggle isClicked state of the todo
            is TodoEvent.toggleIsClicked -> {
                viewModelScope.launch {
                    todoDao.updateTodo(
                        event.todo.copy(
                            isClicked = !event.todo.isClicked
                        )
                    )
                }
            }
            // Delete Todo
            is TodoEvent.deleteTodo -> {
                viewModelScope.launch {
                    todoDao.deleteTodo(event.todo)
                }
            }
            TodoEvent.showDialog -> {
                _state.update {
                    it.copy(showDialog = true)
                }
            }
            TodoEvent.hideDialog -> {
                _state.update {
                    it.copy(showDialog = false)
                }
            }
            // Show edit date
            TodoEvent.showEditDateSelector -> {
                _state.update {
                    it.copy(showEditDateSelector = true)
                }
            }
            // Hide edit date
            TodoEvent.hideEditDateSelector -> {
                _state.update {
                    it.copy(showEditDateSelector = false)
                }
            }
            // Show edit time
            TodoEvent.showEditTimeSelector -> {
                _state.update {
                    it.copy(showEditTimeSelector = true)
                }
            }
            // Hide edit time
            TodoEvent.hideEditTimeSelector -> {
                _state.update {
                    it.copy(showEditTimeSelector = false)
                }
            }
            TodoEvent.saveTodo -> {
                val title = state.value.title
                val description = state.value.description
                val priority = state.value.priority
                val dueDate = state.value.dueDate
                val dueTime = state.value.dueTime
                val userId = state.value.userId

                if (title.isEmpty()) {
                    TodoEvent.titleError(true)
                    return
                }
                if (description.isEmpty()) {
                    TodoEvent.descriptionError(true)
                    return
                }

                val todo = Todo(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    userID = userId
                )

                viewModelScope.launch {
                    todoDao.insertTodo(todo)
                }

                _state.update {
                    it.copy(
                        title = "",
                        description = "",
                        priority = Priority.LOW,
                        dueDate = "",
                        dueTime = "",
                        showDialog = false,
                        showEditTodoDialog = false,
                        titleError = false,
                        descriptionError = false
                    )
                }
            }

            is TodoEvent.setDescription -> {
                _state.update {
                    it.copy(description = event.description)
                }
            }

            is TodoEvent.setDueDate -> {
                _state.update {
                    it.copy(dueDate = event.dueDate)
                }
            }

            is TodoEvent.setDueTime -> {
                _state.update {
                    it.copy(dueTime = event.dueTime)
                }
            }

            is TodoEvent.setPriority -> {
                _state.update {
                    it.copy(priority = event.priority)
                }
            }

            is TodoEvent.setTitle -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }

            is TodoEvent.sortBy -> {
                _sortType.value = event.sortType
            }

            TodoEvent.hideEditTodoDialog -> {
                _state.update {
                    it.copy(showEditTodoDialog = false)
                }
            }

            TodoEvent.showEditTodoDialog -> {
                _state.update {
                    it.copy(showEditTodoDialog = true)
                }
            }
            TodoEvent.hideDateSelector -> {
                _state.update {
                    it.copy(showDateSelector = false)
                }
            }
            TodoEvent.showDateSelector -> {
                _state.update {
                    it.copy(showDateSelector = true)
                }
            }
            TodoEvent.hideTimeSelector -> {
                _state.update {
                    it.copy(showTimeSelector = false)
                }
            }
            TodoEvent.showTimeSelector -> {
                _state.update {
                    it.copy(showTimeSelector = true)
                }
            }
            // Toggle the completed state of the todo
            is TodoEvent.toggleCompleted -> {
                viewModelScope.launch {
                    todoDao.updateTodo(
                        event.todo.copy(
                            isCompleted = !event.todo.isCompleted,
                            completionDate = if (event.todo.isCompleted) {
                                ""
                            } else {
                                LocalDateTime.now().toString()
                            }
                        )
                    )
                }
            }
            is TodoEvent.setUserId -> {
                _state.update {
                    it.copy(userId = event.userId)
                }
            }

            is TodoEvent.getTodoById -> {
                viewModelScope.launch {
                    todoDao.getTodoById(event.id)
                }
            }
            // Reset State Todos to values from database
            TodoEvent.resetTodos -> {
                viewModelScope.launch {
                    todoDao.getAllTodos().collect {
                        state.value.todos = it
                    }
                }
            }
            // Reset State
            TodoEvent.resetState -> {
                _state.update {
                    it.copy(
                        title = "",
                        description = "",
                        priority = Priority.LOW,
                        dueDate = "",
                        dueTime = "",
                        showDialog = false,
                        showEditTodoDialog = false,
                        titleError = false,
                        descriptionError = false
                    )
                }
            }
            else -> {}
        }
    }
}