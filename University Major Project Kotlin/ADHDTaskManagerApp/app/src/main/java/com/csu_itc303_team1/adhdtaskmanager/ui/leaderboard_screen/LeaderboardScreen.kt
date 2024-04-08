package com.csu_itc303_team1.adhdtaskmanager.ui.leaderboard_screen


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.LeaderboardCard
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.MainTopAppBar
import com.csu_itc303_team1.adhdtaskmanager.ui.ui_components.NoInternetScreen
import com.csu_itc303_team1.adhdtaskmanager.utils.connectivity.ConnectivityObserver
import com.csu_itc303_team1.adhdtaskmanager.utils.connectivity.ConnectivityObserverImpl
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Final
import kotlinx.coroutines.CoroutineScope

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LeaderboardScreen(
    connectivityObserver: ConnectivityObserverImpl,
    scope: CoroutineScope,
    drawerState: DrawerState
) {


    val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/adhdtaskmanager-d532d.appspot.com/o/default-user-profile-picture%2FUntitled.png?alt=media&token=0461fb17-8ef2-4192-9c9d-25dfacfd7420"

    // get list of users and sort it by points
    val usersList = Final.finalDataList
    val sortedList = usersList.sortedWith(compareByDescending { it.points?.plus(it.loginNum!!) })

    val observer = connectivityObserver.observeConnectivity().collectAsState(initial = ConnectivityObserver.Status.DISCONNECTED).value

    Scaffold(
        modifier =  Modifier.fillMaxSize(),
        topBar = { MainTopAppBar(scope = scope, drawerState = drawerState)},
        content = {
            when(observer){
                ConnectivityObserver.Status.CONNECTED -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text(
                                text = "Rank",
                                modifier = Modifier.weight(1f),
                                color = LeaderboardBlue,
                                fontSize = 18.sp
                            )
                            // Added space for the Mascot header here
                            Spacer(modifier = Modifier.width(88.dp))
                            Text(
                                text = "Name",
                                modifier = Modifier.weight(1.5f),
                                color = LeaderboardBlue,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Country",
                                modifier = Modifier.weight(1.5f),
                                color = LeaderboardBlue,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Points",
                                modifier = Modifier.weight(1f),
                                color = LeaderboardBlue,
                                fontSize = 18.sp
                            )
                        }
                        //Spacer(modifier = Modifier.height(20.dp))

                        LazyColumn(
                            modifier = Modifier,
                            contentPadding = PaddingValues(0.dp, 20.dp, 0.dp)
                        ) {
                            items(items = sortedList) { element ->
                                LeaderboardCard(
                                    user = element,
                                    rank = sortedList.indexOf(element) + 1,
                                    defaultProfileImageUrl = defaultImageUrl
                                )
                            }
                        }
                    }
                }
                else -> NoInternetScreen()
            }
        }
    )
}