package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PlanConfig
import com.example.ui.components.PackageCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    isLoggedIn: Boolean
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(GeometricBackground),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portfolio Summary Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = GeometricEmeraldDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Background decorative blur circle shapes
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .offset(x = 220.dp, y = (-30).dp)
                                .clip(CircleShape)
                                .background(GeometricEmeraldAccent.copy(alpha = 0.15f))
                        )
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .offset(x = (-30).dp, y = 140.dp)
                                .clip(CircleShape)
                                .background(GeometricEmeraldAccent.copy(alpha = 0.1f))
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL BALANCE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeometricEmeraldLight,
                                    letterSpacing = 1.5.sp
                                )

                                Surface(
                                    color = GeometricEmeraldPrimary.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(20.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GeometricEmeraldAccent.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "PREMIUM PACKAGE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "GH₵ 4,820.50",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color.White.copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "DAILY EARNINGS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GeometricEmeraldLight
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "GH₵ 22.00",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(18.dp),
                                    color = Color.White.copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "TEAM SIZE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GeometricEmeraldLight
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "14 Members",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions 4-Column Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionItem(
                        title = "DEPOSIT",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = GeometricEmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Deposit) }
                    )
                    QuickActionItem(
                        title = "WITHDRAW",
                        icon = Icons.Default.Payments,
                        iconTint = GeometricOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Withdraw) }
                    )
                    QuickActionItem(
                        title = "REFERRAL",
                        icon = Icons.Default.Share,
                        iconTint = GeometricBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.ReferralProgram) }
                    )
                    QuickActionItem(
                        title = "TASKS",
                        icon = Icons.Default.Checklist,
                        iconTint = GeometricPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Tasks) }
                    )
                }
            }

            // Active Daily Task Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CURRENT DAILY TASK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "4 of 5 Complete",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeometricEmeraldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = SlateLight
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GeometricBlue.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Task Icon",
                                        tint = GeometricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Like PRUDENTI Facebook Page",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateDark
                                    )
                                    Text(
                                        text = "Reward: Package Credit Activated",
                                        fontSize = 10.sp,
                                        color = SlateSecondary
                                    )
                                }

                                Button(
                                    onClick = { onNavigate(Screen.Tasks) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDark, contentColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text("GO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Info Notice (Withdrawal Schedule)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = GeometricOrangeLight,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GeometricOrange.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Notice",
                            tint = GeometricOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WITHDRAWAL SCHEDULE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeometricOrange
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Investment earnings: Sunday only. Referral bonus: Friday only. Secure your profits!",
                                fontSize = 11.sp,
                                color = SlateDark,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Referral Link Pill
            item {
                val refCode = "MAXWELL2024"
                val link = "prudenti.com/ref/$refCode"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = "Link",
                                tint = SlateSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = link,
                                fontSize = 11.sp,
                                color = SlateSecondary,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Referral Link", link)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Referral link copied!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "COPY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeometricEmeraldDark
                            )
                        }
                    }
                }
            }

            // Investment Plans Header & Cards
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Investment Plans",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                            Text(
                                text = "Select an automated daily return package",
                                fontSize = 11.sp,
                                color = SlateSecondary
                            )
                        }

                        TextButton(onClick = { onNavigate(Screen.Packages) }) {
                            Text("View All", color = GeometricEmeraldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PackageCard(
                            packageInfo = PlanConfig.STARTER,
                            isCurrent = false,
                            onSelect = { onNavigate(Screen.Register) }
                        )
                        PackageCard(
                            packageInfo = PlanConfig.STANDARD,
                            isCurrent = false,
                            onSelect = { onNavigate(Screen.Register) }
                        )
                        PackageCard(
                            packageInfo = PlanConfig.PREMIUM,
                            isCurrent = false,
                            onSelect = { onNavigate(Screen.Register) }
                        )
                    }
                }
            }
        }

        // Floating Alexa AI Badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .clickable { onNavigate(Screen.AlexaAI) },
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateLight)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GeometricEmeraldAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ALEXA AI ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateLight),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SlateSecondary,
            letterSpacing = 0.5.sp
        )
    }
}

