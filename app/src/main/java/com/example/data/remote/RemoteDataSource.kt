package com.example.data.remote

interface RemoteDataSource {
    suspend fun submitDeposit(request: SubmitDepositRequest): SubmitDepositResponse
    suspend fun submitWithdrawal(request: SubmitWithdrawalRequest): SubmitWithdrawalResponse
}
