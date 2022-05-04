package com.kamilh.authorization

fun credentialsValidatorOf(accessTokenIsValid: Boolean = false): CredentialsValidator = CredentialsValidator {
    accessTokenIsValid
}