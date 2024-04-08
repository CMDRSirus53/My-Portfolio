package com.csu_itc303_team1.adhdtaskmanager.ui.settings_screen

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils.UsersViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SettingsViewModel(
    application: Application,
    private val usersViewModel: UsersViewModel
) : AndroidViewModel(application) {

    // Using SharedPreferences to store the theme preference
    private val prefs: SharedPreferences = application.getSharedPreferences(
        "settings_prefs", Context.MODE_PRIVATE
    )

    // LiveData to represent the dark theme state
    val isDarkTheme: MutableLiveData<Boolean> = MutableLiveData(
        prefs.getBoolean("isDarkTheme", false)
    )

    // LiveData for Pomodoro work timer and break timer
    val workTimerValue: MutableLiveData<Int>
    val breakTimerValue: MutableLiveData<Int>

    init {
        workTimerValue = MutableLiveData(
            prefs.getInt("workTimerValue", 25) // default 25 minutes
        )
        breakTimerValue = MutableLiveData(
            prefs.getInt("breakTimerValue", 5)  // default 5 minutes
        )
    }

    // LiveData to hold profile image URLs
    val profileImages = MutableLiveData<List<String>>()

    // LiveData to hold the current user's profile image URL
    val currentUserProfileImage = MutableLiveData<String?>()


    fun updateUserCountry(userId: String, newCountry: String) {
        usersViewModel.updateUserCountry(userId, newCountry)
    }

    // Toggle the theme preference and save it
    fun toggleTheme(isDark: Boolean) {
        isDarkTheme.value = isDark
        with(prefs.edit()) {
            putBoolean("isDarkTheme", isDark)
            apply()
        }
    }

    fun saveTimerValues(workTime: Int, breakTime: Int) {
        workTimerValue.value = workTime
        breakTimerValue.value = breakTime
        with(prefs.edit()) {
            putInt("workTimerValue", workTime)
            putInt("breakTimerValue", breakTime)
            apply()
        }
    }

    fun getWorkTimerValue(): Int {
        return prefs.getInt("workTimerValue", 25)
    }

    fun getBreakTimerValue(): Int {
        return prefs.getInt("breakTimerValue", 5)
    }

    fun fetchProfilePictures() {
        val storageReference = FirebaseStorage.getInstance().reference
        val userPicturesRef = storageReference.child("user-profile-pictures")

        userPicturesRef.listAll()
            .addOnSuccessListener { listResult ->
                val imageUrls = mutableListOf<String>()
                for (itemRef in listResult.items) {
                    itemRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrls.add(uri.toString())
                        profileImages.value = imageUrls // Update the live data
                    }
                }
            }
    }

    fun fetchCurrentUserProfileImage(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            val imageUrl = document.getString("profileImage")
            currentUserProfileImage.value = imageUrl
        }.addOnFailureListener {
            // Handle failure, log it or show an error
        }
    }

    fun updateProfileImage(userId: String, selectedImageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        userRef.update("profileImage", selectedImageUrl)
            .addOnSuccessListener {
                // Handle successful update, maybe show a toast or update UI
            }
            .addOnFailureListener { exception ->
                // Handle failure, show an error or log it
            }
    }
}

// Here's the factory class definition

class SettingsViewModelFactory(
    private val application: Application,
    private val usersViewModel: UsersViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application, usersViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}