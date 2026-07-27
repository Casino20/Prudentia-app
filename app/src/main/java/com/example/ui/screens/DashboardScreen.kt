package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.DepositEntity
import com.example.data.db.NotificationEntity
import com.example.data.db.WithdrawalEntity
import com.example.ui.components.StatCard
import com.example.ui.components.StatusChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val deposits by viewModel.userDeposits.collectAsState()
    val withdrawals by viewModel.userWithdrawals.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Deposits, 1: Withdrawals, 2: Notifications

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Please sign in to view your dashboard.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { onNavigate(Screen.Login) }) { Text("Go to Login") }
            }
        }
        return
    }

    val currentUser = user!!
    val refLink = "https://prudenti.invest/ref/${currentUser.username}"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${currentUser.fullName}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Package: ${currentUser.packageName} Plan (${currentUser.daysRemaining} Days Left)",
                        fontSize = 12.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (currentUser.isAdmin) {
                    Button(
                        onClick = { onNavigate(Screen.Admin) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Admin Console", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Account Balance Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = GeometricEmeraldDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "TOTAL ACCOUNT BALANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeometricEmeraldLight,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "GH₵ ${String.format("%.2f", currentUser.accountBalance)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(Screen.Deposit) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = GeometricEmeraldDark)
                        ) {
                            Icon(Icons.Default.AddCard, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Deposit", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigate(Screen.Withdraw) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GeometricOrange, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Payments, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Claim Daily Return Button
                    OutlinedButton(
                        onClick = { viewModel.claimDailyEarnings() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.Autorenew, null, tint = GeometricEmeraldLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Claim Daily Return (GH₵ ${currentUser.dailyRate.toInt()})", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(
                        title = "Daily Earnings",
                        value = "GH₵ ${String.format("%.2f", currentUser.dailyEarningsTotal)}",
                        subtitle = "+GH₵ ${currentUser.dailyRate.toInt()}/day",
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Referral Earnings",
                        value = "GH₵ ${String.format("%.2f", currentUser.referralEarningsTotal)}",
                        subtitle = "${currentUser.teamSize} Referrals",
                        icon = Icons.Default.GroupAdd,
                        accentColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Referral Link Section with One-Tap Copy & Share
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Your Referral Link",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Code: ${currentUser.referralCode}",
                            fontSize = 11.sp,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = refLink,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Prudenti Referral Link", refLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = GoldPrimary)
                            }

                            IconButton(
                                onClick = {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Join me on Prudenti Investment Site and earn daily returns! Register here: $refLink")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share Link", tint = EmeraldGreen)
                            }
                        }
                    }
                }
            }
        }

        // History Tabs Header
        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = GoldPrimary
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("Deposits (${deposits.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Withdrawals (${withdrawals.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Notifications (${notifications.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Tab Content List
        if (activeTab == 0) {
            if (deposits.isEmpty()) {
                item {
                    Text("No deposit transactions yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(deposits) { dep ->
                    DepositHistoryRow(dep)
                }
            }
        } else if (activeTab == 1) {
            if (withdrawals.isEmpty()) {
                item {
                    Text("No withdrawal requests yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(withdrawals) { w ->
                    WithdrawalHistoryRow(w)
                }
            }
        } else {
            if (notifications.isEmpty()) {
                item {
                    Text("No notifications.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(notifications) { notif ->
                    NotificationHistoryRow(notif)
                }
            }
        }
    }
}

@Composable
fun DepositHistoryRow(dep: DepositEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Deposit GH₵ ${String.format("%.2f", dep.amount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = dateFormat.format(Date(dep.timestamp)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(status = dep.status)
        }
    }
}

@Composable
fun WithdrawalHistoryRow(w: WithdrawalEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "${w.withdrawalType} Withdrawal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "GH₵ ${String.format("%.2f", w.amount)} → ${w.payoutPhone}", fontSize = 12.sp, color = GoldPrimary)
                Text(text = dateFormat.format(Date(w.timestamp)), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusChip(status = w.status)
        }
    }
}

@Composable
fun NotificationHistoryRow(notif: NotificationEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldPrimary)
                Text(text = dateFormat.format(Date(notif.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = notif.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
