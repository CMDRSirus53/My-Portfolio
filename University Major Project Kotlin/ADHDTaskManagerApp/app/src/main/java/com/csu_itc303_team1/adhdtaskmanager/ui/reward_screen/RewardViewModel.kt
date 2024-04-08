package com.csu_itc303_team1.adhdtaskmanager.ui.reward_screen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.csu_itc303_team1.adhdtaskmanager.utils.local_database.Reward
import com.csu_itc303_team1.adhdtaskmanager.utils.local_database.RewardDatabase
import kotlinx.coroutines.launch

class RewardViewModel(application: Application): ViewModel() {

    var allRewards: LiveData<List<Reward>>
    private val repo: RewardRepo
    private var _searchResults = MutableLiveData<List<Reward>>((emptyList()))
    var searchResults: LiveData<List<Reward>> = _searchResults




    init {
        val rewardDb = RewardDatabase.getInstance(application)
        val rewardDao = rewardDb.rewardDao
        repo = RewardRepo(rewardDao)

        allRewards = repo.allRewards
    }

    fun insertReward(reward: Reward) {
        repo.insertReward(reward)
    }


    fun findReward(name: String): LiveData<List<Reward>> {
        viewModelScope.launch {
            searchResults = repo.findReward(name)
        }
        return searchResults
    }

    fun updateReward(reward: Reward) {
        repo.updateReward(reward)
    }

    fun deleteReward(reward: Reward) {
        repo.deleteReward(reward)
    }

    fun updateState(rewardName: String) {
        findReward(rewardName)
    }

}