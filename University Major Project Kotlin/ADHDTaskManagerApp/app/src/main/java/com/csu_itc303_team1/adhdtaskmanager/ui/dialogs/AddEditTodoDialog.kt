package com.csu_itc303_team1.adhdtaskmanager.ui.dialogs


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager.AlarmItem
import com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager.AlarmSchedulerImpl
import com.csu_itc303_team1.adhdtaskmanager.utils.states.TodoState
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Priority
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Todo
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.TodoEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoDialog(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    scope: CoroutineScope,
    sheetState: SheetState,
    alarmScheduler: AlarmSchedulerImpl,
    modifier: Modifier = Modifier
) {
    var thisTodo: Todo? = null
    var alarmItem: AlarmItem? = null

    if (state.showEditTodoDialog) {
        for (todo in state.todos) {
            if (todo.isClicked) {
                thisTodo = todo
            }
        }

        state.title = thisTodo?.title ?: ""
        state.description = thisTodo?.description ?: ""
        state.priority = thisTodo?.priority ?: Priority.LOW
        state.dueDate = (thisTodo?.dueDate ?: LocalDate.now()).toString()
        state.dueTime = (thisTodo?.dueTime ?: LocalTime.now()).toString()
    }


    var priorityExpandedMenu by remember { mutableStateOf(false) }
    var prioritySelection by remember { mutableStateOf("") }
    if (state.showEditTodoDialog) {
        prioritySelection = thisTodo?.priority?.name ?: ""
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text =
            if (state.showDialog){
                "Add a new task"
            } else{
                "Edit task"
            },
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
        }



        Column(
            modifier = modifier.height(200.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            TextField(
                modifier = Modifier
                    .border(
                        BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium
                    )
                    .width(280.dp),
                singleLine = true,
                value = if (state.showDialog){
                    state.title
                }else {
                    thisTodo?.title ?: ""
                }
                ,
                onValueChange = {
                    if (state.showDialog){
                        onEvent(TodoEvent.setTitle(it))
                    } else if (state.showEditTodoDialog){
                        thisTodo?.title = it
                        onEvent(TodoEvent.setTitle(it))
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium,
                isError = state.titleError,
                label = { Text("Enter Title of the task") } // This line adds a hint to the TextField
            )
            TextField(
                modifier = Modifier
                    .border(
                        BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium
                    )
                    .width(280.dp),
                singleLine = false,
                maxLines = 4,
                value = if (state.showDialog) {
                    state.description
                } else {
                    thisTodo?.description ?: ""
                },
                onValueChange = {
                    if (state.showDialog){
                        onEvent(TodoEvent.setDescription(it))
                    } else if (state.showEditTodoDialog){
                        thisTodo?.description = it
                        onEvent(TodoEvent.setDescription(it))
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = MaterialTheme.shapes.medium,
                isError = state.descriptionError,
                label = { Text("Provide a brief description") } // This line adds a hint to the TextField
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ){
            ExposedDropdownMenuBox(
                expanded = priorityExpandedMenu,
                onExpandedChange = { priorityExpandedMenu = !priorityExpandedMenu }
            )
            {
                Button(
                    modifier = Modifier
                        .width(130.dp)
                        .menuAnchor(),
                    onClick = { }) {
                    Text(
                        text = prioritySelection.ifEmpty {
                            "Priority"
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Priority"
                    )
                }

                ExposedDropdownMenu(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary),
                    expanded = priorityExpandedMenu,
                    onDismissRequest = { priorityExpandedMenu = false }) {
                    Priority.values().forEach { priorityLevel ->
                        DropdownMenuItem(
                            modifier = Modifier.clip(MaterialTheme.shapes.medium),
                            onClick = {
                                if (state.showDialog) {
                                    priorityExpandedMenu = false
                                    prioritySelection = priorityLevel.name
                                    onEvent(TodoEvent.setPriority(priorityLevel))
                                } else if (state.showEditTodoDialog) {
                                    priorityExpandedMenu = false
                                    thisTodo?.priority = priorityLevel
                                    onEvent(TodoEvent.setPriority(priorityLevel))
                                }

                            },
                            text = {
                                Text(
                                    text = priorityLevel.name,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                }
            }
        }


        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Instant.now().toEpochMilli(),
            yearRange = IntRange(2023, 2026),
            initialDisplayMode = DisplayMode.Picker,
            initialDisplayedMonthMillis = null,
            selectableDates = object : SelectableDates{
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter(LocalDateTime.now().minusDays(1))
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year in LocalDateTime.now().year .. LocalDateTime.now().year + 3
                }
            }
        )

        val editDatePickerState = rememberDatePickerState(
            initialSelectedDateMillis = Instant.now().toEpochMilli(),
            yearRange = IntRange(2023, 2026),
            initialDisplayMode = DisplayMode.Picker,
            initialDisplayedMonthMillis = null,
            selectableDates = object : SelectableDates{
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter(LocalDateTime.now().minusDays(1))
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year in LocalDateTime.now().year .. LocalDateTime.now().year + 3
                }
            }
        )

        val timePickerState = rememberTimePickerState(
            initialHour = LocalTime.now().hour,
            initialMinute = LocalTime.now().minute,
            is24Hour = false
        )

        val editTimePickerState = rememberTimePickerState(
            initialHour = LocalTime.now().hour,
            initialMinute = LocalTime.now().minute,
            is24Hour = false
        )


        val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
        var selectedDate: OffsetDateTime? = null
        var editedSelectedDate: OffsetDateTime? = null
        val cal: Calendar = Calendar.getInstance()
        val editCal: Calendar = Calendar.getInstance()



        if (state.showDateSelector || state.showEditDateSelector) {
            Column {
                DatePickerDialog(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .align(Alignment.CenterHorizontally),
                    onDismissRequest = { onEvent(TodoEvent.hideDateSelector) },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                if (state.showDialog){
                                    selectedDate = datePickerState.selectedDateMillis?.let {
                                        Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
                                    }
                                    Log.d("Date", selectedDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString())
                                    onEvent(TodoEvent.setDueDate(selectedDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()))
                                    onEvent(TodoEvent.hideDateSelector)
                                }else if (state.showEditTodoDialog){
                                    editedSelectedDate = editDatePickerState.selectedDateMillis?.let {
                                        Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
                                    }
                                    onEvent(TodoEvent.setDueDate(editedSelectedDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()))
                                    thisTodo?.dueDate = editedSelectedDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
                                    onEvent(TodoEvent.hideEditDateSelector)
                                }

                            }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),

                            onClick = {
                                if (state.showDialog) {
                                    onEvent(TodoEvent.hideDateSelector)
                                } else if (state.showEditTodoDialog){
                                    onEvent(TodoEvent.hideEditDateSelector)
                                }
                            }) {
                            Text(text = "Cancel")
                        }
                    },
                    shape = MaterialTheme.shapes.large,
                    content = {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            DatePicker(
                                state = if (state.showDialog){
                                    datePickerState
                                } else if (state.showEditTodoDialog){
                                    editDatePickerState
                                } else {
                                    datePickerState
                                },
                                showModeToggle = false,
                                title = null
                            )
                        }
                    },
                )
            }
        }

        if (state.showTimeSelector || state.showEditTimeSelector) {
            Column {
                // Time Picker Dialog
                DatePickerDialog(
                    modifier = Modifier
                        .fillMaxSize(),
                    onDismissRequest = {
                        if(state.showDialog) {
                            onEvent(TodoEvent.hideTimeSelector)
                        } else {
                            onEvent(TodoEvent.hideEditTimeSelector)
                        }
                    },
                    confirmButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                if (state.showDialog) {
                                    cal.isLenient = false
                                    cal.timeZone = TimeZone.getDefault()
                                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    cal.set(Calendar.MINUTE, timePickerState.minute)
                                    onEvent(TodoEvent.setDueTime(timeFormatter.format(cal.time)))
                                    onEvent(TodoEvent.hideTimeSelector)
                                } else{
                                    editCal.isLenient = false
                                    editCal.timeZone = TimeZone.getDefault()
                                    editCal.set(Calendar.HOUR_OF_DAY, editTimePickerState.hour)
                                    editCal.set(Calendar.MINUTE, editTimePickerState.minute)
                                    onEvent(TodoEvent.setDueTime(timeFormatter.format(editCal.time)))
                                    thisTodo?.dueTime = timeFormatter.format(editCal.time)
                                    onEvent(TodoEvent.hideEditTimeSelector)
                                }

                            }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = {
                                if (state.showDialog) {
                                    onEvent(TodoEvent.hideTimeSelector)
                                } else{
                                    onEvent(TodoEvent.hideEditTimeSelector)
                                }
                            }) {
                            Text(text = "Cancel")
                        }
                    },
                    shape = MaterialTheme.shapes.large,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 25.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            TimeInput(
                                colors = TimePickerDefaults.colors(
                                    clockDialColor = MaterialTheme.colorScheme.background,
                                    clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    clockDialUnselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    selectorColor = MaterialTheme.colorScheme.primary,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    periodSelectorBorderColor = MaterialTheme.colorScheme.onPrimary,
                                    periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.background,
                                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onBackground,
                                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
                                    timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.background,
                                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onBackground
                                ),
                                state =
                                if (state.showDialog) {
                                    timePickerState
                                } else{
                                    editTimePickerState
                                },
                                //layoutType = TimePickerLayoutType.Vertical
                            )
                        }
                    },
                )
            }

        }


        // A New Row containing Two Columns. One for Date, and one for Time
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // On Button Click it opens the date Dialog Screen,
            // the text displays default or whatever is chosen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(10.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        if (state.showDialog) {
                            onEvent(TodoEvent.showDateSelector)
                        } else {
                            onEvent(TodoEvent.showEditDateSelector)
                        }
                    }
                )
                {
                    Text(text = "Date", fontWeight = FontWeight.Bold)
                }
                if (state.showDialog){
                    Text(text = state.dueDate)
                } else {
                    Text(text = thisTodo?.dueDate ?: "")
                }
            }
            // On Button Click it opens the Time Dialog Screen,
            // the text displays default or whatever is chosen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(10.dp)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        if (state.showDialog) {
                            onEvent(TodoEvent.showTimeSelector)
                        } else {
                            onEvent(TodoEvent.showEditTimeSelector)
                        }
                    }) {
                    Text(text = "Time", fontWeight = FontWeight.Bold)
                }
                if (state.showDialog){
                    Text(text = state.dueTime)
                } else {
                    Text(text = thisTodo?.dueTime ?: "")
                }

            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                onClick = {
                    if (state.showDialog){
                        onEvent(TodoEvent.resetState)
                        scope.launch {
                            sheetState.hide()
                        }
                    } else if (state.showEditTodoDialog){
                        thisTodo.let { TodoEvent.toggleIsClicked(it!!) }.let { onEvent(it) }
                        onEvent(TodoEvent.resetState)
                        onEvent(TodoEvent.resetTodos)
                        scope.launch {
                            sheetState.hide()
                        }
                    }
                }
            ) {
                Text(text = "Cancel", fontWeight = FontWeight.Bold)
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                onClick = {
                    if (state.title.isNotEmpty() && state.description.isNotEmpty()) {
                        if (state.showDialog) {
                            onEvent(TodoEvent.saveTodo)
                            if (state.dueDate.isNotBlank() && state.dueTime.isNotBlank()){
                                alarmItem = AlarmItem(
                                    id = state.id,
                                    time = LocalDateTime.parse(
                                        "${state.dueDate} ${state.dueTime}",
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                                    ).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                    title = state.title,
                                    description = state.description
                                )
                                alarmItem?.let(alarmScheduler::schedule)
                            }
                            scope.launch {
                                sheetState.hide()
                            }
                        } else if (state.showEditTodoDialog) {
                            if (thisTodo?.dueDate?.isNotBlank() == true && thisTodo.dueTime.isNotBlank()){
                                alarmItem = AlarmItem(
                                    id = thisTodo.id,
                                    time = LocalDateTime.parse(
                                        "${thisTodo.dueDate} ${thisTodo.dueTime}",
                                        DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                                    ).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                                    title = thisTodo.title,
                                    description = thisTodo.description
                                )
                                alarmItem?.let(alarmScheduler::schedule)
                            }
                            onEvent(TodoEvent.updateTodo(thisTodo!!))
                            onEvent(TodoEvent.toggleIsClicked(thisTodo))
                            onEvent(TodoEvent.resetState)
                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    }
                    if (state.title.isEmpty()){
                        onEvent(TodoEvent.titleError(true))
                    }else{
                        onEvent(TodoEvent.titleError(false))
                    }
                    if (state.description.isEmpty()){
                        onEvent(TodoEvent.descriptionError(true))
                    }else {
                        onEvent(TodoEvent.descriptionError(false))
                    }

                }
            ) {
                if (state.showDialog){
                    Text(text = "Add Task", fontWeight = FontWeight.Bold)
                } else if (state.showEditTodoDialog){
                    Text(text = "Edit Task", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}