package com.csu_itc303_team1.adhdtaskmanager.database.local

import androidx.room.*
import com.csu_itc303_team1.adhdtaskmanager.database.local.Todo
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {

    @Query("SELECT * FROM Todo")
    suspend fun getAllTodos(): List<Todo>
    @Insert
    suspend fun insertTodo(todo: Todo)
    @Delete
    suspend fun deleteTodo(todo: Todo)
    @Update
    suspend fun updateTodo(todo: Todo)


    // Sort Todos by Priority
    @Query("SELECT * FROM Todo ORDER BY priority DESC")
    fun sortByPriority(): Flow<List<Todo>>

    // Sort Todos by Due Date
    @Query("SELECT * FROM Todo ORDER BY dueDate DESC")
    fun sortByDueDate(): Flow<List<Todo>>

    // Sort Todos by Due Time
    @Query("SELECT * FROM Todo ORDER BY dueTime DESC")
    fun sortByDueTime(): Flow<List<Todo>>

    @Update
    suspend fun updateTodoIsCompleted(todo: Todo)

    @Query("SELECT COUNT(*) FROM Todo WHERE isCompleted = 1")
    suspend fun getCountOfCompletedTodos(): Int

}