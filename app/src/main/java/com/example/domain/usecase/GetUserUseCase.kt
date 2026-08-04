package com.example.domain.usecase

import com.example.data.CashierRepository

class GetUserUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke() = repository.getUser()
}
