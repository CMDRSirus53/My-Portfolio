package com.csu_itc303_team1.adhdtaskmanager.ui.reward_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.*
import com.csu_itc303_team1.adhdtaskmanager.utils.connectivity.ConnectivityObserver
import com.csu_itc303_team1.adhdtaskmanager.utils.connectivity.ConnectivityObserverImpl
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Users
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.UsersViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CoroutineScope

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RewardsScreen(
    rewardViewModel: RewardViewModel,
    usersViewModel: UsersViewModel,
    connectivityObserver: ConnectivityObserverImpl,
    scope: CoroutineScope,
    drawerState: DrawerState
    ) {

    val observer = connectivityObserver.observeConnectivity().collectAsState(initial = ConnectivityObserver.Status.DISCONNECTED).value

    Scaffold(
        topBar = { MainTopAppBar(scope = scope, drawerState = drawerState)},
        content = {
            when(observer) {
                ConnectivityObserver.Status.CONNECTED -> {
                    val allRewards by rewardViewModel.allRewards.observeAsState(listOf())
                    val completedTaskRewards = allRewards.filter { it.title == "Completed Task Reward" }
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val user = usersViewModel.user.collectAsState(initial = Users()).value

                    if (userId != null) {
                        usersViewModel.fetchAndUpdateUserPoints(userId)
                    }
                    // Using Column to organize the layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 20.dp, end = 20.dp, top = 100.dp, bottom = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LeaderboardBlue, shape = RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = user?.username ?: "Username Not Found",
                                fontSize = 30.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Points",
                                fontSize = 26.sp,
                                color = LeaderboardBlue
                            )
                            Text(
                                text = ((user?.points ?: 0) + (user?.loginNum ?: 0)).toString(),
                                fontSize = 26.sp,
                                color = LeaderboardBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            items(completedTaskRewards) { reward ->
                                RewardCard(reward, "Completed Task Reward", user)
                            }

                            item {
                                LoginRewardCard(user)
                            }
                        }
                    }
                }
                else -> {
                    NoInternetScreen()
                }
            }
        }
    )
}