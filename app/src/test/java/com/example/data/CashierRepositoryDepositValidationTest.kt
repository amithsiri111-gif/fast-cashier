package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.DepositEntity
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
class CashierRepositoryDepositValidationTest {

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
    fun `submitDeposit with invalid player id returns invalid player id failure`() = runTest {
        val repository = buildRepository(FakeRemoteDataSource(SubmitDepositResponse(success = true, depositId = 1L)))

        val result = repository.submitDeposit(
            playerId = "12ab",
            bankName = "Bank of Ceylon",
            amountText = "LKR 1,000",
            amountMinorUnits = 100000,
            slipUri = null,
            reference = "REF001"
        )

        assertTrue(result.isFailure)
        assertEquals("INVALID_PLAYER_ID", result.exceptionOrNull()?.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitDeposit with duplicate reference returns duplicate slip failure`() = runTest {
        // Seed an existing deposit with the duplicate reference.
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitDeposit with remote failure returns remote deposit failed failure`() = runTest {
        val repository = buildRepository(FakeRemoteDataSource(SubmitDepositResponse(success = false, depositId = null)))

        val result = repository.submitDeposit(
            playerId = "123456",
            bankName = "Bank of Ceylon",
            amountText = "LKR 1,000",
            amountMinorUnits = 100000,
            slipUri = null,
            reference = "REF002"
        )

        assertTrue(result.isFailure)
        assertEquals("REMOTE_DEPOSIT_FAILED", result.exceptionOrNull()?.message)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `submitDeposit with valid input inserts deposit and returns id`() = runTest {
        val repository = buildRepository(FakeRemoteDataSource(SubmitDepositResponse(success = true, depositId = 42L)))

        val result = repository.submitDeposit(
            playerId = "123456",
            bankName = "Bank of Ceylon",
            amountText = "LKR 1,000",
            amountMinorUnits = 100000,
            slipUri = null,
            reference = ""
        )

        assertTrue(result.isSuccess)
        val insertedId = result.getOrNull()
        assertTrue(insertedId != null && insertedId > 0)
    }

    private fun buildRepository(remoteDataSource: RemoteDataSource): CashierRepository {
        return CashierRepository(localDataSource, RemoteRepository(remoteDataSource))
    }

    private class FakeRemoteDataSource(
        private val response: SubmitDepositResponse
    ) : RemoteDataSource {
        override suspend fun submitDeposit(request: SubmitDepositRequest): SubmitDepositResponse {
            return response
        }

        override suspend fun submitWithdrawal(request: SubmitWithdrawalRequest): SubmitWithdrawalResponse {
            throw UnsupportedOperationException("submitWithdrawal is not used in this test")
        }
    }
}
