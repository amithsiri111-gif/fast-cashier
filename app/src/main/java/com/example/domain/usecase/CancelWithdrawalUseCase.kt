package com.example.domain.usecase

import com.example.data.CashierRepository

class CancelWithdrawalUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(id: Long): Boolean = repository.cancelWithdrawal(id)
}
