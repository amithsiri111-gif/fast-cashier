package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: String = "default_user"): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String = "default_user"): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE users SET language = :lang WHERE id = :id")
    suspend fun updateLanguage(lang: String, id: String = "default_user")

    @Query("UPDATE users SET savedBankName = :bank, savedAccountHolder = :holder, savedAccountNumber = :accNo, savedBranch = :branch WHERE id = :id")
    suspend fun updateSavedBank(bank: String, holder: String, accNo: String, branch: String, id: String = "default_user")

    @Query("SELECT COUNT(*) FROM users")
    fun getTotalUsersFlow(): Flow<Int>
}
