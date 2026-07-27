package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CouponEntity::class,
        VendorEntity::class,
        DepositEntity::class,
        WithdrawalEntity::class,
        DailyTaskEntity::class,
        UserTaskCompletionEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun couponDao(): CouponDao
    abstract fun vendorDao(): VendorDao
    abstract fun depositDao(): DepositDao
    abstract fun withdrawalDao(): WithdrawalDao
    abstract fun dailyTaskDao(): DailyTaskDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prudenti_investment_db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: AppDatabase) {
                // Seed Admin User
                val adminUser = UserEntity(
                    fullName = "Platform Administrator",
                    username = "admin",
                    passwordHash = "admin123", // Admin Login: admin / admin123
                    phone = "+233507861747",
                    email = "admin@prudenti.com",
                    country = "Ghana (GH)",
                    packageName = "Premium",
                    packageCost = 300.0,
                    dailyRate = 22.0,
                    couponCode = "ADMIN-INITIAL",
                    referralCode = "PRUDENTI-ADMIN",
                    accountBalance = 5000.0,
                    dailyEarningsTotal = 1500.0,
                    referralEarningsTotal = 2500.0,
                    isAdmin = true
                )
                db.userDao().insertUser(adminUser)

                // Seed Verified Coupon Vendors
                val vendors = listOf(
                    VendorEntity(name = "Vendor Maxwell (Official)", phone = "0507861747", whatsapp = "233507861747", couponPrice = "GH₵ 120 - GH₵ 300"),
                    VendorEntity(name = "Vendor Kwabena Express", phone = "0245123987", whatsapp = "233245123987", couponPrice = "GH₵ 120 - GH₵ 300"),
                    VendorEntity(name = "Vendor Akosua Verified", phone = "0559876543", whatsapp = "233559876543", couponPrice = "GH₵ 120 - GH₵ 300")
                )
                vendors.forEach { db.vendorDao().insertVendor(it) }

                // Seed Initial Sample Coupon Codes
                val initialCoupons = mutableListOf<CouponEntity>()
                for (i in 1..50) {
                    val code = "PRUDENTI-${(1000..9999).random()}-${(1000..9999).random()}"
                    initialCoupons.add(CouponEntity(code = code))
                }
                // Add known test code for quick review
                initialCoupons.add(CouponEntity(code = "PRUDENTI-VIP-2026"))
                db.couponDao().insertCoupons(initialCoupons)

                // Seed Initial Daily Tasks
                val tasks = listOf(
                    DailyTaskEntity(
                        title = "Like Prudenti Official Facebook Page",
                        platform = "Facebook",
                        rewardAmount = 5.0,
                        targetUrl = "https://facebook.com",
                        instructions = "Open link, like the main page and comment 'Prudenti Pays!', then return to claim reward."
                    ),
                    DailyTaskEntity(
                        title = "Follow Prudenti TikTok Channel",
                        platform = "TikTok",
                        rewardAmount = 5.0,
                        targetUrl = "https://tiktok.com",
                        instructions = "Follow @PrudentiInvest on TikTok and like the top 2 videos."
                    ),
                    DailyTaskEntity(
                        title = "Share Promotional Banner on WhatsApp Status",
                        platform = "WhatsApp",
                        rewardAmount = 6.0,
                        targetUrl = "https://whatsapp.com",
                        instructions = "Copy your referral link and share our daily earnings banner on your WhatsApp story."
                    )
                )
                db.dailyTaskDao().insertTasks(tasks)

                // Seed Welcome Notification
                db.notificationDao().insertNotification(
                    NotificationEntity(
                        username = "ALL",
                        title = "Welcome to Prudenti Investment Site!",
                        message = "Your gateway to daily financial freedom. Select a package, complete tasks, and earn daily!",
                        type = "ANNOUNCEMENT"
                    )
                )
            }
        }
    }
}
