package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val username: String,
    val passwordHash: String,
    val phone: String,
    val email: String,
    val country: String = "Ghana (GH)",
    val packageName: String = "None",
    val packageCost: Double = 0.0,
    val dailyRate: Double = 0.0,
    val couponCode: String,
    val referralCode: String,
    val referredByCode: String? = null,
    val accountBalance: Double = 0.0,
    val dailyEarningsTotal: Double = 0.0,
    val referralEarningsTotal: Double = 0.0,
    val daysRemaining: Int = 30,
    val teamSize: Int = 0,
    val registrationTimestamp: Long = System.currentTimeMillis(),
    val lastEarningsTimestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // ACTIVE, SUSPENDED
    val isAdmin: Boolean = false
)

@Entity(tableName = "coupon_codes")
data class CouponEntity(
    @PrimaryKey val code: String,
    val status: String = "UNUSED", // UNUSED, USED, DEACTIVATED
    val usedByUsername: String? = null,
    val generatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "vendors")
data class VendorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val whatsapp: String,
    val couponPrice: String,
    val status: String = "ACTIVE"
)

@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val amount: Double,
    val paymentNumber: String = "0507861747",
    val accountName: String = "Maxwell",
    val paymentProofUri: String? = null,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val amount: Double,
    val withdrawalType: String, // INVESTMENT or REFERRAL
    val payoutPhone: String,
    val payoutMethod: String = "Mobile Money",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_tasks")
data class DailyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val platform: String, // Facebook, TikTok, Instagram, Twitter
    val rewardAmount: Double,
    val targetUrl: String,
    val instructions: String,
    val dateCreated: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_completions")
data class UserTaskCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val taskId: Int,
    val rewardEarned: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String, // username or "ALL"
    val title: String,
    val message: String,
    val type: String, // DEPOSIT, WITHDRAWAL, EARNINGS, ANNOUNCEMENT, TASK
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
