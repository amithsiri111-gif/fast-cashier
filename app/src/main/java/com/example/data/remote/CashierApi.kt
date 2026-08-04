package com.example.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface CashierApi {
    @POST("deposit")
    suspend fun submitDeposit(@Body request: SubmitDepositRequest): SubmitDepositResponse

    @POST("withdrawal")
    suspend fun submitWithdrawal(@Body request: SubmitWithdrawalRequest): SubmitWithdrawalResponse
}

data class SubmitDepositRequest(
    val playerId: String,
    val bankName: String,
    val amountMinorUnits: Long,
    val slipUri: String?,
    val reference: String
)

data class SubmitDepositResponse(
    val success: Boolean,
    val depositId: Long?
)

data class SubmitWithdrawalRequest(
    val playerId: String,
    val amountMinorUnits: Long,
    val secretCode: String,
    val bankName: String,
    val accountHolder: String,
    val accountNumber: String,
    val branch: String
)

data class SubmitWithdrawalResponse(
    val success: Boolean,
    val withdrawalId: Long?
)
