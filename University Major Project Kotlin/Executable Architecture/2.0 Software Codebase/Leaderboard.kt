package com.csu_itc303_team1.adhdtaskmanager

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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


@Composable
fun LeaderboardItem(user: Users, rank: Int) {

    // This will be each row in the leaderboard

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
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(15.dp))

        Spacer(modifier = Modifier.width(15.dp))
        // Text for the Drawer Item
        Text(
            text = user.displayName.toString(),
            fontSize = 22.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(15.dp))
        // Text for the Drawer Item
        Text(
            text = user.country.toString(),
            fontSize = 22.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(50.dp))
        // Text for the Drawer Item
        Text(
            text = user.points.toString(),
            fontSize = 22.sp,
            color = Color.Black
        )
    }
}

