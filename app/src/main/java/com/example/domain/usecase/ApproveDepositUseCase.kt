package com.example.domain.usecase

import com.example.data.CashierRepository

class ApproveDepositUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(id: Long) = repository.approveDeposit(id)
}
