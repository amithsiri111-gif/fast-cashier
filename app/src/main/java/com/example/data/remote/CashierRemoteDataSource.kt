package com.example.data.remote

class CashierRemoteDataSource(private val api: CashierApi) : RemoteDataSource {
    override suspend fun submitDeposit(request: SubmitDepositRequest): SubmitDepositResponse {
        return api.submitDeposit(request)
    }

    override suspend fun submitWithdrawal(request: SubmitWithdrawalRequest): SubmitWithdrawalResponse {
        return api.submitWithdrawal(request)
    }
}
