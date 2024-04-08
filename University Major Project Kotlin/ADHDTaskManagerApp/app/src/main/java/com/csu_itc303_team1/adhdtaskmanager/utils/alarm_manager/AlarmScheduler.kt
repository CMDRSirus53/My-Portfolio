package com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItem: AlarmItem)
}