package com.csu_itc303_team1.adhdtaskmanager.ui.todo_screen


//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.csu_itc303_team1.adhdtaskmanager.ui.dialogs.AddEditTodoDialog
import com.csu_itc303_team1.adhdtaskmanager.ui.reward_screen.RewardViewModel
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.CustomToastMessage
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.MainTopAppBar
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.TodoCard
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.lottieLoaderAnimation
import com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager.AlarmSchedulerImpl
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Users
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.UsersViewModel
import com.csu_itc303_team1.adhdtaskmanager.utils.states.TodoState
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.SortType
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.TodoEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TodoScreen(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    rewardViewModel: RewardViewModel,
    usersViewModel: UsersViewModel,
    alarmScheduler: AlarmSchedulerImpl,
    navScope: CoroutineScope,
    drawerState: DrawerState
) {

    rewardViewModel.allRewards.observeAsState(listOf())

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    val scope = rememberCoroutineScope()

    val showToast = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = showToast.value) {
        if (showToast.value) {
            delay(3000) // 3 seconds
            showToast.value = false
        }
    }


    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetSwipeEnabled = false,
        sheetDragHandle = {},
        topBar = { MainTopAppBar(scope = navScope, drawerState = drawerState)},
        content = {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {
                            onEvent(TodoEvent.showDialog)
                            scope.launch {
                                sheetState.expand()
                            }
                        }
                    ) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onPrimary,
                            imageVector = Icons.Default.Add,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .height(32.dp)
                                .width(32.dp),
                            contentDescription = "Add Todo"
                        )
                    }
                }
            ) { paddingValues ->
                var expanded by remember { mutableStateOf(false) }

                if (state.showEditTodoDialog){
                    scope.launch {
                        sheetState.expand()
                    }
                }

                Column (
                    verticalArrangement = Arrangement.SpaceAround
                ){
                    // TODO: This is where the task are filtered
                    // If you only want the completed task to show, then you can set
                    // sortType to SortType.BY_COMPLETED
                    ExposedDropdownMenuBox(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 10.dp),
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded}
                    )
                    {
                        IconButton(
                            modifier = Modifier
                                .menuAnchor()
                                .align(Alignment.CenterHorizontally)
                            ,
                            onClick = { }) {
                            Icon(
                                tint = MaterialTheme.colorScheme.onBackground,
                                imageVector = Icons.Filled.List,
                                contentDescription = "Filter"
                            )
                        }

                        ExposedDropdownMenu(
                            modifier = Modifier
                                .width(150.dp)
                                .background(MaterialTheme.colorScheme.surface),
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            SortType.values().forEach { sortType ->
                                DropdownMenuItem(
                                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                                    onClick = {
                                        expanded = false
                                        onEvent(TodoEvent.sortBy(sortType))
                                    },
                                    text = {
                                        when (sortType.name) {
                                            "BY_PRIORITY" -> {
                                                Text(
                                                    text = "By Priority",
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            "BY_DATE_TIME" -> {
                                                Text(
                                                    text = "By Date",
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            "BY_COMPLETED" -> {
                                                Text(
                                                    text = "By Completed",
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            "BY_NOT_COMPLETED" -> {
                                                Text(
                                                    text = "By Not Completed",
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                    LazyColumn(
                        contentPadding = paddingValues,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {

                        items(state.todos) { todo ->
                            if (state.sortType != SortType.BY_COMPLETED) {
                                if (todo.userID == state.userId && !todo.isCompleted) {
                                    TodoCard(
                                        todo = todo,
                                        onEvent = onEvent,
                                        rewardViewModel = rewardViewModel,
                                        usersViewModel = usersViewModel,
                                        showToast = showToast,
                                        alarmScheduler = alarmScheduler,
                                        user = Users(),
                                    )
                                }
                            } else {
                                if (todo.userID == state.userId) {
                                    TodoCard(
                                        todo = todo,
                                        onEvent = onEvent,
                                        rewardViewModel = rewardViewModel,
                                        usersViewModel = usersViewModel,
                                        showToast = showToast,
                                        alarmScheduler = alarmScheduler,
                                        user = Users(),

                                    )
                                }
                            }
                        }
                    }
                }
                lottieLoaderAnimation(isVisible = showToast.value)
                CustomToastMessage(
                    message = "Congrats on completing a task!",
                    isVisible = showToast.value,
                )
            }
        },
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.showDialog || state.showEditTodoDialog) {
                    AddEditTodoDialog(state = state, onEvent = onEvent, scope = scope, sheetState = sheetState, alarmScheduler = alarmScheduler)
                }
            }
        }
    )
}