package com.example.domain.usecase

import com.example.data.CashierRepository

class ClearSavedBankUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke() = repository.clearSavedBank()
}
