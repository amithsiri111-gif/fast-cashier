package com.example.domain.usecase

import com.example.data.CashierRepository

class UpdatePlayerIdUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(playerId: String) = repository.updatePlayerId(playerId)
}
