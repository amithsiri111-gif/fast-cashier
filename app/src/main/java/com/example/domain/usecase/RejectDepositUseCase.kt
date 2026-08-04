package com.example.domain.usecase

import com.example.data.CashierRepository

class RejectDepositUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(id: Long) = repository.rejectDeposit(id)
}
