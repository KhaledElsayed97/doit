package dev.khaled.doit.data.repo

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dev.khaled.doit.data.model.User
import dev.khaled.doit.util.FireStoreCollection
import dev.khaled.doit.util.SharedPrefConstants
import dev.khaled.doit.util.UiState
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val appPreferences: SharedPreferences,
    private val gson: Gson
) : AuthRepo {

    override fun signup(
        email: String,
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    ) {
        android.util.Log.d("AuthRepoImpl", "Starting signup process for email: $email")
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid
                    android.util.Log.d("AuthRepoImpl", "Firebase Auth successful, UID: $uid")
                    
                    if (uid != null) {
                        user.id = uid
                        updateUserInfo(user) { state ->
                            when (state) {
                                is UiState.Success -> {
                                    android.util.Log.d("AuthRepoImpl", "User info updated successfully, now storing session")
                                    storeSession(id = uid) { storedUser ->
                                        if (storedUser != null) {
                                            android.util.Log.d("AuthRepoImpl", "Session stored successfully")
                                            result.invoke(UiState.Success("Registration Successful!"))
                                        } else {
                                            android.util.Log.e("AuthRepoImpl", "Failed to store session after successful registration")
                                            result.invoke(UiState.Failure("Registration successful, but failed to store session."))
                                        }
                                    }
                                }
                                is UiState.Failure -> {
                                    android.util.Log.e("AuthRepoImpl", "Failed to update user info: ${state.error}")
                                    result.invoke(UiState.Failure(state.error))
                                }
                                is UiState.Loading -> {
                                    // This shouldn't happen, but just in case
                                    android.util.Log.w("AuthRepoImpl", "Unexpected loading state in updateUserInfo")
                                }
                            }
                        }
                    } else {
                        android.util.Log.e("AuthRepoImpl", "Firebase Auth successful but UID is null")
                        result.invoke(UiState.Failure("Authentication failed: User ID is null"))
                    }
                } else {
                    android.util.Log.e("AuthRepoImpl", "Firebase Auth failed: ${task.exception?.message}")
                    try {
                        throw task.exception ?: Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, please use a stronger password"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, email invalid"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, email already exists"))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
    }

    override fun login(email: String, password: String, result: (UiState<String>) -> Unit) {
        android.util.Log.d("AuthRepoImpl", "Starting login process for email: $email")
        
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { signIn ->
            if (signIn.isSuccessful) {
                val user = auth.currentUser
                android.util.Log.d("AuthRepoImpl", "Firebase Auth successful, UID: ${user?.uid}")
                
                if (user?.uid != null) {
                    storeSession(id = user.uid) { storedUser ->
                        if (storedUser != null) {
                            android.util.Log.d("AuthRepoImpl", "Login successful, session stored")
                            result.invoke(UiState.Success("Login Success"))
                        } else {
                            android.util.Log.e("AuthRepoImpl", "Login successful but failed to store session")
                            result.invoke(UiState.Failure("Failed to store session locally"))
                        }
                    }
                } else {
                    android.util.Log.e("AuthRepoImpl", "Firebase Auth successful but User ID is null")
                    result.invoke(UiState.Failure("Authentication failed: User ID is null"))
                }
            } else {
                android.util.Log.e("AuthRepoImpl", "Firebase Auth failed: ${signIn.exception?.message}")
                try {
                    throw signIn.exception ?: Exception("Authentication failed")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    result.invoke(UiState.Failure("Invalid email or password"))
                } catch (e: Exception) {
                    result.invoke(UiState.Failure("Authentication failed: ${e.message}"))
                }
            }
        }.addOnFailureListener { exception ->
            android.util.Log.e("AuthRepoImpl", "Firebase Auth failed with exception: ${exception.message}")
            result.invoke(UiState.Failure("Authentication failed: ${exception.message}"))
        }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                result.invoke(UiState.Success("Password reset email has been sent"))
            } else {
                result.invoke(UiState.Failure(task.exception?.message))
            }
        }.addOnFailureListener {
            result.invoke(UiState.Failure("Failed, email does not exist"))
        }
    }

    override fun logout(result: () -> Unit) {
        android.util.Log.d("AuthRepoImpl", "Logging out user")
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
        android.util.Log.d("AuthRepoImpl", "User session cleared")
        result.invoke()
    }

    override fun getSession(result: (User?) -> Unit) {
        val userSession = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (userSession == null) {
            android.util.Log.d("AuthRepoImpl", "No user session found in SharedPreferences")
            result.invoke(null)
        } else {
            try {
                val user = gson.fromJson(userSession, User::class.java)
                if (user != null) {
                    android.util.Log.d("AuthRepoImpl", "User session retrieved successfully: ${user.email}")
                    result.invoke(user)
                } else {
                    android.util.Log.e("AuthRepoImpl", "Failed to deserialize user from JSON: $userSession")
                    result.invoke(null)
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthRepoImpl", "Exception while deserializing user session: ${e.message}")
                result.invoke(null)
            }
        }
    }

    override fun storeSession(id: String, result: (User?) -> Unit) {
        android.util.Log.d("AuthRepoImpl", "Starting storeSession for ID: $id")
        
        if (id.isEmpty()) {
            android.util.Log.e("AuthRepoImpl", "User ID is empty")
            result.invoke(null)
            return
        }
        
        database.collection(FireStoreCollection.USER).document(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    android.util.Log.d("AuthRepoImpl", "Firestore query successful, document exists: ${document?.exists()}")
                    
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        android.util.Log.d("AuthRepoImpl", "User object created: $user")
                        
                        if (user != null) {
                            try {
                                val userJson = gson.toJson(user)
                                android.util.Log.d("AuthRepoImpl", "User JSON: $userJson")
                                appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, userJson).apply()
                                android.util.Log.d("AuthRepoImpl", "User session stored successfully")
                                result.invoke(user)
                            } catch (e: Exception) {
                                android.util.Log.e("AuthRepoImpl", "Failed to store user session: ${e.message}")
                                e.printStackTrace()
                                result.invoke(null)
                            }
                        } else {
                            android.util.Log.e("AuthRepoImpl", "User document exists but failed to convert to User object")
                            android.util.Log.e("AuthRepoImpl", "Document data: ${document.data}")
                            result.invoke(null)
                        }
                    } else {
                        // User document doesn't exist, create it with basic info from Firebase Auth
                        android.util.Log.d("AuthRepoImpl", "User document does not exist, creating new user document")
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val newUser = User(
                                id = currentUser.uid,
                                name = currentUser.displayName ?: "",
                                email = currentUser.email ?: ""
                            )
                            
                            // Save the user to Firestore
                            database.collection(FireStoreCollection.USER).document(id).set(newUser)
                                .addOnSuccessListener {
                                    android.util.Log.d("AuthRepoImpl", "New user document created successfully")
                                    android.util.Log.d("AuthRepoImpl", "New user object: id=${newUser.id}, name=${newUser.name}, email=${newUser.email}")
                                    try {
                                        val userJson = gson.toJson(newUser)
                                        android.util.Log.d("AuthRepoImpl", "User JSON: $userJson")
                                        if (userJson.isNotEmpty()) {
                                            appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, userJson).apply()
                                            android.util.Log.d("AuthRepoImpl", "User session stored successfully")
                                            result.invoke(newUser)
                                        } else {
                                            android.util.Log.e("AuthRepoImpl", "Generated JSON is empty")
                                            result.invoke(null)
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("AuthRepoImpl", "Failed to store user session: ${e.message}")
                                        android.util.Log.e("AuthRepoImpl", "Exception type: ${e.javaClass.simpleName}")
                                        e.printStackTrace()
                                        
                                        // Fallback: try to store basic user info without Gson
                                        try {
                                            val fallbackJson = """{"id":"${newUser.id}","name":"${newUser.name}","email":"${newUser.email}"}"""
                                            android.util.Log.d("AuthRepoImpl", "Trying fallback JSON: $fallbackJson")
                                            appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, fallbackJson).apply()
                                            android.util.Log.d("AuthRepoImpl", "User session stored successfully with fallback")
                                            result.invoke(newUser)
                                        } catch (fallbackException: Exception) {
                                            android.util.Log.e("AuthRepoImpl", "Fallback also failed: ${fallbackException.message}")
                                            result.invoke(null)
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    android.util.Log.e("AuthRepoImpl", "Failed to create user document: ${exception.message}")
                                    exception.printStackTrace()
                                    result.invoke(null)
                                }
                        } else {
                            android.util.Log.e("AuthRepoImpl", "Firebase Auth current user is null")
                            result.invoke(null)
                        }
                    }
                } else {
                    android.util.Log.e("AuthRepoImpl", "Firestore query failed: ${task.exception?.message}")
                    task.exception?.printStackTrace()
                    result.invoke(null)
                }
            }.addOnFailureListener { exception ->
                android.util.Log.e("AuthRepoImpl", "Firestore query failed with exception: ${exception.message}")
                exception.printStackTrace()
                result.invoke(null)
            }
    }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        if (user.id.isEmpty()) {
            result.invoke(UiState.Failure("User ID is empty"))
            return
        }
        
        android.util.Log.d("AuthRepoImpl", "Updating user info for ID: ${user.id}, name: ${user.name}, email: ${user.email}")
        
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document.set(user)
            .addOnSuccessListener {
                android.util.Log.d("AuthRepoImpl", "User info updated successfully for ID: ${user.id}")
                result.invoke(UiState.Success("User has been updated successfully"))
            }.addOnFailureListener { exception ->
                android.util.Log.e("AuthRepoImpl", "Failed to update user info: ${exception.message}")
                exception.printStackTrace()
                result.invoke(UiState.Failure(exception.localizedMessage ?: "Failed to update user info"))
            }
    }

    override fun checkUserExists(id: String, result: (Boolean) -> Unit) {
        if (id.isEmpty()) {
            result.invoke(false)
            return
        }
        
        database.collection(FireStoreCollection.USER).document(id).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val exists = task.result?.exists() ?: false
                    android.util.Log.d("AuthRepoImpl", "User exists check for ID $id: $exists")
                    result.invoke(exists)
                } else {
                    android.util.Log.e("AuthRepoImpl", "Failed to check if user exists: ${task.exception?.message}")
                    result.invoke(false)
                }
            }.addOnFailureListener { exception ->
                android.util.Log.e("AuthRepoImpl", "Exception checking if user exists: ${exception.message}")
                result.invoke(false)
            }
    }
}