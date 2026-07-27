package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.components.PrudentiBottomNavBar
import com.example.ui.components.PrudentiTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.PrudentiTheme
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val statusMessage by viewModel.statusMessage.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(statusMessage) {
                statusMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearStatusMessage()
                }
            }

            PrudentiTheme(darkTheme = isDarkMode) {
                Scaffold(
                    topBar = {
                        PrudentiTopAppBar(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) },
                            isLoggedIn = currentUser != null,
                            isAdmin = currentUser?.isAdmin == true,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onLogout = { viewModel.logout() }
                        )
                    },
                    bottomBar = {
                        PrudentiBottomNavBar(
                            currentScreen = currentScreen,
                            onNavigate = { viewModel.navigateTo(it) },
                            isLoggedIn = currentUser != null
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                            when (screen) {
                                is Screen.Home -> HomeScreen(onNavigate = { viewModel.navigateTo(it) }, isLoggedIn = currentUser != null)
                                is Screen.Packages -> PackagesScreen(onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Register -> RegisterScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Login -> LoginScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Dashboard -> DashboardScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Deposit -> DepositScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Withdraw -> WithdrawScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Tasks -> TasksScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.AlexaAI -> AlexaAIScreen(viewModel = viewModel)
                                is Screen.ReferralProgram -> ReferralProgramScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.Vendors -> VendorsScreen(viewModel = viewModel)
                                is Screen.Admin -> AdminScreen(viewModel = viewModel, onNavigate = { viewModel.navigateTo(it) })
                                is Screen.CouponManager -> CouponManagerScreen(viewModel = viewModel)
                                is Screen.HowItWorks -> HowItWorksScreen(onNavigate = { viewModel.navigateTo(it) })
                                is Screen.FAQ -> FAQScreen()
                                is Screen.About -> AboutScreen()
                                is Screen.Contact -> ContactScreen()
                                is Screen.Privacy -> PrivacyPolicyScreen()
                                is Screen.Terms -> TermsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
