package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CashierRepository
import com.example.data.local.entity.BankEntity
import com.example.data.local.entity.DepositEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WithdrawalEntity
import com.example.domain.usecase.ApproveDepositUseCase
import com.example.domain.usecase.ApproveWithdrawalUseCase
import com.example.domain.usecase.CancelWithdrawalUseCase
import com.example.domain.usecase.ChangeLanguageUseCase
import com.example.domain.usecase.ClearSavedBankUseCase
import com.example.domain.usecase.RejectDepositUseCase
import com.example.domain.usecase.RejectWithdrawalUseCase
import com.example.domain.usecase.SubmitDepositUseCase
import com.example.domain.usecase.SubmitWithdrawalUseCase
import com.example.R
import com.example.security.AdminAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
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

@HiltViewModel
class CashierViewModel @Inject constructor(
    private val repository: CashierRepository,
    private val changeLanguageUseCase: ChangeLanguageUseCase,
    private val submitDepositUseCase: SubmitDepositUseCase,
    private val submitWithdrawalUseCase: SubmitWithdrawalUseCase,
    private val cancelWithdrawalUseCase: CancelWithdrawalUseCase,
    private val clearSavedBankUseCase: ClearSavedBankUseCase,
    private val approveDepositUseCase: ApproveDepositUseCase,
    private val rejectDepositUseCase: RejectDepositUseCase,
    private val approveWithdrawalUseCase: ApproveWithdrawalUseCase,
    private val rejectWithdrawalUseCase: RejectWithdrawalUseCase,
    application: Application
) : AndroidViewModel(application) {

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
    private val _withdrawPlayerId = MutableStateFlow("")
    val withdrawPlayerId = _withdrawPlayerId.asStateFlow()

    private val _withdrawAmount = MutableStateFlow("")
    val withdrawAmount = _withdrawAmount.asStateFlow()

    private val _withdrawSecretCode = MutableStateFlow("")
    val withdrawSecretCode = _withdrawSecretCode.asStateFlow()

    private val _withdrawBankName = MutableStateFlow("")
    val withdrawBankName = _withdrawBankName.asStateFlow()

    private val _withdrawAccountHolder = MutableStateFlow("")
    val withdrawAccountHolder = _withdrawAccountHolder.asStateFlow()

    private val _withdrawAccountNumber = MutableStateFlow("")
    val withdrawAccountNumber = _withdrawAccountNumber.asStateFlow()

    private val _withdrawBranch = MutableStateFlow("")
    val withdrawBranch = _withdrawBranch.asStateFlow()

    // Admin State
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn = _isAdminLoggedIn.asStateFlow()

    private val _adminPinInput = MutableStateFlow("")
    val adminPinInput = _adminPinInput.asStateFlow()

    // Global UI Message / Snackbars
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            changeLanguageUseCase(lang)
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
        val amountMinorUnits = ai?.amountValue?.let { (it * 100).toLong() } ?: 500000L
        val ref = ai?.reference ?: ""

        viewModelScope.launch {
            val result = submitDepositUseCase(
                playerId = playerId,
                bankName = bank,
                amountText = amountText,
                amountMinorUnits = amountMinorUnits,
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
        if (_withdrawPlayerId.value.isBlank()) _withdrawPlayerId.value = user.playerId
        if (_withdrawBankName.value.isBlank()) _withdrawBankName.value = user.savedBankName
        if (_withdrawAccountHolder.value.isBlank()) _withdrawAccountHolder.value = user.savedAccountHolder
        if (_withdrawAccountNumber.value.isBlank()) _withdrawAccountNumber.value = user.savedAccountNumber
        if (_withdrawBranch.value.isBlank()) _withdrawBranch.value = user.savedBranch
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

        val amountMinorUnits = (amtDouble * 100).toLong()

        if (amountMinorUnits == 0L) {
            _uiMessage.value = UiMessage.ErrorRes(R.string.err_invalid_amount)
            return
        }

        viewModelScope.launch {
            val result = submitWithdrawalUseCase(
                playerId = pId,
                amountMinorUnits = amountMinorUnits,
                secretCode = secret,
                bankName = bank,
                accountHolder = holder,
                accountNumber = accNo,
                branch = branch
            )

            result.onSuccess {
                _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_submitted)
                _withdrawAmount.value = ""
                _withdrawSecretCode.value = ""
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
            val success = cancelWithdrawalUseCase(id)
            if (success) {
                _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_cancelled, listOf(id))
            } else {
                _uiMessage.value = UiMessage.ErrorRes(R.string.err_cancel_withdraw_failed, listOf(id))
            }
        }
    }

    fun clearSavedBank() {
        viewModelScope.launch {
            clearSavedBankUseCase()
            _withdrawBankName.value = ""
            _withdrawAccountHolder.value = ""
            _withdrawAccountNumber.value = ""
            _withdrawBranch.value = ""
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_bank_cleared)
        }
    }

    fun setWithdrawPlayerId(id: String) {
        _withdrawPlayerId.value = id
    }

    fun setWithdrawAmount(amount: String) {
        _withdrawAmount.value = amount
    }

    fun setWithdrawSecretCode(secretCode: String) {
        _withdrawSecretCode.value = secretCode
    }

    fun setWithdrawBankName(bankName: String) {
        _withdrawBankName.value = bankName
    }

    fun setWithdrawAccountHolder(holder: String) {
        _withdrawAccountHolder.value = holder
    }

    fun setWithdrawAccountNumber(accountNumber: String) {
        _withdrawAccountNumber.value = accountNumber
    }

    fun setWithdrawBranch(branch: String) {
        _withdrawBranch.value = branch
    }

    fun setAdminPinInput(pin: String) {
        _adminPinInput.value = pin
    }

    // Admin Actions
    fun loginAdmin() {
        if (AdminAuth.verifyPin(adminPinInput.value)) {
            _isAdminLoggedIn.value = true
            _adminPinInput.value = ""
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_admin_access_granted)
        } else {
            _uiMessage.value = UiMessage.ErrorRes(R.string.err_invalid_admin_pin)
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun adminApproveDeposit(id: Long) {
        viewModelScope.launch {
            approveDepositUseCase(id)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_deposit_approved, listOf(id))
        }
    }

    fun adminRejectDeposit(id: Long) {
        viewModelScope.launch {
            rejectDepositUseCase(id)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_deposit_rejected, listOf(id))
        }
    }

    fun adminApproveWithdrawal(id: Long, payoutRef: String?) {
        viewModelScope.launch {
            approveWithdrawalUseCase(id, payoutRef)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_approved, listOf(id))
        }
    }

    fun adminRejectWithdrawal(id: Long, reason: String?) {
        viewModelScope.launch {
            rejectWithdrawalUseCase(id, reason)
            _uiMessage.value = UiMessage.SuccessRes(R.string.msg_withdraw_rejected, listOf(id))
        }
    }
}
