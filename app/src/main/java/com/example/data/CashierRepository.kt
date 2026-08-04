package com.example.data

import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.data.remote.SubmitDepositRequest
import com.example.data.remote.SubmitWithdrawalRequest
import com.example.data.repository.LocalDataSource
import com.example.data.repository.RemoteRepository
import kotlinx.coroutines.flow.Flow

class CashierRepository(
    private val localDataSource: LocalDataSource,
    private val remoteRepository: RemoteRepository
) {

    val userFlow: Flow<UserEntity?> = localDataSource.userFlow
    val activeBanksFlow: Flow<List<BankEntity>> = localDataSource.activeBanksFlow
    val allDepositsFlow: Flow<List<DepositEntity>> = localDataSource.allDepositsFlow
    val pendingDepositsFlow: Flow<List<DepositEntity>> = localDataSource.pendingDepositsFlow
    val allWithdrawalsFlow: Flow<List<WithdrawalEntity>> = localDataSource.allWithdrawalsFlow
    val pendingWithdrawalsFlow: Flow<List<WithdrawalEntity>> = localDataSource.pendingWithdrawalsFlow

    val pendingDepositsCount: Flow<Int> = localDataSource.pendingDepositsCount
    val pendingWithdrawalsCount: Flow<Int> = localDataSource.pendingWithdrawalsCount
    val totalUsersCount: Flow<Int> = localDataSource.totalUsersCount

    suspend fun getUser(): UserEntity {
        return localDataSource.getUser() ?: UserEntity().also { localDataSource.insertOrUpdateUser(it) }
    }

    suspend fun updateLanguage(lang: String) = localDataSource.updateLanguage(lang)

    suspend fun updatePlayerId(playerId: String) {
        val user = getUser()
        localDataSource.insertOrUpdateUser(user.copy(playerId = playerId))
    }

    suspend fun saveBankDetails(bankName: String, holder: String, accNo: String, branch: String) {
        localDataSource.updateSavedBank(bankName, holder, accNo, branch)
    }

    suspend fun clearSavedBank() {
        localDataSource.updateSavedBank("", "", "", "")
    }

    suspend fun submitDeposit(
        playerId: String,
        bankName: String,
        amountText: String,
        amountMinorUnits: Long,
        slipUri: String?,
        reference: String
    ): Result<Long> {
        if (!playerId.matches(Regex("^[0-9]{5,12}$"))) {
            return Result.failure(IllegalArgumentException("INVALID_PLAYER_ID"))
        }

        if (reference.isNotEmpty()) {
            val existing = localDataSource.getDepositByReference(reference)
            if (existing != null) {
                return Result.failure(IllegalStateException("DUPLICATE_SLIP:${existing.id}"))
            }
        }

        val generatedRef = if (reference.isEmpty()) "DEP-${System.currentTimeMillis().toString().takeLast(6)}" else reference

        val request = SubmitDepositRequest(
            playerId = playerId,
            bankName = bankName,
            amountMinorUnits = amountMinorUnits,
            slipUri = slipUri,
            reference = generatedRef
        )

        val response = try {
            remoteRepository.submitDeposit(request)
        } catch (error: Throwable) {
            return Result.failure(IllegalStateException("REMOTE_DEPOSIT_FAILED", error))
        }

        if (!response.success) {
            return Result.failure(IllegalStateException("REMOTE_DEPOSIT_FAILED"))
        }

        val deposit = DepositEntity(
            userJid = "user_${playerId}",
            playerId = playerId,
            bankName = bankName,
            amountText = amountText,
            amountMinorUnits = amountMinorUnits,
            slipUri = slipUri,
            status = "PENDING",
            reference = generatedRef
        )

        val id = localDataSource.insertDeposit(deposit)
        updatePlayerId(playerId)
        return Result.success(id)
    }

    suspend fun submitWithdrawal(
        playerId: String,
        amountMinorUnits: Long,
        secretCode: String,
        bankName: String,
        accountHolder: String,
        accountNumber: String,
        branch: String
    ): Result<Long> {
        if (!playerId.matches(Regex("^[0-9]{5,12}$"))) {
            return Result.failure(IllegalArgumentException("INVALID_PLAYER_ID"))
        }

        if (amountMinorUnits < 100_000 || amountMinorUnits > 50_000_000) {
            return Result.failure(IllegalArgumentException("INVALID_AMOUNT"))
        }

        val request = SubmitWithdrawalRequest(
            playerId = playerId,
            amountMinorUnits = amountMinorUnits,
            secretCode = secretCode,
            bankName = bankName,
            accountHolder = accountHolder,
            accountNumber = accountNumber,
            branch = branch
        )

        val response = try {
            remoteRepository.submitWithdrawal(request)
        } catch (error: Throwable) {
            return Result.failure(IllegalStateException("REMOTE_WITHDRAWAL_FAILED", error))
        }

        if (!response.success) {
            return Result.failure(IllegalStateException("REMOTE_WITHDRAWAL_FAILED"))
        }

        val withdrawal = WithdrawalEntity(
            userJid = "user_${playerId}",
            playerId = playerId,
            amountText = "LKR ${amountMinorUnits / 100}",
            amountMinorUnits = amountMinorUnits,
            secretCode = secretCode,
            bankName = bankName,
            accountHolder = accountHolder,
            accountNumber = accountNumber,
            branch = branch,
            status = "PENDING"
        )

        val id = localDataSource.insertWithdrawalIfNoPending(withdrawal)
        if (id == -1L) {
            val pendingExists = localDataSource.getPendingWithdrawal()
            val parts = pendingExists?.let { "${it.id}:${it.amountText}" } ?: ""
            return Result.failure(IllegalStateException("PENDING_WITHDRAWAL_EXISTS:$parts"))
        }

        saveBankDetails(bankName, accountHolder, accountNumber, branch)
        updatePlayerId(playerId)
        return Result.success(id)
    }

    suspend fun cancelWithdrawal(id: Long): Boolean {
        val w = localDataSource.getWithdrawalById(id)
        if (w != null && w.status == "PENDING") {
            localDataSource.updateWithdrawalStatus(id, "CANCELLED")
            return true
        }
        return false
    }

    suspend fun approveDeposit(id: Long) {
        localDataSource.updateDepositStatus(id, "APPROVED")
    }

    suspend fun rejectDeposit(id: Long) {
        localDataSource.updateDepositStatus(id, "REJECTED")
    }

    suspend fun approveWithdrawal(id: Long, payoutRef: String?) {
        val status = if (payoutRef.isNullOrBlank()) "APPROVED" else "COMPLETED"
        localDataSource.updateWithdrawalStatus(id, status, payoutRef = payoutRef)
    }

    suspend fun rejectWithdrawal(id: Long, reason: String?) {
        localDataSource.updateWithdrawalStatus(id, "REJECTED", reason = reason)
    }
}
