package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PlanConfig
import com.example.ui.components.PackageCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyDark
import com.example.ui.viewmodel.Screen

@Composable
fun PackagesScreen(
    onNavigate: (Screen) -> Unit
) {
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
                    text = "Prudenti Investment Packages",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Choose your preferred daily earnings package below.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            PackageCard(
                packageInfo = PlanConfig.STARTER,
                isCurrent = false,
                onSelect = { onNavigate(Screen.Register) }
            )
        }
        item {
            PackageCard(
                packageInfo = PlanConfig.STANDARD,
                isCurrent = false,
                onSelect = { onNavigate(Screen.Register) }
            )
        }
        item {
            PackageCard(
                packageInfo = PlanConfig.PREMIUM,
                isCurrent = false,
                onSelect = { onNavigate(Screen.Register) }
            )
        }
    }
}

@Composable
fun HowItWorksScreen(
    onNavigate: (Screen) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("How Prudenti Works", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        item {
            InfoStepCard("1. Buy a Coupon Code", "Contact a verified vendor to purchase a coupon code for your preferred plan.")
        }
        item {
            InfoStepCard("2. Create Account & Select Plan", "Fill out the registration form with your details, coupon code, and investment tier.")
        }
        item {
            InfoStepCard("3. Earn Daily Returns", "Watch your daily earnings auto-credit every 24 hours to your account balance.")
        }
        item {
            InfoStepCard("4. Complete Social Tasks", "Earn extra daily rewards by completing quick social engagement tasks.")
        }
        item {
            InfoStepCard("5. Withdraw Cash", "Withdraw investment earnings every Sunday and referral commissions every Friday directly to Mobile Money.")
        }

        item {
            Button(
                onClick = { onNavigate(Screen.Register) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDark)
            ) {
                Text("Start Investing Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoStepCard(stepTitle: String, stepDesc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stepTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GoldPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stepDesc, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun FAQScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Frequently Asked Questions", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        item { InfoStepCard("Q: What is the minimum withdrawal amount?", "The minimum withdrawal amount across all earnings is GH₵ 20.00.") }
        item { InfoStepCard("Q: On which days can I withdraw?", "Investment daily earnings are withdrawable on SUNDAYS. Referral commissions are withdrawable on FRIDAYS.") }
        item { InfoStepCard("Q: How do I get a coupon code?", "Contact any of our authorized Vendors on the Vendors page or during registration.") }
        item { InfoStepCard("Q: How long does my package last?", "Each package remains active for 30 consecutive calendar days.") }
    }
}

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("About PRUDENTI INVESTMENT SITE", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "Prudenti is a premier digital investment and affiliate platform engineered to empower users across Ghana and Africa. We offer high-yield daily returns, transparent coupon security, and automated weekly payout cycles directly integrated with Mobile Money.",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun ContactScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Contact Support", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        InfoStepCard("Customer Support Line", "Phone: +233 50 786 1747 (Maxwell)")
        InfoStepCard("WhatsApp Helpline", "WhatsApp: +233 50 786 1747")
        InfoStepCard("Email Support", "Email: support@prudenti.invest")
    }
}

@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Privacy Policy", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "Your privacy and account safety are our top priorities. We encrypt user credentials and maintain strict confidentiality regarding account balances, phone numbers, and withdrawal transaction logs.",
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun TermsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Terms & Conditions", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "By registering an account on Prudenti Investment Site, you agree to comply with our withdrawal schedule (Sundays for investment earnings, Fridays for referral commissions) and keep your coupon codes confidential.",
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}
