package com.csu_itc303_team1.adhdtaskmanager.ui.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Users

@Composable
fun LoginRewardCard(user: Users?) {
    val timesAchieved = user?.loginNum?.div(2) ?: 0
    val total: Int? = user?.loginNum

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp
            )
            .fillMaxWidth()
            .background(Color.White),
        elevation = 4.dp
    ) {
        Column {  // Wrap the Rows in a Column
            Row(
                modifier = Modifier.padding(15.dp)
            ) {
                Column {
                    Text(
                        text = "Log in Reward",
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (timesAchieved > 1)
                            "You have achieved this reward $timesAchieved times"
                        else
                            "You have achieved this reward $timesAchieved time",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Column {
                    if (total != null) {
                        Text(
                            text = if (total > 1)
                                "You have earned $total points"
                            else
                                "You have earned $total point",
                            fontSize = 20.sp,
                            color = LeaderboardBlue
                        )
                    }
                }
            }
        }
    }
}