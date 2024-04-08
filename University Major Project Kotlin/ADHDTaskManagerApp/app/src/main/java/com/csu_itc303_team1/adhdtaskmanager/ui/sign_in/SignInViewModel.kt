package com.csu_itc303_team1.adhdtaskmanager.ui.sign_in

import androidx.lifecycle.ViewModel
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.SignInResult
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()


    fun userIsAnonymous() {
        _state.update { it.copy(userIsAnonymous = true) }
    }


    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }


    fun resetState() {
        _state.update { SignInState() }
    }
}