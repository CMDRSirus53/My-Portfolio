package com.csu_itc303_team1.adhdtaskmanager.utils.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observeConnectivity(): Flow<Status>

    enum class Status {
        CONNECTED, DISCONNECTED, LOSING, LOST
    }
}