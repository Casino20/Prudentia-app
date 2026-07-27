package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun ReferralProgramScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val referredUsers by viewModel.referredUsers.collectAsState()

    val username = user?.username ?: "investor"
    val refCode = user?.referralCode ?: "PRUDENTI-REF"
    val refLink = "https://prudenti.invest/ref/$username"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Referral Commission Program",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Invite friends to Prudenti & earn lucrative cash bonuses withdrawable every Friday!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Referral Commission Tiers Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "REFERRAL BONUS TIERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )

                    HorizontalDivider(color = GoldPrimary.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        RefTierItem("Starter Plan", "GH₵ 60")
                        RefTierItem("Standard Plan", "GH₵ 90")
                        RefTierItem("Premium Plan", "GH₵ 130")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Note: Referral commissions can be withdrawn every FRIDAY to your Mobile Money account.",
                        fontSize = 11.sp,
                        color = GoldPrimary
                    )
                }
            }
        }

        // Share Referral Link Card
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
                    Text(text = "Your Sharing Link & Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = refLink,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Referral Link") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Prudenti Link", refLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
                        ) {
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Join me on Prudenti Investment Site and earn daily returns! Register here: $refLink")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Icon(Icons.Default.Share, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Link", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Team Members List
        item {
            Text(
                text = "Your Team Members (${referredUsers.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (referredUsers.isEmpty()) {
            item {
                Text("No referred users registered with your code yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(referredUsers) { refUser ->
                ReferredUserRow(refUser)
            }
        }
    }
}

@Composable
fun RefTierItem(label: String, amount: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = amount, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
    }
}

@Composable
fun ReferredUserRow(user: UserEntity) {
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
                Text(text = user.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Plan: ${user.packageName}", fontSize = 11.sp, color = GoldPrimary)
            }
            Text(text = "Joined", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
        }
    }
}
