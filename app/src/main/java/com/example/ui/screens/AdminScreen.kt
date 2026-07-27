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
import com.example.data.db.DepositEntity
import com.example.data.db.UserEntity
import com.example.data.db.VendorEntity
import com.example.data.db.WithdrawalEntity
import com.example.ui.components.StatusChip
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun AdminScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    var adminTab by remember { mutableStateOf(0) } // 0: Deposits, 1: Withdrawals, 2: Coupons, 3: Users, 4: Vendors, 5: Tasks, 6: Broadcast

    val allDeposits by viewModel.allDeposits.collectAsState()
    val allWithdrawals by viewModel.allWithdrawals.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val vendors by viewModel.allVendors.collectAsState()

    val pendingDeposits = remember(allDeposits) { allDeposits.filter { it.status == "PENDING" } }
    val pendingWithdrawals = remember(allWithdrawals) { allWithdrawals.filter { it.status == "PENDING" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Header
        Surface(
            color = NavyDark,
            tonalElevation = 4.dp
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
                        Icon(Icons.Default.AdminPanelSettings, null, tint = GoldPrimary, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("ADMIN CONTROL CENTER", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 16.sp)
                            Text("Prudenti Platform Administration", fontSize = 11.sp, color = androidx.compose.ui.graphics.Color.LightGray)
                        }
                    }

                    Button(
                        onClick = { onNavigate(Screen.CouponManager) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Coupons", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Admin Navigation Scrollable TabRow
        ScrollableTabRow(
            selectedTabIndex = adminTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = GoldPrimary,
            edgePadding = 8.dp
        ) {
            Tab(selected = adminTab == 0, onClick = { adminTab = 0 }) {
                Text("Deposits (${pendingDeposits.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = adminTab == 1, onClick = { adminTab = 1 }) {
                Text("Withdrawals (${pendingWithdrawals.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = adminTab == 2, onClick = { adminTab = 2 }) {
                Text("Users (${allUsers.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = adminTab == 3, onClick = { adminTab = 3 }) {
                Text("Vendors (${vendors.size})", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = adminTab == 4, onClick = { adminTab = 4 }) {
                Text("Add Task", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = adminTab == 5, onClick = { adminTab = 5 }) {
                Text("Broadcast", modifier = Modifier.padding(12.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (adminTab) {
                0 -> {
                    // Pending Deposits
                    if (pendingDeposits.isEmpty()) {
                        item { Text("No pending deposit requests.", fontSize = 12.sp) }
                    } else {
                        items(pendingDeposits) { dep ->
                            AdminDepositCard(dep, onApprove = { viewModel.adminApproveDeposit(dep.id) }, onReject = { viewModel.adminRejectDeposit(dep.id) })
                        }
                    }
                }
                1 -> {
                    // Pending Withdrawals
                    if (pendingWithdrawals.isEmpty()) {
                        item { Text("No pending withdrawal requests.", fontSize = 12.sp) }
                    } else {
                        items(pendingWithdrawals) { w ->
                            AdminWithdrawalCard(w, onApprove = { viewModel.adminApproveWithdrawal(w.id) }, onReject = { viewModel.adminRejectWithdrawal(w.id) })
                        }
                    }
                }
                2 -> {
                    // Users Control
                    items(allUsers) { u ->
                        AdminUserCard(u, onToggleStatus = { viewModel.adminToggleUserStatus(u.username) })
                    }
                }
                3 -> {
                    // Vendors Manager
                    item { AdminAddVendorBox(onAdd = { n, p, w, pr -> viewModel.adminAddVendor(n, p, w, pr) }) }
                    items(vendors) { v ->
                        AdminVendorRow(v, onDelete = { viewModel.adminDeleteVendor(v) })
                    }
                }
                4 -> {
                    // Tasks Manager
                    item { AdminAddTaskBox(onAdd = { t, p, r, u, i -> viewModel.adminAddTask(t, p, r, u, i) }) }
                }
                5 -> {
                    // Broadcast
                    item { AdminBroadcastBox(onSend = { t, m -> viewModel.adminBroadcast(t, m) }) }
                }
            }
        }
    }
}

@Composable
fun AdminDepositCard(dep: DepositEntity, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("User: ${dep.username}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("GH₵ ${String.format("%.2f", dep.amount)}", fontWeight = FontWeight.Bold, color = GoldPrimary, fontSize = 16.sp)
            }
            if (dep.paymentProofUri != null) {
                Text("Proof: Receipt Image Attached ✓", fontSize = 11.sp, color = EmeraldGreen)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApprove, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                    Text("Approve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = onReject, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)) {
                    Text("Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalCard(w: WithdrawalEntity, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("User: ${w.username} (${w.withdrawalType})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("GH₵ ${String.format("%.2f", w.amount)}", fontWeight = FontWeight.Bold, color = EmeraldGreen, fontSize = 16.sp)
            }
            Text("Payout Phone: ${w.payoutPhone}", fontSize = 12.sp, color = GoldPrimary)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApprove, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                    Text("Approve & Pay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = onReject, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)) {
                    Text("Reject & Refund", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminUserCard(user: UserEntity, onToggleStatus: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${user.fullName} (@${user.username})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Plan: ${user.packageName} | Bal: GH₵ ${String.format("%.2f", user.accountBalance)}", fontSize = 11.sp, color = GoldPrimary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(status = user.status)
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onToggleStatus) {
                    Icon(
                        imageVector = if (user.status == "ACTIVE") Icons.Default.Block else Icons.Default.CheckCircle,
                        contentDescription = "Toggle Status",
                        tint = if (user.status == "ACTIVE") CrimsonRed else EmeraldGreen
                    )
                }
            }
        }
    }
}

@Composable
fun AdminAddVendorBox(onAdd: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("GH₵ 120 - GH₵ 300") }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Add New Vendor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Vendor Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = whatsapp, onValueChange = { whatsapp = it }, label = { Text("WhatsApp Number") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, phone, if (whatsapp.isBlank()) phone else whatsapp, price)
                        name = ""
                        phone = ""
                        whatsapp = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
            ) {
                Text("Save Vendor", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminVendorRow(vendor: VendorEntity, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(vendor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Phone: ${vendor.phone}", fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed)
            }
        }
    }
}

@Composable
fun AdminAddTaskBox(onAdd: (String, String, Double, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("Facebook") }
    var rewardStr by remember { mutableStateOf("5.0") }
    var url by remember { mutableStateOf("https://facebook.com") }
    var instructions by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Create Daily Social Task", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = platform, onValueChange = { platform = it }, label = { Text("Platform (Facebook/TikTok/etc)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = rewardStr, onValueChange = { rewardStr = it }, label = { Text("Reward Amount (GH₵)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Target URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Task Instructions") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    val reward = rewardStr.toDoubleOrNull() ?: 5.0
                    if (title.isNotBlank()) {
                        onAdd(title, platform, reward, url, instructions)
                        title = ""
                        instructions = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
            ) {
                Text("Publish Task", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdminBroadcastBox(onSend: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Send Announcement to All Users", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title, message)
                        title = ""
                        message = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
            ) {
                Text("Send Broadcast Alert", fontWeight = FontWeight.Bold)
            }
        }
    }
}
