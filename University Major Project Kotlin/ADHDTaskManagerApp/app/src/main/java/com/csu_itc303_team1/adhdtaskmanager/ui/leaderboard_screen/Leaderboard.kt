package com.csu_itc303_team1.adhdtaskmanager.ui.leaderboard_screen

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Final
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Response
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Users

fun usersList(response: Response) {

    // for each user, add them to the arraylist

    response.users?.let { users ->
        users.forEach{ user ->
            Final.addToList(user)
            user.displayName?.let { Log.i(TAG, it) }

        }
    }
    response.exception?.message?.let {
        Log.e(TAG, it)
    }

}


val LeaderboardBlue = Color(0xFF045EA5)

@Composable
fun LeaderboardItem(user: Users, rank: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(start = 10.dp)
    ) {
        Text(
            text = rank.toString(),
            fontSize = 22.sp,
            color = LeaderboardBlue
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = user.displayName ?: "",
            fontSize = 22.sp,
            color = LeaderboardBlue
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = user.country ?: "",
            fontSize = 22.sp,
            color = LeaderboardBlue
        )
        Spacer(modifier = Modifier.width(50.dp))
        Text(
            text = user.points.toString(),
            fontSize = 22.sp,
            color = LeaderboardBlue
        )
    }
}