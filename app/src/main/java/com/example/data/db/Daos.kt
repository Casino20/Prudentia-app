package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username")
    fun observeUserByUsername(username: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registrationTimestamp DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users WHERE referredByCode = :refCode")
    suspend fun getReferredCount(refCode: String): Int

    @Query("SELECT * FROM users WHERE referredByCode = :refCode")
    fun getReferredUsers(refCode: String): Flow<List<UserEntity>>
}

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupon_codes WHERE code = :code LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    @Query("SELECT * FROM coupon_codes ORDER BY generatedTimestamp DESC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupon_codes WHERE status = :status ORDER BY generatedTimestamp DESC")
    fun getCouponsByStatus(status: String): Flow<List<CouponEntity>>

    @Query("SELECT COUNT(*) FROM coupon_codes")
    suspend fun getTotalCouponCount(): Int

    @Query("SELECT COUNT(*) FROM coupon_codes WHERE status = 'UNUSED'")
    suspend fun getUnusedCouponCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCoupons(coupons: List<CouponEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Update
    suspend fun updateCoupon(coupon: CouponEntity)

    @Query("DELETE FROM coupon_codes WHERE code = :code")
    suspend fun deleteCoupon(code: String)

    @Query("DELETE FROM coupon_codes WHERE status = 'USED'")
    suspend fun deleteUsedCoupons()
}

@Dao
interface VendorDao {
    @Query("SELECT * FROM vendors WHERE status = 'ACTIVE' ORDER BY id ASC")
    fun getActiveVendors(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors ORDER BY id ASC")
    fun getAllVendors(): Flow<List<VendorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: VendorEntity)

    @Delete
    suspend fun deleteVendor(vendor: VendorEntity)
}

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits WHERE username = :username ORDER BY timestamp DESC")
    fun getDepositsForUser(username: String): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits ORDER BY timestamp DESC")
    fun getAllDeposits(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE id = :id LIMIT 1")
    suspend fun getDepositById(id: Int): DepositEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity)

    @Update
    suspend fun updateDeposit(deposit: DepositEntity)
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals WHERE username = :username ORDER BY timestamp DESC")
    fun getWithdrawalsForUser(username: String): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE id = :id LIMIT 1")
    suspend fun getWithdrawalById(id: Int): WithdrawalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalEntity)
}

@Dao
interface DailyTaskDao {
    @Query("SELECT * FROM daily_tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<DailyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DailyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<DailyTaskEntity>)

    @Query("SELECT * FROM task_completions WHERE username = :username")
    fun getCompletionsForUser(username: String): Flow<List<UserTaskCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: UserTaskCompletionEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE username = :username OR username = 'ALL' ORDER BY timestamp DESC")
    fun getNotificationsForUser(username: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE username = :username OR username = 'ALL'")
    suspend fun markAllAsRead(username: String)
}
