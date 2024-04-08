package com.csu_itc303_team1.adhdtaskmanager

import androidx.lifecycle.ViewModel

// Leaderboard View Model. Creates a User Repository

class LeaderboardViewModel(
    private val repo: UsersRepo = UsersRepo()
): ViewModel() {
    fun getResponseUsingCallback(callback: FirebaseCallback) = repo.getResponse(callback)
}