package com.csu_itc303_team1.adhdtaskmanager.ui.completed_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.CompletedTaskCard
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.MainTopAppBar
import com.csu_itc303_team1.adhdtaskmanager.utils.states.TodoState
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.SortType
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.TodoEvent
import kotlinx.coroutines.CoroutineScope


@Composable
fun CompletedScreen(state: TodoState, scope: CoroutineScope, drawerState: DrawerState) {

    Scaffold(
        topBar = { MainTopAppBar(scope = scope, drawerState = drawerState)},
        content = {paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                items(state.todos, {it.id}) { todo ->
                    if (todo.userID == state.userId && todo.isCompleted){
                        CompletedTaskCard(todo)
                    }
                }
            }
        }
    )
}