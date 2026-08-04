package com.example.domain.usecase

import com.example.data.CashierRepository

class RejectWithdrawalUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(id: Long, reason: String?) = repository.rejectWithdrawal(id, reason)
}
