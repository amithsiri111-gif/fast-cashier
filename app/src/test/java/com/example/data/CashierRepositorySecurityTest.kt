package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.data.remote.RemoteDataSource
import com.example.data.remote.SubmitDepositRequest
import com.example.data.remote.SubmitDepositResponse
import com.example.data.remote.SubmitWithdrawalRequest
import com.example.data.remote.SubmitWithdrawalResponse
import com.example.data.repository.LocalDataSource
import com.example.data.repository.RemoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CashierRepositorySecurityTest {

    private lateinit var db: AppDatabase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = LocalDataSource(
            db.userDao(),
            db.depositDao(),
            db.withdrawalDao(),
            db.bankDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitWithdrawal prevents second pending withdrawal`() = runTest {
        val repository = buildRepository(FakeRemoteDataSource(SubmitWithdrawalResponse(success = true, withdrawalId = 1L)))

        val first = repository.submitWithdrawal(
            playerId = "123456",
            amountMinorUnits = 200_000,
            secretCode = "1234",
            bankName = "Bank of Ceylon",
            accountHolder = "Test User",
            accountNumber = "0001234567",
            branch = "Head Office"
        )

        assertTrue(first.isSuccess)

        val second = repository.submitWithdrawal(
            playerId = "123456",
            amountMinorUnits = 200_000,
            secretCode = "1234",
            bankName = "Bank of Ceylon",
            accountHolder = "Test User",
            accountNumber = "0001234567",
            branch = "Head Office"
        )

        assertTrue(second.isFailure)
        assertTrue(second.exceptionOrNull()?.message?.startsWith("PENDING_WITHDRAWAL_EXISTS") == true)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitDeposit duplicate reference fails`() = runTest {
        localDataSource.insertDeposit(
            DepositEntity(
                playerId = "123456",
                bankName = "Bank of Ceylon",
                amountText = "LKR 1,000",
                amountMinorUnits = 100000,
                reference = "DUPLICATE_REF"
            )
        )

        val repository = buildRepository(FakeRemoteDataSource(SubmitDepositResponse(success = true, depositId = 2L)))

        val result = repository.submitDeposit(
            playerId = "123456",
            bankName = "Bank of Ceylon",
            amountText = "LKR 1,000",
            amountMinorUnits = 100000,
            slipUri = null,
            reference = "DUPLICATE_REF"
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.startsWith("DUPLICATE_SLIP") == true)
    }

    private fun buildRepository(remoteDataSource: RemoteDataSource): CashierRepository {
        return CashierRepository(localDataSource, RemoteRepository(remoteDataSource))
    }

    private class FakeRemoteDataSource(
        private val depositResponse: SubmitDepositResponse? = null,
        private val withdrawalResponse: SubmitWithdrawalResponse? = null
    ) : RemoteDataSource {
        override suspend fun submitDeposit(request: SubmitDepositRequest): SubmitDepositResponse {
            return depositResponse ?: throw UnsupportedOperationException("submitDeposit is not configured")
        }

        override suspend fun submitWithdrawal(request: SubmitWithdrawalRequest): SubmitWithdrawalResponse {
            return withdrawalResponse ?: throw UnsupportedOperationException("submitWithdrawal is not configured")
        }
    }
}
