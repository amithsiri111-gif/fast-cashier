package com.example.domain.usecase

import com.example.data.CashierRepository
import com.example.data.local.entity.DepositEntity

class SubmitDepositUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(
        playerId: String,
        bankName: String,
        amountText: String,
        amountMinorUnits: Long,
        slipUri: String?,
        reference: String
    ): Result<Long> {
        return repository.submitDeposit(playerId, bankName, amountText, amountMinorUnits, slipUri, reference)
    }
}
