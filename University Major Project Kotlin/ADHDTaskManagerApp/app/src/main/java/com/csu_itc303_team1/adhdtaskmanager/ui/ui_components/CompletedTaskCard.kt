package com.csu_itc303_team1.adhdtaskmanager.ui.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.utils.todo_utils.Todo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CompletedTaskCard(todo: Todo) {

    // If completion date = "" then make parsedDate Minimum Date
    val parsedDate: LocalDateTime = if (todo.completionDate == "") {
        LocalDateTime.MIN
    } else {
        LocalDateTime.parse(todo.completionDate, DateTimeFormatter.ISO_DATE_TIME)
    }

    val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm a"))

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            .height(160.dp)
            .background(Color.White),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = todo.title,
                    fontSize = 24.sp, // Increased font size
                    color = Color.Black
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = todo.description,
                    fontSize = 20.sp, // Adjusted font size
                    color = Color.Black // Adjusted color
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Completed On:",
                    fontSize = 20.sp, // Adjusted font size
                    color = LeaderboardBlue
                )
            }
            Row(
                modifier = Modifier.padding(top = 8.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = todo.priority.name, color = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text(text = todo.dueDate, color = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text(text = todo.dueTime, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = formattedDate, color = LeaderboardBlue)
            }
        }
    }
}