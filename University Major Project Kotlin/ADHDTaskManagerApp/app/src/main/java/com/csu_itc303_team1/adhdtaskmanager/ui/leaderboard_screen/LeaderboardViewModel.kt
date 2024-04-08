package com.csu_itc303_team1.adhdtaskmanager.ui.leaderboard_screen

import androidx.lifecycle.ViewModel
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.FirebaseCallback
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.UsersRepo

// Leaderboard View Model. Creates a User Repository

class LeaderboardViewModel(
    private val repo: UsersRepo = UsersRepo()
): ViewModel() {

    fun getResponseUsingCallback(callback: FirebaseCallback) = repo.getResponse(callback)

}