package com.csu_itc303_team1.adhdtaskmanager.utils.local_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "rewards")
data class Reward (

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "pointsAwarded")
    var pointsAwarded: Int,

    @ColumnInfo(name = "timesAchieved")
    var timesAchieved: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)