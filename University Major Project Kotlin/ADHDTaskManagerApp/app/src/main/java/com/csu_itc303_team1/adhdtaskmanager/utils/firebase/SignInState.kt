package com.csu_itc303_team1.adhdtaskmanager.utils.firebase

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val userIsAnonymous: Boolean = false,
    val signInError: String? = null
)