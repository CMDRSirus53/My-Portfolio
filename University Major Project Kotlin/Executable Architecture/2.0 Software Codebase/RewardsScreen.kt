package com.csu_itc303_team1.adhdtaskmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.database.local.Reward

@Composable
fun RewardsScreen(rewardViewModel: RewardViewModel) {

    val allRewards by rewardViewModel.allRewards.observeAsState(listOf())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "UserName",
                    fontSize = 30.sp
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp, 20.dp)
            ) {
                Text(
                    text = "Total Points",
                    fontSize = 26.sp,
                    modifier = Modifier.absoluteOffset(50.dp, 100.dp)
                )
                Text(
                    text = "350",
                    fontSize = 26.sp,
                    modifier = Modifier.absoluteOffset(100.dp, 100.dp)
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier.padding(10.dp, 150.dp, 10.dp)
    ) {
        items(allRewards) { reward ->
            RewardCard(reward)
        }
    }
}