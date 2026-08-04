package com.example.domain.usecase

import com.example.data.CashierRepository

class ApproveWithdrawalUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(id: Long, payoutRef: String?) = repository.approveWithdrawal(id, payoutRef)
}
