package com.example.myapplicationlbf.Request

data class ResetPasswordRequest(
    val email: String?,
    val otp: String,
    val newPassword: String
)
