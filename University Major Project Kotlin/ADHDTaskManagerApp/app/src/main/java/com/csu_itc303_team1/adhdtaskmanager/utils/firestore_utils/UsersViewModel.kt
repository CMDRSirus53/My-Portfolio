package com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.AuthUiClient
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate


class UsersViewModel(
    private val repo: UsersRepo = UsersRepo(),
) : ViewModel() {

    private val _user = MutableStateFlow<Users?>(null)
    val user: StateFlow<Users?>
        get() = _user

    private var currentUser by mutableStateOf(Users())

    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    private lateinit var authUiClient: AuthUiClient

    fun checkUserInFirestore(userId: String, authUser: AuthUiClient) {
        userCollection.document(userId).get()
            // When it has finished searching
            .addOnCompleteListener { task ->
                // If it has found a an existing user
                if (task.isSuccessful) {
                    Log.d("User", "Completed Searching for User in Firestore")
                    val documentSnapshot = task.result
                    // If a snapshot exists and it is not null
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Log.d("User", "Found User $userId in Firestore")
                        // convert to Users Object
                        val user = documentSnapshot.toObject(Users::class.java)
                        val lastLogin = user?.lastLoginDate!!
                        val todaysDate = getCurrentDate()

                        // If last login date does not equal todays date
                        if (todaysDate != lastLogin) {
                            Log.d("User", "User has not logged in today yet. Giving Log in Reward and updating last login date.")
                            updateLastLoginDate(userId)
                            updateLoginNum(userId)
                        } else {
                            Log.d("User", "User has already logged in today.")
                        }
                        // Made the current user the found user
                        _user.value = user
                    } else {
                        // If User did not exist in data base, add it
                        Log.d("User", "Did Not Find User in Firestore. Adding...")
                        val newUser = Users(
                            displayName = authUser.getSignedInUser()?.username,
                            points = 0,
                            emailAddress = null,
                            password = null,
                            username = authUser.getSignedInUser()?.username,
                            country = null,
                            userID = authUser.getSignedInUser()?.userId,
                            profileImage = authUser.getSignedInUser()?.profilePictureUrl,
                            loginNum = 2,
                            totalPoints = 2,
                            lastLoginDate = LocalDate.now().toString()
                        )
                        addUserToFirestore(newUser)
                    }
                } else {
                    task.exception?.printStackTrace()
                }
            }
    }

    private fun updateLastLoginDate(userId: String) {
        val currentDate = getCurrentDate()
        userCollection.document(userId).update("lastLoginDate", currentDate)
    }

    private fun updateLoginNum(userId: String) {
        userCollection.document(userId).update("loginNum", FieldValue.increment(2))
    }

    private fun addUserToFirestore(newUser: Users) {
        userCollection.document(newUser.userID.toString()).set(newUser)
            .addOnSuccessListener {
                _user.value = newUser
                Log.d("User", "Successfully added new User ${newUser.userID} in Firestore and set them as the current user.")
            }
            .addOnFailureListener { e ->
                Log.w("User", "There was an error user document in addToFirestore function", e)
            }
    }

    private fun getCurrentDate(): String {
        return LocalDate.now().toString()
    }

    fun initializeAuthUiClient(context: Context, oneTapClient: SignInClient) {
        authUiClient = AuthUiClient(context, oneTapClient, repo)
    }

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference


    fun fetchAndUpdateUserPoints(userId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getUser(userId)  // Fetch the user. _user value will be updated after this call.

                // Wait for _user to be populated with the fetched user.
                delay(500)

                // Calculate totalPoints
                val fetchedUser = _user.value
                val totalPoints = (fetchedUser?.points ?: 0) + (fetchedUser?.loginNum ?: 0)

                // Update Firestore
                updateTotalPoints(userId, totalPoints)
            }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.getUserTwo(userId).collect { user ->
                    _user.value = user
                }
            }
        }
    }

    fun updateUserCountry(userId: String, newCountry: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.updateCountry(userId, newCountry)
            }
        }
    }

    fun updateTotalPoints(userId: String, totalPoints: Int) {
        // Reference to the Firestore collection.
        val db = FirebaseFirestore.getInstance().collection("users")

        db.document(userId).update("totalPoints", totalPoints)
            .addOnSuccessListener {
                Log.d("Firestore", "Document successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error updating document", e)
            }
    }

    fun completedTaskPoints() {
        val points = user.value?.points?.plus(3)
        if (points != null) {
            user.value?.let { repo.updatePoints(it, points) }
        }
    }

    fun fetchImageList(directory: String): Flow<List<Uri>> = flow {
        val listResult = storageRef.child(directory).listAll().await()
        val uriList = mutableListOf<Uri>()
        for (reference in listResult.items) {
            val downloadUri = reference.getDownloadUrl().await() // Corrected here
            uriList.add(downloadUri)
        }
        emit(uriList)
    }

}