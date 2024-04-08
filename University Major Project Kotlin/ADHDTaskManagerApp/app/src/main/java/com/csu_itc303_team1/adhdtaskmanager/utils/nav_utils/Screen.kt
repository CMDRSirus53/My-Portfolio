package com.csu_itc303_team1.adhdtaskmanager.utils.nav_utils

import com.csu_itc303_team1.adhdtaskmanager.R

/**
 * When a New Screen is added for the Navigation Drawer Menu. A new Screen Object will
 * need to be created here...
 */

sealed class Screen(val route: String, var icon: Int, val title: String) {
    object TodoScreen : Screen("todo_screen", R.drawable.ic_home,"Home")
    object SettingsScreen: Screen("settings_screen", R.drawable.ic_settings,"Settings")
    object LeaderboardScreen: Screen("leaderboard_screen", R.drawable.ic_leaderboard,"Leaderboard")
    object RewardsScreen: Screen("reward_screen", R.drawable.ic_rewards,"Rewards")
    object CompletedScreen: Screen("completed_screen", R.drawable.ic_complete,"Completed Tasks")
    // Sign In Screen
    object SignInScreen: Screen("sign_in_screen", R.drawable.ic_sign_in,"Sign In")

    // Help Screen
    object HelpScreen: Screen("help_screen", R.drawable.ic_help,"Help")

    // Pomodoro Timer Screen
    object PomodoroTimerScreen: Screen("pomodoro_timer_screen", R.drawable.ic_pomodoro_timer, "Pomodoro Timer")
}