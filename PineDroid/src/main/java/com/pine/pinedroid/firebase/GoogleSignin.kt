package com.pine.pinedroid.firebase


import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logd
import com.pine.pinedroid.utils.log.loge
import com.pine.pinedroid.utils.sp

//https://github.com/firebase/snippets-android/blob/391c1646eacf44d2aab3f76bcfa60dfc6c14acf1/auth/app/src/main/java/com/google/firebase/quickstart/auth/kotlin/GoogleSignInActivity.kt#L131-L145

object GoogleSignIn{
    var clientId = ""

    fun signOut() {
        sp("FirebaseUser", "null")
    }

    fun getSignedInUser(): FirebaseUser? = sp("FirebaseUser")


    suspend fun signIn(): FirebaseUser? {
        val user: FirebaseUser? = getSignedInUser()
        if (user != null) return user

        // Instantiate a Google sign-in request
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(clientId)
            // Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()


        val credentialManager = CredentialManager.create(activityContext)



        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext,
            )
            handleSignIn(result)
            // 登录成功
        } catch (e: Exception) {
            loge("signin", e)
            null
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