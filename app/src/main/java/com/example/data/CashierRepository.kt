package com.example.data

import com.example.data.local.AppDatabase
import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

class CashierRepository(private val db: AppDatabase) {

    val userFlow: Flow<UserEntity?> = db.userDao().getUserFlow()
    val activeBanksFlow: Flow<List<BankEntity>> = db.bankDao().getActiveBanksFlow()
    val allDepositsFlow: Flow<List<DepositEntity>> = db.depositDao().getAllDepositsFlow()
    val pendingDepositsFlow: Flow<List<DepositEntity>> = db.depositDao().getPendingDepositsFlow()
    val allWithdrawalsFlow: Flow<List<WithdrawalEntity>> = db.withdrawalDao().getAllWithdrawalsFlow()
    val pendingWithdrawalsFlow: Flow<List<WithdrawalEntity>> = db.withdrawalDao().getPendingWithdrawalsFlow()

    val pendingDepositsCount: Flow<Int> = db.depositDao().getPendingCountFlow()
    val pendingWithdrawalsCount: Flow<Int> = db.withdrawalDao().getPendingCountFlow()
    val totalUsersCount: Flow<Int> = db.userDao().getTotalUsersFlow()

    suspend fun getUser(): UserEntity {
        return db.userDao().getUser() ?: UserEntity().also { db.userDao().insertOrUpdate(it) }
    }

    suspend fun updateLanguage(lang: String) {
        db.userDao().updateLanguage(lang)
    }

    suspend fun updatePlayerId(playerId: String) {
        val user = getUser()
        db.userDao().insertOrUpdate(user.copy(playerId = playerId))
    }

    suspend fun saveBankDetails(bankName: String, holder: String, accNo: String, branch: String) {
        db.userDao().updateSavedBank(bankName, holder, accNo, branch)
    }

    suspend fun clearSavedBank() {
        db.userDao().updateSavedBank("", "", "", "")
    }

    // Validation & Submission for Deposit
    suspend fun submitDeposit(
        playerId: String,
        bankName: String,
        amountText: String,
        amount: Double,
        slipUri: String?,
        reference: String
    ): Result<Long> {
        // Validate Player ID (5-12 digits)
        if (!playerId.matches(Regex("^[0-9]{5,12}$"))) {
            return Result.failure(IllegalArgumentException("INVALID_PLAYER_ID"))
        }

        // Duplicate reference check
        if (reference.isNotEmpty()) {
            val existing = db.depositDao().getDepositByReference(reference)
            if (existing != null) {
                return Result.failure(IllegalStateException("DUPLICATE_SLIP:${existing.id}"))
            }
        }

        val generatedRef = if (reference.isEmpty()) "DEP-${System.currentTimeMillis().toString().takeLast(6)}" else reference

        val deposit = DepositEntity(
            userJid = "user_${playerId}",
            playerId = playerId,
            bankName = bankName,
            amountText = amountText,
            amount = amount,
            slipUri = slipUri,
            status = "PENDING",
            reference = generatedRef
        )

        val id = db.depositDao().insertDeposit(deposit)
        updatePlayerId(playerId)
        return Result.success(id)
    }

    // Validation & Submission for Withdrawal
    suspend fun submitWithdrawal(
        playerId: String,
        amount: Double,
        secretCode: String,
        bankName: String,
        accountHolder: String,
        accountNumber: String,
        branch: String
    ): Result<Long> {
        // Player ID validation
        if (!playerId.matches(Regex("^[0-9]{5,12}$"))) {
            return Result.failure(IllegalArgumentException("INVALID_PLAYER_ID"))
        }

        // Amount limits: 1000 - 500,000 LKR
        if (amount < 1000.0 || amount > 500000.0) {
            return Result.failure(IllegalArgumentException("INVALID_AMOUNT"))
        }

        // Check if pending withdrawal exists
        val pendingExists = db.withdrawalDao().getPendingWithdrawal()
        if (pendingExists != null) {
            return Result.failure(IllegalStateException("PENDING_WITHDRAWAL_EXISTS:${pendingExists.id}:${pendingExists.amountText}"))
        }

        val withdrawal = WithdrawalEntity(
            userJid = "user_${playerId}",
            playerId = playerId,
            amountText = "LKR ${amount.toInt()}",
            amount = amount,
            secretCode = secretCode,
            bankName = bankName,
            accountHolder = accountHolder,
            accountNumber = accountNumber,
            branch = branch,
            status = "PENDING"
        )

        val id = db.withdrawalDao().insertWithdrawal(withdrawal)

        // Save bank details for auto-fill in future
        saveBankDetails(bankName, accountHolder, accountNumber, branch)
        updatePlayerId(playerId)

        return Result.success(id)
    }

    suspend fun cancelWithdrawal(id: Long): Boolean {
        val w = db.withdrawalDao().getWithdrawalById(id)
        if (w != null && w.status == "PENDING") {
            db.withdrawalDao().updateStatus(id, "CANCELLED")
            return true
        }
        return false
    }

    // Admin Actions
    suspend fun approveDeposit(id: Long) {
        db.depositDao().updateStatus(id, "APPROVED")
    }

    suspend fun rejectDeposit(id: Long) {
        db.depositDao().updateStatus(id, "REJECTED")
    }

    suspend fun approveWithdrawal(id: Long, payoutRef: String?) {
        val status = if (payoutRef.isNullOrBlank()) "APPROVED" else "COMPLETED"
        db.withdrawalDao().updateStatus(id, status, payoutRef = payoutRef)
    }

    suspend fun rejectWithdrawal(id: Long, reason: String?) {
        db.withdrawalDao().updateStatus(id, "REJECTED", reason = reason)
    }
}
