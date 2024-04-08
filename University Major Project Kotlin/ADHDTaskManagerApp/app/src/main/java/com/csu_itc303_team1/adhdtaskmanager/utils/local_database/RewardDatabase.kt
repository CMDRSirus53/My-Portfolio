package com.csu_itc303_team1.adhdtaskmanager.utils.local_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.csu_itc303_team1.adhdtaskmanager.utils.database_dao.RewardDao

@Database(
    entities = [Reward::class],
    version = 1,
    exportSchema = false
)

abstract class RewardDatabase: RoomDatabase() {
    abstract val rewardDao: RewardDao

    companion object {
        @Volatile
        private var INSTANCE: RewardDatabase? = null

        fun getInstance(context: Context): RewardDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RewardDatabase::class.java,
                        "reward_database.db"
                    ).createFromAsset("reward.db").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}