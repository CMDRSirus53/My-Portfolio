package com.csu_itc303_team1.adhdtaskmanager.utils.alarm_manager

import java.time.LocalDateTime

data class AlarmItem (
    var id: Int,
    var time: LocalDateTime,
    var title: String,
    var description: String,
)