package com.pine.pinedroid.firebase


import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.log.logv
import com.pine.pinedroid.utils.sp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


object GoogleSignIn {
    var clientId = ""

    // Legacy GoogleSignInClient launcher
    private var signInLauncher: ActivityResultLauncher<Intent>? = null
    private var signInContinuation: ((FirebaseUser?) -> Unit)? = null

    /**
     * 在 Activity.onCreate 中调用，注册 ActivityResult 回调
     */
    fun registerLauncher(activity: ComponentActivity) {
        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            try {
                val account = com.google.android.gms.auth.api.signin.GoogleSignIn
                    .getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)
                val user = accountToFirebaseUser(account)
                sp("FirebaseUser", user)
                signInContinuation?.invoke(user)
            } catch (e: ApiException) {
                loge("Legacy Google Sign-In failed", e)
                signInContinuation?.invoke(null)
            }
            signInContinuation = null
        }
    }

    fun signOut() {
        sp("FirebaseUser", "null")
    }

    fun getSignedInUser(): FirebaseUser? = sp("FirebaseUser")

    fun isSignIn(): Boolean =
        (sp<FirebaseUser>("FirebaseUser") != null).also { logv("Google SignIn Statue", it) }

    suspend fun signIn(): FirebaseUser? {
        logv("Trigger Google Sign in")
        val user: FirebaseUser? = getSignedInUser()
        if (user != null) return user

        logv("Start Google Sign in")

        // 先尝试 Credential Manager
        val credentialResult = tryCredentialManager()
        if (credentialResult != null) return credentialResult

        // 降级到旧版 GoogleSignInClient
        logv("Credential Manager failed, fallback to legacy GoogleSignInClient")
        return tryLegacySignIn()
    }

    private suspend fun tryCredentialManager(): FirebaseUser? {
        return try {
            val credentialManager = CredentialManager.create(activityContext)
            val signInOption = GetSignInWithGoogleOption.Builder(clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext,
            )
            handleCredentialResponse(result)
        } catch (e: Exception) {
            logv("GetSignInWithGoogleOption failed", e)
            try {
                val credentialManager = CredentialManager.create(activityContext)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(
                    request = request,
                    context = activityContext,
                )
                handleCredentialResponse(result)
            } catch (e2: Exception) {
                logv("GetGoogleIdOption also failed", e2)
                null
            }
        }
    }

    private suspend fun tryLegacySignIn(): FirebaseUser? {
        val launcher = signInLauncher
        if (launcher == null) {
            loge("signInLauncher not registered, call GoogleSignIn.registerLauncher() in onCreate")
            return null
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()

        val client: GoogleSignInClient =
            com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(activityContext, gso)

        return suspendCancellableCoroutine { cont ->
            signInContinuation = { user -> cont.resume(user) }
            launcher.launch(client.signInIntent)
        }
    }

    private fun accountToFirebaseUser(account: GoogleSignInAccount): FirebaseUser {
        return FirebaseUser(
            email = account.email ?: "",
            displayName = account.displayName,
            givenName = account.givenName,
            familyName = account.familyName,
            idToken = account.idToken,
            profilePictureUrl = account.photoUrl?.toString()
        )
    }

    private fun handleCredentialResponse(result: GetCredentialResponse): FirebaseUser? {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val user = FirebaseUser(
                            email = googleIdTokenCredential.id,
                            displayName = googleIdTokenCredential.displayName,
                            givenName = googleIdTokenCredential.givenName,
                            familyName = googleIdTokenCredential.familyName,
                            idToken = googleIdTokenCredential.idToken,
                            profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        )
                        sp("FirebaseUser", user)
                        logd(googleIdTokenCredential)
                        return user
                    } catch (e: GoogleIdTokenParsingException) {
                        loge("Received an invalid google id token response", e)
                    }
                } else {
                    loge("Unexpected type of credential")
                }
            }
            else -> {
                loge("Unexpected type of credential")
            }
        }
        return null
    }
}
