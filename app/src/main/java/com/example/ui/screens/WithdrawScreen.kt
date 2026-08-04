package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryGold
import com.example.ui.viewmodel.CashierViewModel

@Composable
fun WithdrawScreen(viewModel: CashierViewModel) {
    val userState by viewModel.userState.collectAsState()
    val pendingWithdrawals by viewModel.pendingWithdrawalsState.collectAsState()

    val playerId by viewModel.withdrawPlayerId.collectAsState()
    val amount by viewModel.withdrawAmount.collectAsState()
    val secretCode by viewModel.withdrawSecretCode.collectAsState()
    val bankName by viewModel.withdrawBankName.collectAsState()
    val accountHolder by viewModel.withdrawAccountHolder.collectAsState()
    val accountNumber by viewModel.withdrawAccountNumber.collectAsState()
    val branch by viewModel.withdrawBranch.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(userState) {
        viewModel.prefillWithdrawalForm()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location & Cashier Info
        CashierLocationCard()

        // Pending Withdrawal Block Banner if exists
        if (pendingWithdrawals.isNotEmpty()) {
            val pending = pendingWithdrawals.first()
            PendingWithdrawalWarningCard(pending = pending) {
                viewModel.cancelPendingWithdrawal(pending.id)
            }
        }

        // Auto Fill Saved Bank Notice if user has saved bank
        userState?.let { user ->
            if (user.savedBankName.isNotBlank() && user.savedAccountNumber.isNotBlank()) {
                SavedBankNotificationCard(user = user) {
                    viewModel.prefillWithdrawalForm()
                }
            }
        }

        // Withdrawal Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.withdraw_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.withdraw_min_max),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SecondaryGold
                )

                // Player ID
                OutlinedTextField(
                    value = playerId,
                    onValueChange = { viewModel.setWithdrawPlayerId(it) },
                    label = { Text(text = stringResource(R.string.label_player_id)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_withdraw_player_id"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { viewModel.setWithdrawAmount(it) },
                    label = { Text(text = stringResource(R.string.label_amount)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_withdraw_amount"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Secret Code
                OutlinedTextField(
                    value = secretCode,
                    onValueChange = { viewModel.setWithdrawSecretCode(it) },
                    label = { Text(text = stringResource(R.string.label_secret_code)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_withdraw_secret_code"),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = stringResource(R.string.withdraw_bank_details_header),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Bank Name
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { viewModel.setWithdrawBankName(it) },
                    label = { Text(text = stringResource(R.string.label_bank_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Account Holder Name
                OutlinedTextField(
                    value = accountHolder,
                    onValueChange = { viewModel.setWithdrawAccountHolder(it) },
                    label = { Text(text = stringResource(R.string.label_acc_holder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Account Number
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { viewModel.setWithdrawAccountNumber(it) },
                    label = { Text(text = stringResource(R.string.label_acc_number)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Branch
                OutlinedTextField(
                    value = branch,
                    onValueChange = { viewModel.setWithdrawBranch(it) },
                    label = { Text(text = stringResource(R.string.label_branch)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.submitWithdrawalRequest() },
                    enabled = pendingWithdrawals.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_withdrawal_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Withdraw")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.submit_withdrawal_btn),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CashierLocationCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = SecondaryGold,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Fast Xbet Official Cashier",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.withdraw_address_info),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SavedBankNotificationCard(user: com.example.data.local.entity.UserEntity, onFill: () -> Unit) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Saved",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${user.savedBankName} (${user.savedAccountNumber})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            OutlinedButton(
                onClick = onFill,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.btn_autofill), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun PendingWithdrawalWarningCard(
    pending: com.example.data.local.entity.WithdrawalEntity,
    onCancel: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = stringResource(R.string.pending_withdraw_exists_header),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.pending_withdraw_exists_details, pending.id, pending.amountText, pending.bankName),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.cancel_request_id, pending.id), fontSize = 11.sp)
            }
        }
    }
}
