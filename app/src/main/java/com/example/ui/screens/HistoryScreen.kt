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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.ui.theme.ApprovedGreen
import com.example.ui.theme.CancelledGray
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.RejectedRed
import com.example.ui.viewmodel.CashierViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: CashierViewModel) {
    val deposits by viewModel.depositsState.collectAsState()
    val withdrawals by viewModel.withdrawalsState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.history_title),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = "${stringResource(R.string.tab_deposits)} (${deposits.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = "${stringResource(R.string.tab_withdrawals)} (${withdrawals.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        if (selectedTabIndex == 0) {
            if (deposits.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(deposits) { deposit ->
                        DepositHistoryCard(deposit = deposit)
                    }
                }
            }
        } else {
            if (withdrawals.isEmpty()) {
                EmptyStateView()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(withdrawals) { withdrawal ->
                        WithdrawalHistoryCard(
                            withdrawal = withdrawal,
                            onCancel = { viewModel.cancelPendingWithdrawal(withdrawal.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DepositHistoryCard(deposit: DepositEntity) {
    val (statusColor, statusIconText) = getStatusDetails(deposit.status)
    val localizedStatus = getLocalizedStatus(deposit.status)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("deposit_history_card_${deposit.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.deposit_id_title, deposit.id),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusChip(statusText = localizedStatus, color = statusColor, iconText = statusIconText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = deposit.amountText,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.history_bank_player_info, deposit.bankName, deposit.playerId),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (deposit.reference.isNotBlank()) {
                Text(
                    text = stringResource(R.string.history_ref_info, deposit.reference),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formatDate(deposit.createdAt),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun WithdrawalHistoryCard(withdrawal: WithdrawalEntity, onCancel: () -> Unit) {
    val (statusColor, statusIconText) = getStatusDetails(withdrawal.status)
    val localizedStatus = getLocalizedStatus(withdrawal.status)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("withdrawal_history_card_${withdrawal.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.withdrawal_id_title, withdrawal.id),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusChip(statusText = localizedStatus, color = statusColor, iconText = statusIconText)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = withdrawal.amountText,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.history_bank_acc_info, withdrawal.bankName, withdrawal.accountNumber),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = stringResource(R.string.history_holder_player_info, withdrawal.accountHolder, withdrawal.playerId),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!withdrawal.payoutReference.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.history_payout_ref_info, withdrawal.payoutReference),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ApprovedGreen
                )
            }

            if (!withdrawal.rejectionReason.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.history_reason_info, withdrawal.rejectionReason),
                    fontSize = 11.sp,
                    color = RejectedRed
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(withdrawal.createdAt),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (withdrawal.status == "PENDING") {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = stringResource(R.string.cancel_withdrawal_btn), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(statusText: String, color: Color, iconText: String) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = iconText, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = statusText,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun EmptyStateView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_transactions),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun getStatusDetails(status: String): Pair<Color, String> {
    return when (status.uppercase()) {
        "APPROVED", "COMPLETED" -> ApprovedGreen to "✅"
        "PENDING" -> PendingAmber to "⏳"
        "REJECTED" -> RejectedRed to "❌"
        "CANCELLED" -> CancelledGray to "🚫"
        else -> PendingAmber to "🔍"
    }
}

@Composable
fun getLocalizedStatus(status: String): String {
    return when (status.uppercase()) {
        "APPROVED" -> stringResource(R.string.status_approved)
        "COMPLETED" -> stringResource(R.string.status_completed)
        "PENDING" -> stringResource(R.string.status_pending)
        "REJECTED" -> stringResource(R.string.status_rejected)
        "CANCELLED" -> stringResource(R.string.status_cancelled)
        else -> status
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
