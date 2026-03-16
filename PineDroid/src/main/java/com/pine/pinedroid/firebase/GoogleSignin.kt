package com.pine.pinedroid.firebase


import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
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

//https://github.com/firebase/snippets-android/blob/391c1646eacf44d2aab3f76bcfa60dfc6c14acf1/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/GoogleSignInActivity.kt#L131-L145

object GoogleSignIn{
    var clientId = ""

    fun signOut() {
        sp("FirebaseUser", "null")
    }

    fun getSignedInUser(): FirebaseUser? = sp("FirebaseUser")

    fun isSignIn(): Boolean = (sp<FirebaseUser>("FirebaseUser") != null).also { logv("Google SignIn Statue", it) }

    suspend fun signIn(): FirebaseUser? {
        logv("Trigger Google Sign in")
        val user: FirebaseUser? = getSignedInUser()
        if (user != null) return user

        logv("Start Google Sign in")
        val credentialManager = CredentialManager.create(activityContext)

        // 优先用 GetSignInWithGoogleOption（兼容性更好，直接弹 Google 登录界面）
        // 失败则降级到 GetGoogleIdOption
        return try {
            val signInOption = GetSignInWithGoogleOption.Builder(clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext,
            )
            handleSignIn(result)
        } catch (e: Exception) {
            logv("GetSignInWithGoogleOption failed, fallback to GetGoogleIdOption", e)
            try {
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
                handleSignIn(result)
            } catch (e2: Exception) {
                loge("signin", e2)
                null
            }
        }
    }

    fun handleSignIn(result: GetCredentialResponse): FirebaseUser? {
        // Handle the successfully returned credential.
        val credential = result.credential


        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // 从 GoogleIdTokenCredential 中获取用户信息
                        val email = googleIdTokenCredential.id
                        val displayName = googleIdTokenCredential.displayName
                        val givenName = googleIdTokenCredential.givenName
                        val familyName = googleIdTokenCredential.familyName
                        val idToken = googleIdTokenCredential.idToken
                        val profilePictureUri = googleIdTokenCredential.profilePictureUri

                        logd(googleIdTokenCredential)


                        // 创建 User 对象
                        val user = FirebaseUser(
                            email = email,
                            displayName = displayName,
                            givenName = givenName,
                            familyName = familyName,
                            idToken = idToken,
                            profilePictureUrl = profilePictureUri?.toString()
                        )
                        sp("FirebaseUser", user)

                        return user

                    } catch (e: GoogleIdTokenParsingException) {
                        loge("Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized credential type here.
                    loge("Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                loge("Unexpected type of credential")
            }
        }
        return null
    }

}