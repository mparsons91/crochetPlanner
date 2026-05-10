package com.matthewparsons.hookline.domain.auth

/**
 * The current signed-in user as exposed to the rest of the app. This is a
 * deliberate slimming-down of Firebase's user model so the domain layer
 * doesn't depend on Firebase types.
 */
data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
)
