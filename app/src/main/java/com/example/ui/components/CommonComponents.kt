package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.DepositEntity
import com.example.data.db.NotificationEntity
import com.example.data.db.VendorEntity
import com.example.data.db.WithdrawalEntity
import com.example.data.repository.PackageInfo
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrudentiTopAppBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    isLoggedIn: Boolean,
    isAdmin: Boolean,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onNavigate(Screen.Home) }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GeometricEmeraldLight)
                        .border(1.dp, GeometricEmeraldAccent.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GeometricEmeraldDark
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "WELCOME BACK",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "PRUDENTI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeometricEmeraldDark
                    )
                }
            }
        },
        actions = {
            // Theme toggle button with pill container
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, SlateLight, CircleShape)
                    .clickable { onToggleDarkMode() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = SlateMedium,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Alexa AI Assistant action
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, SlateLight, CircleShape)
                    .clickable { onNavigate(Screen.AlexaAI) },
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Alexa AI Assistant",
                        tint = GeometricEmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(GeometricEmeraldAccent)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // Menu button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, SlateLight, CircleShape)
                    .clickable { menuExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = SlateMedium,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier
                    .width(220.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = { Text("Home", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Default.Home, null, tint = GeometricEmeraldPrimary) },
                    onClick = { menuExpanded = false; onNavigate(Screen.Home) }
                )
                DropdownMenuItem(
                    text = { Text("Investment Packages", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, null, tint = GeometricEmeraldPrimary) },
                    onClick = { menuExpanded = false; onNavigate(Screen.Packages) }
                )
                DropdownMenuItem(
                    text = { Text("Daily Tasks", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Default.Task, null, tint = GeometricOrange) },
                    onClick = { menuExpanded = false; onNavigate(Screen.Tasks) }
                )
                DropdownMenuItem(
                    text = { Text("Coupon Vendors", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Default.Storefront, null, tint = GeometricBlue) },
                    onClick = { menuExpanded = false; onNavigate(Screen.Vendors) }
                )
                DropdownMenuItem(
                    text = { Text("Referral Program", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.Default.GroupAdd, null, tint = GeometricPurple) },
                    onClick = { menuExpanded = false; onNavigate(Screen.ReferralProgram) }
                )
                DropdownMenuItem(
                    text = { Text("How It Works", fontWeight = FontWeight.SemiBold) },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Help, null, tint = SlateSecondary) },
                    onClick = { menuExpanded = false; onNavigate(Screen.HowItWorks) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                if (isLoggedIn) {
                    DropdownMenuItem(
                        text = { Text("User Dashboard") },
                        leadingIcon = { Icon(Icons.Default.Dashboard, null) },
                        onClick = { menuExpanded = false; onNavigate(Screen.Dashboard) }
                    )
                    DropdownMenuItem(
                        text = { Text("Make Deposit") },
                        leadingIcon = { Icon(Icons.Default.AddCard, null) },
                        onClick = { menuExpanded = false; onNavigate(Screen.Deposit) }
                    )
                    DropdownMenuItem(
                        text = { Text("Withdraw Funds") },
                        leadingIcon = { Icon(Icons.Default.Payments, null) },
                        onClick = { menuExpanded = false; onNavigate(Screen.Withdraw) }
                    )
                    if (isAdmin) {
                        DropdownMenuItem(
                            text = { Text("Admin Console", fontWeight = FontWeight.Bold, color = GeometricOrange) },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null, tint = GeometricOrange) },
                            onClick = { menuExpanded = false; onNavigate(Screen.Admin) }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Logout", color = CrimsonRed) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = CrimsonRed) },
                        onClick = { menuExpanded = false; onLogout() }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Sign In") },
                        leadingIcon = { Icon(Icons.Default.Login, null) },
                        onClick = { menuExpanded = false; onNavigate(Screen.Login) }
                    )
                    DropdownMenuItem(
                        text = { Text("Register Now", fontWeight = FontWeight.Bold, color = GeometricEmeraldPrimary) },
                        leadingIcon = { Icon(Icons.Default.PersonAdd, null, tint = GeometricEmeraldPrimary) },
                        onClick = { menuExpanded = false; onNavigate(Screen.Register) }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GeometricBackground
        )
    )
}

@Composable
fun PrudentiBottomNavBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    isLoggedIn: Boolean
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Packages,
            onClick = { onNavigate(Screen.Packages) },
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Packages") },
            label = { Text("Plans", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Tasks,
            onClick = { onNavigate(Screen.Tasks) },
            icon = { Icon(Icons.Default.Task, contentDescription = "Tasks") },
            label = { Text("Tasks", fontSize = 11.sp) }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.AlexaAI,
            onClick = { onNavigate(Screen.AlexaAI) },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = "Alexa AI") },
            label = { Text("Alexa AI", fontSize = 11.sp) }
        )
        if (isLoggedIn) {
            NavigationBarItem(
                selected = currentScreen is Screen.Dashboard,
                onClick = { onNavigate(Screen.Dashboard) },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                label = { Text("Dashboard", fontSize = 11.sp) }
            )
        } else {
            NavigationBarItem(
                selected = currentScreen is Screen.Register,
                onClick = { onNavigate(Screen.Register) },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Register") },
                label = { Text("Register", fontSize = 11.sp) }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color = GoldPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PackageCard(
    packageInfo: PackageInfo,
    isCurrent: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gradientBrush = if (packageInfo.name == "Premium") {
        Brush.horizontalGradient(listOf(GoldPrimary, GoldBright))
    } else {
        Brush.horizontalGradient(listOf(NavyDark, NavyLight))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = if (isCurrent) EmeraldGreen else GoldPrimary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${packageInfo.name} Plan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isCurrent) {
                    Surface(
                        color = EmeraldGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ACTIVE PLAN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "GH₵ ${packageInfo.price.toInt()}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = " / one-time investment",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PlanFeatureItem("Daily Earnings:", "GH₵ ${packageInfo.dailyRate.toInt()} / day")
                PlanFeatureItem("Referral Commission:", "GH₵ ${packageInfo.refBonus.toInt()} per invite")
                PlanFeatureItem("Investment Duration:", "30 Active Days")
                PlanFeatureItem("Withdrawal Access:", "Sundays & Fridays")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = NavyDark
                )
            ) {
                Text(
                    text = if (isCurrent) "Current Plan" else "Select ${packageInfo.name} Plan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PlanFeatureItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun VendorCard(
    vendor: VendorEntity,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = GoldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = vendor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Phone: ${vendor.phone}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusChip(status = vendor.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val url = "https://api.whatsapp.com/send?phone=${vendor.whatsapp}&text=Hello%20Vendor,%20I%20want%20to%20buy%20a%20Prudenti%20Coupon%20Code"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${vendor.phone}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Vendor", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "APPROVED", "ACTIVE", "COMPLETED", "UNUSED" -> Pair(EmeraldGreen.copy(alpha = 0.15f), EmeraldGreen)
        "PENDING" -> Pair(GoldPrimary.copy(alpha = 0.15f), GoldPrimary)
        "REJECTED", "SUSPENDED", "USED", "DEACTIVATED" -> Pair(CrimsonRed.copy(alpha = 0.15f), CrimsonRed)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
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
