package com.example.data.repository

import com.example.data.local.dao.BankDao
import com.example.data.local.dao.DepositDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WithdrawalDao
import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val userDao: UserDao,
    private val depositDao: DepositDao,
    private val withdrawalDao: WithdrawalDao,
    private val bankDao: BankDao
) {
    val userFlow: Flow<UserEntity?> = userDao.getUserFlow()
    val activeBanksFlow: Flow<List<BankEntity>> = bankDao.getActiveBanksFlow()
    val allDepositsFlow: Flow<List<DepositEntity>> = depositDao.getAllDepositsFlow()
    val pendingDepositsFlow: Flow<List<DepositEntity>> = depositDao.getPendingDepositsFlow()
    val allWithdrawalsFlow: Flow<List<WithdrawalEntity>> = withdrawalDao.getAllWithdrawalsFlow()
    val pendingWithdrawalsFlow: Flow<List<WithdrawalEntity>> = withdrawalDao.getPendingWithdrawalsFlow()

    val pendingDepositsCount: Flow<Int> = depositDao.getPendingCountFlow()
    val pendingWithdrawalsCount: Flow<Int> = withdrawalDao.getPendingCountFlow()
    val totalUsersCount: Flow<Int> = userDao.getTotalUsersFlow()

    suspend fun getUser(): UserEntity? = userDao.getUser()
    suspend fun insertOrUpdateUser(user: UserEntity) = userDao.insertOrUpdate(user)
    suspend fun updateLanguage(lang: String) = userDao.updateLanguage(lang)
    suspend fun updateSavedBank(bankName: String, holder: String, accNo: String, branch: String) = userDao.updateSavedBank(bankName, holder, accNo, branch)

    suspend fun getDepositByReference(reference: String) = depositDao.getDepositByReference(reference)
    suspend fun insertDeposit(deposit: DepositEntity): Long = depositDao.insertDeposit(deposit)
    suspend fun updateDepositStatus(id: Long, status: String) = depositDao.updateStatus(id, status)

    suspend fun getPendingWithdrawal(): WithdrawalEntity? = withdrawalDao.getPendingWithdrawal()
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long = withdrawalDao.insertWithdrawal(withdrawal)
    suspend fun getWithdrawalById(id: Long): WithdrawalEntity? = withdrawalDao.getWithdrawalById(id)
    suspend fun updateWithdrawalStatus(id: Long, status: String, payoutRef: String? = null, reason: String? = null) = withdrawalDao.updateStatus(id, status, payoutRef = payoutRef, reason = reason)
}
