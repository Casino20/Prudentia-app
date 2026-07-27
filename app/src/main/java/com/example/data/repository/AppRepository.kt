package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

data class PackageInfo(
    val name: String,
    val price: Double,
    val dailyRate: Double,
    val refBonus: Double,
    val durationDays: Int = 30
)

object PlanConfig {
    val STARTER = PackageInfo("Starter", 120.0, 10.0, 60.0)
    val STANDARD = PackageInfo("Standard", 220.0, 16.0, 90.0)
    val PREMIUM = PackageInfo("Premium", 300.0, 22.0, 130.0)

    fun getPlan(name: String): PackageInfo {
        return when (name.lowercase()) {
            "starter" -> STARTER
            "standard" -> STANDARD
            "premium" -> PREMIUM
            else -> STARTER
        }
    }
}

class AppRepository(private val db: AppDatabase) {

    val userDao = db.userDao()
    val couponDao = db.couponDao()
    val vendorDao = db.vendorDao()
    val depositDao = db.depositDao()
    val withdrawalDao = db.withdrawalDao()
    val dailyTaskDao = db.dailyTaskDao()
    val notificationDao = db.notificationDao()

    fun observeUser(username: String): Flow<UserEntity?> = userDao.observeUserByUsername(username)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    fun getReferredUsers(refCode: String): Flow<List<UserEntity>> = userDao.getReferredUsers(refCode)

    fun getAllCoupons(): Flow<List<CouponEntity>> = couponDao.getAllCoupons()
    fun getCouponsByStatus(status: String): Flow<List<CouponEntity>> = couponDao.getCouponsByStatus(status)

    fun getActiveVendors(): Flow<List<VendorEntity>> = vendorDao.getActiveVendors()
    fun getAllVendors(): Flow<List<VendorEntity>> = vendorDao.getAllVendors()

    fun getDepositsForUser(username: String): Flow<List<DepositEntity>> = depositDao.getDepositsForUser(username)
    fun getAllDeposits(): Flow<List<DepositEntity>> = depositDao.getAllDeposits()

    fun getWithdrawalsForUser(username: String): Flow<List<WithdrawalEntity>> = withdrawalDao.getWithdrawalsForUser(username)
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>> = withdrawalDao.getAllWithdrawals()

    fun getAllTasks(): Flow<List<DailyTaskEntity>> = dailyTaskDao.getAllTasks()
    fun getTaskCompletions(username: String): Flow<List<UserTaskCompletionEntity>> = dailyTaskDao.getCompletionsForUser(username)

    fun getNotifications(username: String): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForUser(username)

    // Check withdrawal allowed days
    fun isInvestmentWithdrawalDay(overrideForTest: Boolean = false): Boolean {
        if (overrideForTest) return true
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

    fun isReferralWithdrawalDay(overrideForTest: Boolean = false): Boolean {
        if (overrideForTest) return true
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
    }

    // Register user
    suspend fun registerUser(
        fullName: String,
        username: String,
        password: String,
        phone: String,
        email: String,
        country: String,
        packageName: String,
        couponCodeInput: String,
        referralCodeInput: String?
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().lowercase()
        val cleanCoupon = couponCodeInput.trim().uppercase()

        // 1. Check existing username
        val existing = userDao.getUserByUsername(cleanUsername)
        if (existing != null) {
            return@withContext Result.failure(Exception("Username '$cleanUsername' is already taken."))
        }

        // 2. Validate Coupon Code (MANDATORY REQUIREMENT!)
        val coupon = couponDao.getCouponByCode(cleanCoupon)
        if (coupon == null) {
            return@withContext Result.failure(Exception("Invalid Coupon Code. Please contact a vendor to purchase a valid coupon code."))
        }
        if (coupon.status != "UNUSED") {
            return@withContext Result.failure(Exception("Coupon Code '$cleanCoupon' has already been used or deactivated."))
        }

        // Plan details
        val plan = PlanConfig.getPlan(packageName)
        val userRefCode = "PRUDENTI-${cleanUsername.uppercase()}-${(100..999).random()}"

        // Create user entity
        val newUser = UserEntity(
            fullName = fullName.trim(),
            username = cleanUsername,
            passwordHash = password,
            phone = phone.trim(),
            email = email.trim(),
            country = country,
            packageName = plan.name,
            packageCost = plan.price,
            dailyRate = plan.dailyRate,
            couponCode = cleanCoupon,
            referralCode = userRefCode,
            referredByCode = referralCodeInput?.trim()?.ifEmpty { null },
            accountBalance = 0.0,
            dailyEarningsTotal = 0.0,
            referralEarningsTotal = 0.0,
            daysRemaining = plan.durationDays,
            teamSize = 0,
            registrationTimestamp = System.currentTimeMillis(),
            lastEarningsTimestamp = System.currentTimeMillis(),
            status = "ACTIVE"
        )

        val insertedId = userDao.insertUser(newUser)

        // Mark coupon as USED
        couponDao.updateCoupon(coupon.copy(status = "USED", usedByUsername = cleanUsername))

        // Check Referral Code logic
        if (!referralCodeInput.isNullOrBlank()) {
            val referrer = userDao.getUserByReferralCode(referralCodeInput.trim())
            if (referrer != null) {
                val bonus = plan.refBonus
                val updatedReferrer = referrer.copy(
                    accountBalance = referrer.accountBalance + bonus,
                    referralEarningsTotal = referrer.referralEarningsTotal + bonus,
                    teamSize = referrer.teamSize + 1
                )
                userDao.updateUser(updatedReferrer)

                // Send notification to referrer
                notificationDao.insertNotification(
                    NotificationEntity(
                        username = referrer.username,
                        title = "Referral Bonus Credited!",
                        message = "You earned GH₵ ${String.format("%.2f", bonus)} from $cleanUsername's registration on ${plan.name} plan!",
                        type = "EARNINGS"
                    )
                )
            }
        }

        // Send Welcome notification to new user
        notificationDao.insertNotification(
            NotificationEntity(
                username = cleanUsername,
                title = "Account Activated!",
                message = "Welcome to ${plan.name} Plan! Your daily earning rate is GH₵ ${plan.dailyRate}.",
                type = "EARNINGS"
            )
        )

        val createdUser = userDao.getUserByUsername(cleanUsername)!!
        Result.success(createdUser)
    }

    private fun String?.isNullFrancoOrEmpty(): Boolean = this == null || this.trim().isEmpty()

    // Login
    suspend fun loginUser(usernameInput: String, passwordInput: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanUsername = usernameInput.trim().lowercase()
        val user = userDao.getUserByUsername(cleanUsername)
        if (user == null) {
            return@withContext Result.failure(Exception("Account not found. Please register."))
        }
        if (user.passwordHash != passwordInput) {
            return@withContext Result.failure(Exception("Invalid password. Please check your credentials."))
        }
        if (user.status == "SUSPENDED") {
            return@withContext Result.failure(Exception("Your account has been suspended by administration."))
        }
        Result.success(user)
    }

    // Trigger daily earnings credit for active user
    suspend fun checkAndCreditDailyEarnings(username: String) = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext
        if (user.packageName == "None" || user.daysRemaining <= 0 || user.dailyRate <= 0) return@withContext

        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        val elapsedDays = ((now - user.lastEarningsTimestamp) / oneDayMs).toInt()

        if (elapsedDays >= 1) {
            val totalCreditDays = minOf(elapsedDays, user.daysRemaining)
            val creditAmount = totalCreditDays * user.dailyRate

            val updatedUser = user.copy(
                accountBalance = user.accountBalance + creditAmount,
                dailyEarningsTotal = user.dailyEarningsTotal + creditAmount,
                daysRemaining = user.daysRemaining - totalCreditDays,
                lastEarningsTimestamp = now
            )
            userDao.updateUser(updatedUser)

            notificationDao.insertNotification(
                NotificationEntity(
                    username = user.username,
                    title = "Daily Earnings Credited!",
                    message = "GH₵ ${String.format("%.2f", creditAmount)} has been credited to your balance for $totalCreditDays day(s).",
                    type = "EARNINGS"
                )
            )
        }
    }

    // Force claim daily earnings (Manual trigger for demo/testing)
    suspend fun claimDailyEarningsManual(username: String): Result<Double> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username)
            ?: return@withContext Result.failure(Exception("User not found"))
        if (user.daysRemaining <= 0) {
            return@withContext Result.failure(Exception("Your investment plan period has expired."))
        }
        val rate = user.dailyRate
        if (rate <= 0) {
            return@withContext Result.failure(Exception("No active daily rate."))
        }

        val updatedUser = user.copy(
            accountBalance = user.accountBalance + rate,
            dailyEarningsTotal = user.dailyEarningsTotal + rate,
            daysRemaining = user.daysRemaining - 1,
            lastEarningsTimestamp = System.currentTimeMillis()
        )
        userDao.updateUser(updatedUser)

        notificationDao.insertNotification(
            NotificationEntity(
                username = user.username,
                title = "Daily Earnings Collected!",
                message = "GH₵ ${String.format("%.2f", rate)} added to your balance.",
                type = "EARNINGS"
            )
        )
        Result.success(rate)
    }

    // Deposit submission
    suspend fun submitDeposit(username: String, amount: Double, proofUri: String?): Result<DepositEntity> = withContext(Dispatchers.IO) {
        if (amount <= 0) return@withContext Result.failure(Exception("Please enter a valid deposit amount"))
        val deposit = DepositEntity(
            username = username,
            amount = amount,
            paymentProofUri = proofUri,
            status = "PENDING"
        )
        depositDao.insertDeposit(deposit)
        notificationDao.insertNotification(
            NotificationEntity(
                username = username,
                title = "Deposit Submitted",
                message = "Your deposit of GH₵ ${String.format("%.2f", amount)} is pending admin verification.",
                type = "DEPOSIT"
            )
        )
        Result.success(deposit)
    }

    // Withdrawal submission
    suspend fun submitWithdrawal(
        username: String,
        amount: Double,
        withdrawalType: String, // INVESTMENT or REFERRAL
        payoutPhone: String,
        overrideDayCheck: Boolean = false
    ): Result<WithdrawalEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username)
            ?: return@withContext Result.failure(Exception("User not found"))

        if (amount < 20.0) {
            return@withContext Result.failure(Exception("Minimum withdrawal amount is GH₵ 20.00"))
        }

        if (withdrawalType == "INVESTMENT") {
            if (!isInvestmentWithdrawalDay(overrideDayCheck)) {
                return@withContext Result.failure(Exception("Investment earnings can ONLY be requested for withdrawal on Sundays!"))
            }
        } else if (withdrawalType == "REFERRAL") {
            if (!isReferralWithdrawalDay(overrideDayCheck)) {
                return@withContext Result.failure(Exception("Referral commissions can ONLY be requested for withdrawal on Fridays!"))
            }
        }

        if (user.accountBalance < amount) {
            return@withContext Result.failure(Exception("Insufficient account balance. Available: GH₵ ${String.format("%.2f", user.accountBalance)}"))
        }

        // Create pending withdrawal
        val withdrawal = WithdrawalEntity(
            username = username,
            amount = amount,
            withdrawalType = withdrawalType,
            payoutPhone = payoutPhone,
            status = "PENDING"
        )
        withdrawalDao.insertWithdrawal(withdrawal)

        // Deduct pending balance to lock funds
        userDao.updateUser(user.copy(accountBalance = user.accountBalance - amount))

        notificationDao.insertNotification(
            NotificationEntity(
                username = username,
                title = "Withdrawal Requested",
                message = "Your $withdrawalType withdrawal request of GH₵ ${String.format("%.2f", amount)} has been logged for admin approval.",
                type = "WITHDRAWAL"
            )
        )

        Result.success(withdrawal)
    }

    // Complete Daily Task
    suspend fun completeTask(username: String, task: DailyTaskEntity): Result<Double> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found"))
        val completion = UserTaskCompletionEntity(
            username = username,
            taskId = task.id,
            rewardEarned = task.rewardAmount
        )
        dailyTaskDao.insertCompletion(completion)

        userDao.updateUser(user.copy(accountBalance = user.accountBalance + task.rewardAmount))

        notificationDao.insertNotification(
            NotificationEntity(
                username = username,
                title = "Task Completed!",
                message = "You earned GH₵ ${String.format("%.2f", task.rewardAmount)} for completing '${task.title}'.",
                type = "TASK"
            )
        )
        Result.success(task.rewardAmount)
    }

    // --- ADMIN CONTROLS ---

    // Admin batch generate coupons (1000 to 2000)
    suspend fun generateBatchCoupons(count: Int): Result<Int> = withContext(Dispatchers.IO) {
        val list = mutableListOf<CouponEntity>()
        for (i in 1..count) {
            val code = "PRUDENTI-${(1000..9999).random()}-${(1000..9999).random()}-${(100..999).random()}"
            list.add(CouponEntity(code = code, status = "UNUSED"))
        }
        couponDao.insertCoupons(list)
        Result.success(count)
    }

    suspend fun approveDeposit(depositId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val deposit = depositDao.getDepositById(depositId) ?: return@withContext Result.failure(Exception("Deposit not found"))
        if (deposit.status != "PENDING") return@withContext Result.failure(Exception("Deposit already processed"))

        depositDao.updateDeposit(deposit.copy(status = "APPROVED"))

        val user = userDao.getUserByUsername(deposit.username)
        if (user != null) {
            userDao.updateUser(user.copy(accountBalance = user.accountBalance + deposit.amount))
            notificationDao.insertNotification(
                NotificationEntity(
                    username = user.username,
                    title = "Deposit Approved!",
                    message = "Your deposit of GH₵ ${String.format("%.2f", deposit.amount)} has been approved and added to your balance.",
                    type = "DEPOSIT"
                )
            )
        }
        Result.success(Unit)
    }

    suspend fun rejectDeposit(depositId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val deposit = depositDao.getDepositById(depositId) ?: return@withContext Result.failure(Exception("Deposit not found"))
        depositDao.updateDeposit(deposit.copy(status = "REJECTED"))
        notificationDao.insertNotification(
            NotificationEntity(
                username = deposit.username,
                title = "Deposit Rejected",
                message = "Your deposit of GH₵ ${String.format("%.2f", deposit.amount)} was rejected. Please contact support.",
                type = "DEPOSIT"
            )
        )
        Result.success(Unit)
    }

    suspend fun approveWithdrawal(withdrawalId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val w = withdrawalDao.getWithdrawalById(withdrawalId) ?: return@withContext Result.failure(Exception("Withdrawal not found"))
        if (w.status != "PENDING") return@withContext Result.failure(Exception("Already processed"))

        withdrawalDao.updateWithdrawal(w.copy(status = "APPROVED"))
        notificationDao.insertNotification(
            NotificationEntity(
                username = w.username,
                title = "Withdrawal Approved & Sent!",
                message = "Your ${w.withdrawalType} withdrawal of GH₵ ${String.format("%.2f", w.amount)} was paid to ${w.payoutPhone}.",
                type = "WITHDRAWAL"
            )
        )
        Result.success(Unit)
    }

    suspend fun rejectWithdrawal(withdrawalId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val w = withdrawalDao.getWithdrawalById(withdrawalId) ?: return@withContext Result.failure(Exception("Withdrawal not found"))
        if (w.status != "PENDING") return@withContext Result.failure(Exception("Already processed"))

        withdrawalDao.updateWithdrawal(w.copy(status = "REJECTED"))
        // Refund balance
        val user = userDao.getUserByUsername(w.username)
        if (user != null) {
            userDao.updateUser(user.copy(accountBalance = user.accountBalance + w.amount))
        }
        notificationDao.insertNotification(
            NotificationEntity(
                username = w.username,
                title = "Withdrawal Rejected",
                message = "Your withdrawal request of GH₵ ${String.format("%.2f", w.amount)} was rejected and refunded to balance.",
                type = "WITHDRAWAL"
            )
        )
        Result.success(Unit)
    }

    suspend fun toggleUserAccountStatus(username: String): Result<String> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username) ?: return@withContext Result.failure(Exception("User not found"))
        val newStatus = if (user.status == "ACTIVE") "SUSPENDED" else "ACTIVE"
        userDao.updateUser(user.copy(status = newStatus))
        Result.success(newStatus)
    }

    suspend fun addVendor(name: String, phone: String, whatsapp: String, price: String) = withContext(Dispatchers.IO) {
        vendorDao.insertVendor(VendorEntity(name = name, phone = phone, whatsapp = whatsapp, couponPrice = price))
    }

    suspend fun deleteVendor(vendor: VendorEntity) = withContext(Dispatchers.IO) {
        vendorDao.deleteVendor(vendor)
    }

    suspend fun addTask(title: String, platform: String, reward: Double, url: String, instructions: String) = withContext(Dispatchers.IO) {
        dailyTaskDao.insertTask(DailyTaskEntity(title = title, platform = platform, rewardAmount = reward, targetUrl = url, instructions = instructions))
    }

    suspend fun broadcastAnnouncement(title: String, message: String) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(
            NotificationEntity(
                username = "ALL",
                title = title,
                message = message,
                type = "ANNOUNCEMENT"
            )
        )
    }
}
