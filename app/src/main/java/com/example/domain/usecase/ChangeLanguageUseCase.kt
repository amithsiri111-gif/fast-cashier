package com.example.domain.usecase

import com.example.data.CashierRepository

class ChangeLanguageUseCase(private val repository: CashierRepository) {
    suspend operator fun invoke(lang: String) = repository.updateLanguage(lang)
}
