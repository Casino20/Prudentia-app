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
import com.example.data.db.CouponEntity
import com.example.ui.components.StatusChip
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.AppViewModel

@Composable
fun CouponManagerScreen(
    viewModel: AppViewModel
) {
    val context = LocalContext.current
    val coupons by viewModel.allCoupons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var filterStatus by remember { mutableStateOf("ALL") } // ALL, UNUSED, USED
    var customGenCount by remember { mutableStateOf("1000") }

    val filteredCoupons = remember(coupons, filterStatus) {
        when (filterStatus) {
            "UNUSED" -> coupons.filter { it.status == "UNUSED" }
            "USED" -> coupons.filter { it.status == "USED" }
            else -> coupons
        }
    }

    val unusedCount = remember(coupons) { coupons.count { it.status == "UNUSED" } }
    val usedCount = remember(coupons) { coupons.count { it.status == "USED" } }

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
                    text = "Coupon Code Manager",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Generate and control registration coupon codes for vendors and users.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Stats Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    CouponStatCol("Total Codes", coupons.size.toString())
                    CouponStatCol("Unused", unusedCount.toString(), EmeraldGreen)
                    CouponStatCol("Used", usedCount.toString(), GoldPrimary)
                }
            }
        }

        // Batch Generator Box (Space requested by user to generate 1000 to 2000 codes!)
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
                    Text(text = "Generate Batch Coupons", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    OutlinedTextField(
                        value = customGenCount,
                        onValueChange = { customGenCount = it },
                        label = { Text("Number of Coupons to Generate (e.g. 1000)") },
                        leadingIcon = { Icon(Icons.Default.ConfirmationNumber, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.adminGenerateCoupons(1000) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
                        ) {
                            Text("Generate 1,000", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val count = customGenCount.toIntOrNull() ?: 1000
                                viewModel.adminGenerateCoupons(count)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("Generate Custom", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Copy List Button
                    OutlinedButton(
                        onClick = {
                            val codeListStr = filteredCoupons.take(500).joinToString("\n") { it.code }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Prudenti Coupons", codeListStr)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied ${minOf(500, filteredCoupons.size)} coupon codes!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export / Copy Code List to Clipboard")
                    }
                }
            }
        }

        // Filter Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == "ALL",
                    onClick = { filterStatus = "ALL" },
                    label = { Text("All (${coupons.size})") }
                )
                FilterChip(
                    selected = filterStatus == "UNUSED",
                    onClick = { filterStatus = "UNUSED" },
                    label = { Text("Unused ($unusedCount)") }
                )
                FilterChip(
                    selected = filterStatus == "USED",
                    onClick = { filterStatus = "USED" },
                    label = { Text("Used ($usedCount)") }
                )
            }
        }

        // Coupon Items List
        items(filteredCoupons.take(100)) { coupon ->
            CouponRowItem(coupon)
        }
    }
}

@Composable
fun CouponStatCol(label: String, valStr: String, color: androidx.compose.ui.graphics.Color = GoldPrimary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valStr, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun CouponRowItem(coupon: CouponEntity) {
    val context = LocalContext.current
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
                Text(text = coupon.code, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldPrimary)
                if (coupon.usedByUsername != null) {
                    Text(text = "Used by: ${coupon.usedByUsername}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(status = coupon.status)
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Coupon Code", coupon.code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
