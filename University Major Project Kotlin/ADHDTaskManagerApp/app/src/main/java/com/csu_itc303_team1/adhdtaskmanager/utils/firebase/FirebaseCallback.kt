package com.csu_itc303_team1.adhdtaskmanager.utils.firebase

import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.Response

interface FirebaseCallback {
    fun onResponse(response: Response)
}