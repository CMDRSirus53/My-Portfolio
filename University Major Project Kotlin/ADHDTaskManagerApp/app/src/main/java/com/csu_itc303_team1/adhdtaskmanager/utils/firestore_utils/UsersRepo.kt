package com.csu_itc303_team1.adhdtaskmanager.utils.firestore_utils

import android.content.ContentValues.TAG
import android.util.Log
import com.csu_itc303_team1.adhdtaskmanager.utils.firebase.FirebaseCallback
import com.google.firebase.firestore.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UsersRepo (

    // initializing the firestore database
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance(),
    // creating a reference to the Firestore Collection, Users
    private val userRef: CollectionReference = rootRef.collection("users")
){

    // This will either respond with the data if successful or the exception if not successful
    fun getResponse(callback: FirebaseCallback) {
        userRef.get().addOnCompleteListener { task ->
            val response = Response()
            if (task.isSuccessful) {

                // For each document in the collection, create a user object

                val result = task.result
                result?.let {
                    response.users = result.documents.mapNotNull {snapShot ->
                        snapShot.toObject(Users::class.java)
                    }
                }
            } else {
                response.exception = task.exception
            }
            callback.onResponse(response)
        }
    }

    fun updateCountry(userId: String, newCountry: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        userRef.update("country", newCountry)
            .addOnSuccessListener {
                // Handle successful update, maybe log it or show a toast
            }
            .addOnFailureListener { exception ->
                // Handle failure, log it or show an error
            }
    }

    fun getUserTwo(userId: String): Flow<Users?> = callbackFlow {
        val listener = object : EventListener<DocumentSnapshot> {
            override fun onEvent(snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException?) {
                if (exception != null) {
                    cancel()
                    return
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(Users::class.java)
                    trySend(user)
                }
            }
        }
        val registration = rootRef.collection("users").document(userId).addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }

    fun updatePoints(user: Users, points: Int){
        val ref = userRef.document(user.userID.toString())

        ref.update("points", points)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!")
                println("I updated Points successfully")}
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e)
                println("I did not update Points")}
    }

    // Add this function:
    suspend fun updateUser(uid: String, userMap: HashMap<String, *>) {
        userRef.document(uid).update(userMap as Map<String, Any>).await()
    }

}