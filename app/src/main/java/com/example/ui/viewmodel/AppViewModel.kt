package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AlexaAIService
import com.example.data.db.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object About : Screen()
    object Packages : Screen()
    object HowItWorks : Screen()
    object Register : Screen()
    object Login : Screen()
    object Dashboard : Screen()
    object Deposit : Screen()
    object Withdraw : Screen()
    object Tasks : Screen()
    object AlexaAI : Screen()
    object ReferralProgram : Screen()
    object Vendors : Screen()
    object Admin : Screen()
    object CouponManager : Screen()
    object FAQ : Screen()
    object Contact : Screen()
    object Privacy : Screen()
    object Terms : Screen()
}

data class ChatMessage(
    val sender: String, // "USER" or "ALEXA"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(AppDatabase.getDatabase(application))
    private val alexaService = AlexaAIService()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Referral Prefill for Registration
    var prefilledReferralCode: String? = null

    // Alexa Chat History
    private val _alexaMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ALEXA",
                text = "Hello! I am Alexa AI, your personal guide on PRUDENTI INVESTMENT SITE. Ask me anything about daily earnings, package returns, withdrawal days, or coupon codes!"
            )
        )
    )
    val alexaMessages: StateFlow<List<ChatMessage>> = _alexaMessages.asStateFlow()

    // Reactive database streams
    val activeVendors: StateFlow<List<VendorEntity>> = repository.getActiveVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVendors: StateFlow<List<VendorEntity>> = repository.getAllVendors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeposits: StateFlow<List<DepositEntity>> = repository.getAllDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.getAllWithdrawals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyTasks: StateFlow<List<DailyTaskEntity>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User Specific Flows
    val userDeposits: StateFlow<List<DepositEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getDepositsForUser(user.username) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userWithdrawals: StateFlow<List<WithdrawalEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getWithdrawalsForUser(user.username) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taskCompletions: StateFlow<List<UserTaskCompletionEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getTaskCompletions(user.username) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userNotifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotifications(user.username) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referredUsers: StateFlow<List<UserEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getReferredUsers(user.referralCode) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Observe updated user data from DB when user is logged in
        viewModelScope.launch {
            _currentUser.collect { user ->
                if (user != null) {
                    repository.observeUser(user.username).collect { updated ->
                        if (updated != null) {
                            _currentUser.value = updated
                        }
                    }
                }
            }
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun register(
        fullName: String,
        username: String,
        password: String,
        confirmPassword: String,
        phone: String,
        email: String,
        country: String,
        packageName: String,
        couponCode: String,
        referralCode: String?
    ) {
        if (password != confirmPassword) {
            _statusMessage.value = "Passwords do not match."
            return
        }
        if (couponCode.isBlank()) {
            _statusMessage.value = "A valid Coupon Code is required for registration. Please contact a vendor."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registerUser(
                fullName = fullName,
                username = username,
                password = password,
                phone = phone,
                email = email,
                country = country,
                packageName = packageName,
                couponCodeInput = couponCode,
                referralCodeInput = referralCode
            )
            _isLoading.value = false
            result.onSuccess { user ->
                _currentUser.value = user
                _statusMessage.value = "Registration successful! Welcome to Prudenti."
                _currentScreen.value = Screen.Dashboard
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Registration failed."
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.loginUser(username, password)
            _isLoading.value = false
            result.onSuccess { user ->
                _currentUser.value = user
                _statusMessage.value = "Welcome back, ${user.fullName}!"
                // Trigger daily earnings check on login
                repository.checkAndCreditDailyEarnings(user.username)
                _currentScreen.value = if (user.isAdmin) Screen.Admin else Screen.Dashboard
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Login failed."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = Screen.Home
        _statusMessage.value = "Logged out successfully."
    }

    fun submitDeposit(amountStr: String, proofUri: String?) {
        val user = _currentUser.value ?: return
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            _statusMessage.value = "Please enter a valid deposit amount."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.submitDeposit(user.username, amount, proofUri)
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Deposit submitted! Pending admin approval."
                _currentScreen.value = Screen.Dashboard
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Deposit failed."
            }
        }
    }

    fun submitWithdrawal(amountStr: String, payoutPhone: String, type: String, overrideCheck: Boolean = false) {
        val user = _currentUser.value ?: return
        val amount = amountStr.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.submitWithdrawal(
                username = user.username,
                amount = amount,
                withdrawalType = type,
                payoutPhone = payoutPhone,
                overrideDayCheck = overrideCheck
            )
            _isLoading.value = false
            res.onSuccess {
                _statusMessage.value = "Withdrawal request submitted! Pending admin approval."
                _currentScreen.value = Screen.Dashboard
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Withdrawal request failed."
            }
        }
    }

    fun claimDailyEarnings() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.claimDailyEarningsManual(user.username)
            _isLoading.value = false
            res.onSuccess { rate ->
                _statusMessage.value = "Claimed GH₵ ${String.format("%.2f", rate)} daily earnings!"
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Claim failed."
            }
        }
    }

    fun completeDailyTask(task: DailyTaskEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.completeTask(user.username, task)
            _isLoading.value = false
            res.onSuccess { reward ->
                _statusMessage.value = "Task completed! Earned GH₵ ${String.format("%.2f", reward)}"
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Task submission failed."
            }
        }
    }

    fun sendAlexaMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage("USER", text)
        _alexaMessages.value = _alexaMessages.value + userMsg

        viewModelScope.launch {
            val replyText = alexaService.askAlexa(text)
            val alexaMsg = ChatMessage("ALEXA", replyText)
            _alexaMessages.value = _alexaMessages.value + alexaMsg
        }
    }

    // --- ADMIN FUNCTIONS ---

    fun adminGenerateCoupons(count: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.generateBatchCoupons(count)
            _isLoading.value = false
            res.onSuccess { generated ->
                _statusMessage.value = "Successfully generated $generated new coupon codes!"
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Coupon generation failed."
            }
        }
    }

    fun adminApproveDeposit(id: Int) {
        viewModelScope.launch {
            repository.approveDeposit(id)
            _statusMessage.value = "Deposit #$id Approved!"
        }
    }

    fun adminRejectDeposit(id: Int) {
        viewModelScope.launch {
            repository.rejectDeposit(id)
            _statusMessage.value = "Deposit #$id Rejected."
        }
    }

    fun adminApproveWithdrawal(id: Int) {
        viewModelScope.launch {
            repository.approveWithdrawal(id)
            _statusMessage.value = "Withdrawal #$id Approved!"
        }
    }

    fun adminRejectWithdrawal(id: Int) {
        viewModelScope.launch {
            repository.rejectWithdrawal(id)
            _statusMessage.value = "Withdrawal #$id Rejected."
        }
    }

    fun adminToggleUserStatus(username: String) {
        viewModelScope.launch {
            val res = repository.toggleUserAccountStatus(username)
            res.onSuccess { status ->
                _statusMessage.value = "User $username account is now $status."
            }
        }
    }

    fun adminAddVendor(name: String, phone: String, whatsapp: String, price: String) {
        viewModelScope.launch {
            repository.addVendor(name, phone, whatsapp, price)
            _statusMessage.value = "New Vendor '$name' added successfully!"
        }
    }

    fun adminDeleteVendor(vendor: VendorEntity) {
        viewModelScope.launch {
            repository.deleteVendor(vendor)
            _statusMessage.value = "Vendor removed."
        }
    }

    fun adminAddTask(title: String, platform: String, reward: Double, url: String, instructions: String) {
        viewModelScope.launch {
            repository.addTask(title, platform, reward, url, instructions)
            _statusMessage.value = "Daily Task '$title' created!"
        }
    }

    fun adminBroadcast(title: String, message: String) {
        viewModelScope.launch {
            repository.broadcastAnnouncement(title, message)
            _statusMessage.value = "Announcement broadcast to all users!"
        }
    }
}
