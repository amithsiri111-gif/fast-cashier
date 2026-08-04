package com.example.di

import android.content.Context
import com.example.data.CashierRepository
import com.example.data.local.AppDatabase
import com.example.data.local.dao.BankDao
import com.example.data.local.dao.DepositDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WithdrawalDao
import com.example.data.remote.AuthInterceptor
import com.example.data.remote.CashierApi
import com.example.data.remote.CashierRemoteDataSource
import com.example.data.remote.RemoteDataSource
import com.example.data.repository.LocalDataSource
import com.example.data.repository.RemoteRepository
import com.example.domain.usecase.ApproveDepositUseCase
import com.example.domain.usecase.ApproveWithdrawalUseCase
import com.example.domain.usecase.CancelWithdrawalUseCase
import com.example.domain.usecase.ChangeLanguageUseCase
import com.example.domain.usecase.ClearSavedBankUseCase
import com.example.domain.usecase.GetUserUseCase
import com.example.domain.usecase.RejectDepositUseCase
import com.example.domain.usecase.RejectWithdrawalUseCase
import com.example.domain.usecase.SubmitDepositUseCase
import com.example.domain.usecase.SubmitWithdrawalUseCase
import com.example.domain.usecase.UpdatePlayerIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.data.remote.DefaultTokenProvider
import com.example.data.remote.TokenProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideDepositDao(db: AppDatabase): DepositDao = db.depositDao()

    @Provides
    @Singleton
    fun provideWithdrawalDao(db: AppDatabase): WithdrawalDao = db.withdrawalDao()

    @Provides
    @Singleton
    fun provideBankDao(db: AppDatabase): BankDao = db.bankDao()

    @Provides
    @Singleton
    fun provideTokenProvider(): TokenProvider = DefaultTokenProvider

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenProvider: TokenProvider): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()
    }

    @Provides
    @Singleton
    fun provideCashierApi(client: OkHttpClient): CashierApi {
        return Retrofit.Builder()
            .baseUrl("https://api.fastxbet.example.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CashierApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(api: CashierApi): RemoteDataSource = CashierRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideRemoteRepository(remoteDataSource: RemoteDataSource): RemoteRepository = RemoteRepository(remoteDataSource)

    @Provides
    @Singleton
    fun provideLocalDataSource(
        userDao: UserDao,
        depositDao: DepositDao,
        withdrawalDao: WithdrawalDao,
        bankDao: BankDao
    ): LocalDataSource = LocalDataSource(userDao, depositDao, withdrawalDao, bankDao)

    @Provides
    @Singleton
    fun provideRepository(
        localDataSource: LocalDataSource,
        remoteRepository: RemoteRepository
    ): CashierRepository {
        return CashierRepository(localDataSource, remoteRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserUseCase(repository: CashierRepository): GetUserUseCase = GetUserUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePlayerIdUseCase(repository: CashierRepository): UpdatePlayerIdUseCase = UpdatePlayerIdUseCase(repository)

    @Provides
    @Singleton
    fun provideChangeLanguageUseCase(repository: CashierRepository): ChangeLanguageUseCase = ChangeLanguageUseCase(repository)

    @Provides
    @Singleton
    fun provideSubmitDepositUseCase(repository: CashierRepository): SubmitDepositUseCase = SubmitDepositUseCase(repository)

    @Provides
    @Singleton
    fun provideSubmitWithdrawalUseCase(repository: CashierRepository): SubmitWithdrawalUseCase = SubmitWithdrawalUseCase(repository)

    @Provides
    @Singleton
    fun provideCancelWithdrawalUseCase(repository: CashierRepository): CancelWithdrawalUseCase = CancelWithdrawalUseCase(repository)

    @Provides
    @Singleton
    fun provideApproveDepositUseCase(repository: CashierRepository): ApproveDepositUseCase = ApproveDepositUseCase(repository)

    @Provides
    @Singleton
    fun provideRejectDepositUseCase(repository: CashierRepository): RejectDepositUseCase = RejectDepositUseCase(repository)

    @Provides
    @Singleton
    fun provideApproveWithdrawalUseCase(repository: CashierRepository): ApproveWithdrawalUseCase = ApproveWithdrawalUseCase(repository)

    @Provides
    @Singleton
    fun provideRejectWithdrawalUseCase(repository: CashierRepository): RejectWithdrawalUseCase = RejectWithdrawalUseCase(repository)

    @Provides
    @Singleton
    fun provideClearSavedBankUseCase(repository: CashierRepository): ClearSavedBankUseCase = ClearSavedBankUseCase(repository)
}
