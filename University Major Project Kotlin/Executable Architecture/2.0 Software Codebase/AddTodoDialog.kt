package com.csu_itc303_team1.adhdtaskmanager

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun AddTodoDialog(
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {onEvent(TodoEvent.hideDialog)},
        title = {Text(text = "Add Todo")},
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = state.title,
                    onValueChange = {
                        onEvent(TodoEvent.setTitle(it))
                    },
                    label = { Text("Enter Title of the task") } // This line adds a hint to the TextField
                )
                TextField(
                    value = state.description,
                    onValueChange = {
                        onEvent(TodoEvent.setDescription(it))
                    },
                    label = { Text("Provide a brief description") } // This line adds a hint to the TextField

                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp))
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                        ) {
                        RadioButton(
                            selected = state.priority == Priority.LOW,
                            onClick = {
                                onEvent(TodoEvent.setPriority(Priority.LOW))
                            })
                        Text(text = Priority.LOW.name)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        RadioButton(
                            selected = state.priority == Priority.MEDIUM,
                            onClick = {
                                onEvent(TodoEvent.setPriority(Priority.MEDIUM))
                            })
                        Text(text = Priority.MEDIUM.name)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        RadioButton(
                            selected = state.priority == Priority.HIGH,
                            onClick = {
                                onEvent(TodoEvent.setPriority(Priority.HIGH))
                            })
                        Text(text = Priority.HIGH.name)
                    }
                }
                // Date Picker && Time Picker
                var pickedDate by remember {            // date variable stored to remember
                    mutableStateOf(LocalDate.now())
                }
                var pickedTime by remember {            // time variable stored to remember
                    mutableStateOf(LocalTime.NOON)
                }
                val formattedDate by remember {         // date variable formatted to string
                    derivedStateOf {
                        DateTimeFormatter
                            .ofPattern("dd MMM yyyy")   // Format Selected
                            .format(pickedDate)
                    }
                }
                val formattedTime by remember {         // Time variable formatted to string
                    derivedStateOf {
                        DateTimeFormatter
                            .ofPattern("hh:mm a")   // The Format Selected
                            .format(pickedTime)
                    }
                }
                // Date and Time Dialog States
                val dateDialogState = rememberMaterialDialogState()
                val timeDialogState = rememberMaterialDialogState()

                // A New Row containing Two Columns. One for Date, and one for Time
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,


                ) {
                    // On Button Click it opens the date Dialog Screen,
                    // the text displays default or whatever is chosen
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Button(onClick = {
                            dateDialogState.show()
                        }) {
                            Text(text = "Date")
                        }
                        Text(text = formattedDate)
                    }
                    // On Button Click it opens the Time Dialog Screen,
                    // the text displays default or whatever is chosen
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top

                    ) {
                        Button(onClick = {
                            timeDialogState.show()
                        }) {
                            Text(text = "Time")
                        }
                        Text(text = formattedTime)
                    }
                }

                // Inside the Date Dialog Screen, Ok and Cancel Buttons
                MaterialDialog(
                    dialogState = dateDialogState,
                    buttons = {
                        positiveButton(text = "Ok") {
                        }
                        negativeButton(text = "Cancel")
                    }
                ) {
                    datepicker(
                        // Date Settings, initial date, title and a constraint
                        initialDate = LocalDate.now(),
                        title = "Pick a date",
                        allowedDateValidator = {
                            it.isAfter(LocalDate.now())
                        }
                    ) {
                        // What Happens when Date is picked.
                        // Date Chosen becomes the date variable
                        // the formatted date is then added to the TodoEvent
                        pickedDate = it
                        onEvent(TodoEvent.setDueDate(formattedDate))
                    }

                }
                MaterialDialog(
                    // Inside the Time Dialog Screen, Ok and Cancel Buttons
                    dialogState = timeDialogState,
                    buttons = {
                        positiveButton(text = "Ok") {
                        }
                        negativeButton(text = "Cancel")
                    }
                ) {
                    timepicker(
                        // Time Settings: initial time and title
                        initialTime = LocalTime.NOON,
                        title = "Pick a Time"
                    ) {
                        // What Happens when Time is picked.
                        // Date Chosen becomes the Time variable
                        // the formatted Time is then added to the TodoEvent
                        pickedTime = it
                        onEvent(TodoEvent.setDueTime(formattedTime))
                    }

                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEvent(TodoEvent.saveTodo)
                }
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onEvent(TodoEvent.hideDialog)
                }
            ) {
                Text(text = "Cancel")
            }
        }
    )
}