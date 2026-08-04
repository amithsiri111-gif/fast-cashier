package com.example.domain.usecase

import com.example.data.CashierRepository

class SubmitWithdrawalUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(
        playerId: String,
        amountMinorUnits: Long,
        secretCode: String,
        bankName: String,
        accountHolder: String,
        accountNumber: String,
        branch: String
    ): Result<Long> {
        return repository.submitWithdrawal(playerId, amountMinorUnits, secretCode, bankName, accountHolder, accountNumber, branch)
    }
}
