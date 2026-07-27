package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CashierRepository
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AiScanResult(
    val amountText: String = "",
    val amountValue: Double = 0.0,
    val bankName: String = "",
    val reference: String = "",
    val detectedDateTime: String = ""
)

sealed class UiMessage {
    data class SuccessRes(val resId: Int, val args: List<Any> = emptyList()) : UiMessage()
    data class ErrorRes(val resId: Int, val args: List<Any> = emptyList()) : UiMessage()
    data class Success(val message: String) : UiMessage()
    data class Error(val message: String) : UiMessage()
}

class CashierViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CashierRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CashierRepository(db)
    }

    val userState: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val banksState: StateFlow<List<BankEntity>> = repository.activeBanksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val depositsState: StateFlow<List<DepositEntity>> = repository.allDepositsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingDepositsState: StateFlow<List<DepositEntity>> = repository.pendingDepositsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawalsState: StateFlow<List<WithdrawalEntity>> = repository.allWithdrawalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingWithdrawalsState: StateFlow<List<WithdrawalEntity>> = repository.pendingWithdrawalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUsersCount = repository.totalUsersCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val pendingDepositsCount = repository.pendingDepositsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingWithdrawalsCount = repository.pendingWithdrawalsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Deposit Wizard State
    private val _depositStep = MutableStateFlow(1) // 1: Select Bank, 2: Transfer Details, 3: Upload Slip, 4: Confirm Player ID
    val depositStep: StateFlow<Int> = _depositStep.asStateFlow()

    private val _selectedBank = MutableStateFlow<BankEntity?>(null)
    val selectedBank: StateFlow<BankEntity?> = _selectedBank.asStateFlow()

    private val _uploadedSlipUri = MutableStateFlow<Uri?>(null)
    val uploadedSlipUri: StateFlow<Uri?> = _uploadedSlipUri.asStateFlow()

    private val _isAnalyzingSlip = MutableStateFlow(false)
    val isAnalyzingSlip: StateFlow<Boolean> = _isAnalyzingSlip.asStateFlow()

    private val _aiScanResult = MutableStateFlow<AiScanResult?>(null)
    val aiScanResult: StateFlow<AiScanResult?> = _aiScanResult.asStateFlow()

    private val _depositPlayerId = MutableStateFlow("")
    val depositPlayerId: StateFlow<String> = _depositPlayerId.asStateFlow()

    // Withdraw Form State
    val withdrawPlayerId = MutableStateFlow("")
    val withdrawAmount = MutableStateFlow("")
    val withdrawSecretCode = MutableStateFlow("")
    val withdrawBankName = MutableStateFlow("")
    val withdrawAccountHolder = MutableStateFlow("")
    val withdrawAccountNumber = MutableStateFlow("")
    val withdrawBranch = MutableStateFlow("")

    // Admin State
    val isAdminLoggedIn = MutableStateFlow(false)
    val adminPinInput = MutableStateFlow("")

    // Global UI Message / Snackbars
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.updateLanguage(lang)
        }
    }

    // Deposit Wizard Actions
    fun selectBankForDeposit(bank: BankEntity) {
        _selectedBank.value = bank
        _depositStep.value = 2
    }

    fun resetDepositWizard() {
        _depositStep.value = 1
        _selectedBank.value = null
        _uploadedSlipUri.value = null
        _isAnalyzingSlip.value = false
        _aiScanResult.value = null
        _depositPlayerId.value = userState.value?.playerId ?: ""
    }

    fun updateDepositPlayerId(id: String) {
        _depositPlayerId.value = id
    }

    fun processUploadedSlip(uri: Uri) {
        _uploadedSlipUri.value = uri
        _isAnalyzingSlip.value = true
        _depositStep.value = 3

        viewModelScope.launch {
            delay(2000) // Simulate AI Slip Recognition
            val sampleAmount = (Random.nextInt(20, 500) * 100).toDouble()
            val sampleRef = "TXN${Random.nextInt(100000, 999999)}"
            val bankName = _selectedBank.value?.bankName ?: "BOC"

            _aiScanResult.value = AiScanResult(
                amountText = "LKR ${sampleAmount.toInt()}",
                amountValue = sampleAmount,
                bankName = bankName,
                reference = sampleRef,
                detectedDateTime = "2026-07-27 14:30"
            )
            _isAnalyzingSlip.value = false
            _depositPlayerId.value = userState.value?.playerId ?: ""
            _depositStep.value = 4
        }
    }

    fun submitDepositRequest() {
        val playerId = _depositPlayerId.value.trim()
        val bank = _selectedBank.value?.bankName ?: _aiScanResult.value?.bankName ?: "Bank Account"
        val ai = _aiScanResult.value

        val amountText = ai?.amountText ?: "LKR 5,000"
        val amount = ai?.amountValue ?: 5000.0
        val ref = ai?.reference ?: ""

        viewModelScope.launch {
            val result = repository.submitDeposit(
                playerId = playerId,
                bankName = bank,
                amountText = amountText,
                amount = amount,
                slipUri = _uploadedSlipUri.value?.toString(),
                reference = ref
            )

            result.onSuccess {
                _uiMessage.value = UiMessage.SuccessRes(R.string.msg_deposit_submitted)
                resetDepositWizard()
            }.onFailure { ex ->
                when {
                    ex.message?.startsWith("DUPLICATE_SLIP") == true -> {
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_duplicate_slip)
                    }
                    ex.message == "INVALID_PLAYER_ID" -> {
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_invalid_player_id)
                    }
                    else -> {
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_submit_deposit_failed)
                    }
                }
            }
        }
    }

    // Auto fill withdrawal fields from saved user bank
    fun prefillWithdrawalForm() {
        val user = userState.value ?: return
        if (withdrawPlayerId.value.isBlank()) withdrawPlayerId.value = user.playerId
        if (withdrawBankName.value.isBlank()) withdrawBankName.value = user.savedBankName
        if (withdrawAccountHolder.value.isBlank()) withdrawAccountHolder.value = user.savedAccountHolder
        if (withdrawAccountNumber.value.isBlank()) withdrawAccountNumber.value = user.savedAccountNumber
        if (withdrawBranch.value.isBlank()) withdrawBranch.value = user.savedBranch
    }

    fun submitWithdrawalRequest() {
        val pId = withdrawPlayerId.value.trim()
        val amtDouble = withdrawAmount.value.toDoubleOrNull() ?: 0.0
        val secret = withdrawSecretCode.value.trim()
        val bank = withdrawBankName.value.trim()
        val holder = withdrawAccountHolder.value.trim()
        val accNo = withdrawAccountNumber.value.trim()
        val branch = withdrawBranch.value.trim()

        if (pId.isBlank() || secret.isBlank() || bank.isBlank() || holder.isBlank() || accNo.isBlank()) {
            _uiMessage.value = UiMessage.ErrorRes(R.string.err_empty_field)
            return
        }

        viewModelScope.launch {
            val result = repository.submitWithdrawal(
                playerId = pId,
                amount = amtDouble,
                secretCode = secret,
                bankName = bank,
                accountHolder = holder,
                accountNumber = accNo,
                branch = branch
            )

            result.onSuccess {
                _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_submitted)
                withdrawAmount.value = ""
                withdrawSecretCode.value = ""
            }.onFailure { ex ->
                val msg = ex.message ?: ""
                when {
                    msg.startsWith("PENDING_WITHDRAWAL_EXISTS") -> {
                        val parts = msg.split(":")
                        val id = if (parts.size > 1) parts[1] else "?"
                        val amt = if (parts.size > 2) parts[2] else ""
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_pending_withdraw_exists, listOf(id, amt))
                    }
                    msg == "INVALID_PLAYER_ID" -> {
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_invalid_player_id)
                    }
                    msg == "INVALID_AMOUNT" -> {
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_invalid_amount)
                    }
                    else -> {
                        _uiMessage.value = UiMessage.ErrorRes(R.string.err_submit_withdraw_failed)
                    }
                }
            }
        }
    }

    fun cancelPendingWithdrawal(id: Long) {
        viewModelScope.launch {
            val success = repository.cancelWithdrawal(id)
            if (success) {
                _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_cancelled, listOf(id))
            } else {
                _uiMessage.value = UiMessage.ErrorRes(R.string.err_cancel_withdraw_failed, listOf(id))
            }
        }
    }

    fun clearSavedBank() {
        viewModelScope.launch {
            repository.clearSavedBank()
            withdrawBankName.value = ""
            withdrawAccountHolder.value = ""
            withdrawAccountNumber.value = ""
            withdrawBranch.value = ""
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_bank_cleared)
        }
    }

    // Admin Actions
    fun loginAdmin() {
        if (adminPinInput.value == "1234" || adminPinInput.value == "7777") {
            isAdminLoggedIn.value = true
            adminPinInput.value = ""
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_admin_access_granted)
        } else {
            _uiMessage.value = UiMessage.ErrorRes(R.string.err_invalid_admin_pin)
        }
    }

    fun logoutAdmin() {
        isAdminLoggedIn.value = false
    }

    fun adminApproveDeposit(id: Long) {
        viewModelScope.launch {
            repository.approveDeposit(id)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_deposit_approved, listOf(id))
        }
    }

    fun adminRejectDeposit(id: Long) {
        viewModelScope.launch {
            repository.rejectDeposit(id)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_deposit_rejected, listOf(id))
        }
    }

    fun adminApproveWithdrawal(id: Long, payoutRef: String?) {
        viewModelScope.launch {
            repository.approveWithdrawal(id, payoutRef)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_approved, listOf(id))
        }
    }

    fun adminRejectWithdrawal(id: Long, reason: String?) {
        viewModelScope.launch {
            repository.rejectWithdrawal(id, reason)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_rejected, listOf(id))
        }
    }
}
