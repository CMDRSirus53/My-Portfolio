package com.csu_itc303_team1.adhdtaskmanager

data class RewardState (
    val title : String = "",
    val description: String = "",
    val pointsAwarded: Int = 0,
    val timesAchieved: Int = 0
        )