package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.ui.theme.ApprovedGreen
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.RejectedRed
import com.example.ui.theme.SecondaryGold
import com.example.ui.viewmodel.CashierViewModel

@Composable
fun AdminScreen(viewModel: CashierViewModel) {
    val isLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val pinInput by viewModel.adminPinInput.collectAsState()

    val totalUsers by viewModel.totalUsersCount.collectAsState()
    val pendingDeposits by viewModel.pendingDepositsState.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawalsState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    if (!isLoggedIn) {
        AdminPinLoginView(
            pinInput = pinInput,
            onPinChange = { viewModel.adminPinInput.value = it },
            onLogin = { viewModel.loginAdmin() }
        ) {
            viewModel.logoutAdmin()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.admin_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.logoutAdmin() },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.btn_logout), fontSize = 11.sp)
                }
            }

            // Stats Dashboard Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = stringResource(R.string.stat_users),
                    value = totalUsers.toString(),
                    color = PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = stringResource(R.string.stat_pending_dep),
                    value = pendingDeposits.size.toString(),
                    color = SecondaryGold,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = stringResource(R.string.stat_pending_wdr),
                    value = pendingWithdrawals.size.toString(),
                    color = SecondaryGold,
                    modifier = Modifier.weight(1f)
                )
            }

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text(text = stringResource(R.string.admin_tab_deposits, pendingDeposits.size), fontWeight = FontWeight.Bold) }
                )

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text(text = stringResource(R.string.admin_tab_withdrawals, pendingWithdrawals.size), fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTabIndex == 0) {
                if (pendingDeposits.isEmpty()) {
                    EmptyStateView()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(pendingDeposits) { deposit ->
                            AdminDepositCard(
                                deposit = deposit,
                                onApprove = { viewModel.adminApproveDeposit(deposit.id) },
                                onReject = { viewModel.adminRejectDeposit(deposit.id) }
                            )
                        }
                    }
                }
            } else {
                if (pendingWithdrawals.isEmpty()) {
                    EmptyStateView()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(pendingWithdrawals) { withdrawal ->
                            AdminWithdrawalCard(
                                withdrawal = withdrawal,
                                onApprove = { payoutRef -> viewModel.adminApproveWithdrawal(withdrawal.id, payoutRef) },
                                onReject = { reason -> viewModel.adminRejectWithdrawal(withdrawal.id, reason) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPinLoginView(
    pinInput: String,
    onPinChange: (String) -> Unit,
    onLogin: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock",
            tint = PrimaryGreen,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.admin_security_access),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.admin_pin_prompt),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = pinInput,
            onValueChange = onPinChange,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .testTag("input_admin_pin"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .testTag("admin_login_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text(text = stringResource(R.string.admin_login_btn), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AdminDepositCard(
    deposit: DepositEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Deposit #${deposit.id} | ${deposit.amountText}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Player ID: ${deposit.playerId} | Bank: ${deposit.bankName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Ref: ${deposit.reference}",
                fontSize = 11.sp,
                color = PrimaryGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = ApprovedGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.approve_btn), fontSize = 12.sp)
                }

                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = RejectedRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.reject_btn), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalCard(
    withdrawal: WithdrawalEntity,
    onApprove: (String?) -> Unit,
    onReject: (String?) -> Unit
) {
    var payoutRefInput by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Withdrawal #${withdrawal.id} | ${withdrawal.amountText}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Player ID: ${withdrawal.playerId} | Code: ${withdrawal.secretCode}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Bank: ${withdrawal.bankName} | Acc: ${withdrawal.accountNumber}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Holder: ${withdrawal.accountHolder} | Branch: ${withdrawal.branch}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = payoutRefInput,
                onValueChange = { payoutRefInput = it },
                placeholder = { Text(stringResource(R.string.payout_ref_placeholder), fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onApprove(payoutRefInput.ifBlank { null }) },
                    colors = ButtonDefaults.buttonColors(containerColor = ApprovedGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.approve_btn), fontSize = 12.sp)
                }

                Button(
                    onClick = { onReject("Admin Declined") },
                    colors = ButtonDefaults.buttonColors(containerColor = RejectedRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.reject_btn), fontSize = 12.sp)
                }
            }
        }
    }
}
