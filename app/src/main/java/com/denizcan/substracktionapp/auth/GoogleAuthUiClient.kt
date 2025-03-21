package com.denizcan.substracktionapp.auth

import android.content.Context
import android.content.Intent
import com.denizcan.substracktionapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val webClientId = context.getString(R.string.web_client_id)

    fun getSignInIntent(): Intent {
        auth.signOut()
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        return try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(intent).await()
            val googleCredential = GoogleAuthProvider.getCredential(account.idToken, null)
            val user = auth.signInWithCredential(googleCredential).await().user

            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        email = email
                    )
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?
) 