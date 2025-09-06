package com.pine.pinedroid.firebase

data class FirebaseUser(
    val email: String,
    val displayName: String? = null,
    val givenName: String? = null,
    val familyName: String? = null,
    val idToken: String? = null,
    val profilePictureUrl: String? = null
) {
    // 可选：添加一些辅助方法
    fun getFullName(): String {
        return if (!givenName.isNullOrEmpty() && !familyName.isNullOrEmpty()) {
            "$givenName $familyName"
        } else {
            displayName ?: ""
        }
    }

    fun hasProfilePicture(): Boolean {
        return !profilePictureUrl.isNullOrEmpty()
    }
}