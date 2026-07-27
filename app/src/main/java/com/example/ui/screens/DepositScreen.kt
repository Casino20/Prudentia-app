package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DepositHistoryRow
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun DepositScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("") }
    var proofAttached by remember { mutableStateOf(false) }

    val user by viewModel.currentUser.collectAsState()
    val deposits by viewModel.userDeposits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val paymentNumber = "0507861747"
    val accountName = "Maxwell"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Deposit Funds",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Send Mobile Money to the official payment account below.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Official Payment Account Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OFFICIAL DEPOSIT ACCOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            letterSpacing = 1.sp
                        )
                        Icon(Icons.Default.PhoneAndroid, null, tint = GoldPrimary)
                    }

                    HorizontalDivider(color = GoldPrimary.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Payment Number", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(paymentNumber, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Deposit Number", paymentNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Payment Number copied!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Number", tint = androidx.compose.ui.graphics.Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Account Name", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(accountName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Accepted Methods", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("MoMo / Vodacash", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EmeraldGreen)
                        }
                    }
                }
            }
        }

        // Deposit Form
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
                    Text(text = "Submit Payment Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount Paid (GH₵)") },
                        leadingIcon = { Icon(Icons.Default.Payments, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Payment Proof Upload Selector
                    OutlinedButton(
                        onClick = { proofAttached = !proofAttached },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (proofAttached) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                            contentDescription = null,
                            tint = if (proofAttached) EmeraldGreen else GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (proofAttached) "Receipt Screenshot Attached ✓" else "Upload Payment Proof / Receipt",
                            fontWeight = if (proofAttached) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.submitDeposit(
                                amountStr = amountText,
                                proofUri = if (proofAttached) "content://proof/receipt_sample.png" else null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyDark)
                        } else {
                            Text("Submit Deposit Request", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // Deposit History
        item {
            Text("Deposit History", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (deposits.isEmpty()) {
            item {
                Text("No deposits recorded yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(deposits) { dep ->
                DepositHistoryRow(dep)
            }
        }
    }
}
