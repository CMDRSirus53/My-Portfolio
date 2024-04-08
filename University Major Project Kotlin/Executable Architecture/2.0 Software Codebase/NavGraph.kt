package com.csu_itc303_team1.adhdtaskmanager

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.csu_itc303_team1.adhdtaskmanager.database.local.RewardDao

/**
 * This is Navigation Controller code. This page points to the screen that is requested.
 * Each new screen will need to be added here as well
 */

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    state: TodoState,
    event: (TodoEvent) -> Unit,
    rewardViewModel: RewardViewModel,

) {
    // NavHost for controlling the pages.
    NavHost(
        navController = navController,
        startDestination = Screen.TodoScreen.route   // Screen that displays when app is first opened
    ){

        // Home screen/to-do screen
        composable(
            route = Screen.TodoScreen.route
        ) {
            TodoScreen(state = state, onEvent = event, rewardViewModel = rewardViewModel)
        }

        // Settings Screen
        composable(
            route = Screen.SettingsScreen.route
        ) {
            SettingsScreen()
        }

        // Leaderboard Screen
        composable(
            route = Screen.LeaderboardScreen.route
        ) {
            LeaderboardScreen()
        }

        // Rewards Screen
        composable(
            route = Screen.RewardsScreen.route
        ) {
            RewardsScreen(rewardViewModel)
        }
    }
}

