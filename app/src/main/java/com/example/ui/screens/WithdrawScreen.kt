package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WithdrawalHistoryRow
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val withdrawals by viewModel.userWithdrawals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var withdrawalType by remember { mutableStateOf("INVESTMENT") } // INVESTMENT or REFERRAL
    var amountText by remember { mutableStateOf("") }
    var payoutPhone by remember { mutableStateOf(user?.phone ?: "") }
    var overrideCheck by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val currentDayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Unknown"
    }

    val isTodaySunday = currentDayOfWeek == "Sunday"
    val isTodayFriday = currentDayOfWeek == "Friday"

    val isWithdrawalAllowed = if (withdrawalType == "INVESTMENT") {
        isTodaySunday || overrideCheck
    } else {
        isTodayFriday || overrideCheck
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Withdrawal Portal",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Today is $currentDayOfWeek. Enforcing automated withdrawal schedule.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Automated Day Schedule Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isWithdrawalAllowed) EmeraldGreen.copy(alpha = 0.15f) else GoldPrimary.copy(alpha = 0.15f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isWithdrawalAllowed) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isWithdrawalAllowed) EmeraldGreen else GoldPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (withdrawalType == "INVESTMENT") "Investment Withdrawals (Sundays)" else "Referral Withdrawals (Fridays)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (withdrawalType == "INVESTMENT") {
                            if (isTodaySunday) "SUNDAY OPEN! You can process your investment earnings withdrawal today."
                            else "Investment withdrawals are locked today ($currentDayOfWeek). They unlock on SUNDAYS."
                        } else {
                            if (isTodayFriday) "FRIDAY OPEN! You can process your referral commission withdrawal today."
                            else "Referral withdrawals are locked today ($currentDayOfWeek). They unlock on FRIDAYS."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Demo / Test Bypass Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Admin/Demo Bypass Day Restriction:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = overrideCheck,
                            onCheckedChange = { overrideCheck = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary)
                        )
                    }
                }
            }
        }

        // Withdrawal Request Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Select Withdrawal Category", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = withdrawalType == "INVESTMENT",
                            onClick = { withdrawalType = "INVESTMENT" },
                            label = { Text("Investment Earnings (Sunday)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = withdrawalType == "REFERRAL",
                            onClick = { withdrawalType = "REFERRAL" },
                            label = { Text("Referral Bonus (Friday)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount to Withdraw (GH₵)") },
                        placeholder = { Text("Minimum GH₵ 20.00") },
                        leadingIcon = { Icon(Icons.Default.Payments, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = payoutPhone,
                        onValueChange = { payoutPhone = it },
                        label = { Text("Mobile Money Payout Number") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            viewModel.submitWithdrawal(
                                amountStr = amountText,
                                payoutPhone = payoutPhone,
                                type = withdrawalType,
                                overrideCheck = overrideCheck
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading && isWithdrawalAllowed,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = androidx.compose.ui.graphics.Color.White)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = androidx.compose.ui.graphics.Color.White)
                        } else {
                            Text("Request Withdrawal", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // History
        item {
            Text("Your Withdrawal Requests", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (withdrawals.isEmpty()) {
            item {
                Text("No withdrawal records found.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(withdrawals) { w ->
                WithdrawalHistoryRow(w)
            }
        }
    }
}
