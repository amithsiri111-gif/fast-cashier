package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryGold
import com.example.ui.util.ContactHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSupportScreen(
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var playerId by remember { mutableStateOf("") }
    var issueType by remember { mutableStateOf("Deposit Delay") }

    fun launchWhatsAppSupport() {
        val message = "Hello FastXbet Support! I need help with my transaction.\n" +
                "Player ID: ${if (playerId.isNotBlank()) playerId else "Not specified"}\n" +
                "Issue: $issueType"
        ContactHelper.openWhatsApp(context = context, message = message)
    }

    fun launchTelegramSupport() {
        ContactHelper.openTelegram(context = context)
    }

    fun launchEmailSupport() {
        ContactHelper.openEmail(
            context = context,
            subject = "FastXbet Support Inquiry - Player ID $playerId",
            body = "Hello Support Team,\n\nPlayer ID: $playerId\nIssue: $issueType\n\nPlease assist me."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button if provided
        if (onNavigateBack != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Customer Support",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Top Status Header Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HeadsetMic,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "24/7 Live Support",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "FastXbet Official Care",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Online Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "à¶…à¶´à¶œà·š à¶´à·à¶»à·’à¶·à·à¶œà·’à¶š à·ƒà·„à·à¶º à·ƒà·šà·€à·à·€ à·ƒà¶­à·’à¶ºà·š à¶¯à·’à¶± 7à¶¸ à¶´à·à¶º 24 à¶´à·”à¶»à·à¶¸ à¶šà·Šâ€à¶»à·’à¶ºà·à¶­à·Šà¶¸à¶š à·€à·š. à¶­à·à¶±à·Šà¶´à¶­à·”, à¶¸à·”à¶¯à¶½à·Š à¶†à¶´à·ƒà·” à¶œà·à¶±à·“à¶¸à·Š à·„à· à·€à·™à¶±à¶­à·Š à¶œà·à¶§à¶½à·” à·ƒà¶³à·„à· à¶…à¶´ à·„à· à·ƒà¶¸à·Šà¶¶à¶±à·Šà¶° à·€à¶±à·Šà¶±.\n(Our dedicated support team is available 24/7 to assist with your deposits, withdrawals, and account inquiries.)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }

        // Instant Support Channels Section
        Text(
            text = "à·ƒà·„à·à¶º à¶±à·à¶½à·’à¶šà· (Support Channels)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // WhatsApp Channel Card
        SupportChannelCard(
            title = "WhatsApp Direct Support",
            subtitle = "+94 77 676 3093 (à¶šà·Šà·‚à¶«à·’à¶š à·ƒà·„à·à¶º à·ƒà¶³à·„à· WhatsApp à¶·à·à·€à·’à¶­à· à¶šà¶»à¶±à·Šà¶±)",
            tag = "24/7 Fast Response",
            icon = Icons.Default.PhoneInTalk,
            brandColor = Color(0xFF25D366),
            buttonText = "Open WhatsApp (+94776763093)",
            testTag = "support_whatsapp_btn",
            onClick = { launchWhatsAppSupport() }
        )

        // Telegram Channel Card
        SupportChannelCard(
            title = "Telegram Official Support",
            subtitle = "@fast_xbet (à¶­à·à¶±à·Šà¶´à¶­à·”/à¶†à¶´à·ƒà·” à¶œà·à¶±à·“à¶¸à·Š à·ƒà·„à·à¶ºà¶š Telegram à¶ à·à¶±à¶½à¶º)",
            tag = "Official Channel",
            icon = Icons.AutoMirrored.Filled.Send,
            brandColor = Color(0xFF229ED9),
            buttonText = "Open Telegram (@fast_xbet)",
            testTag = "support_telegram_btn",
            onClick = { launchTelegramSupport() }
        )

        // Email Support Card
        SupportChannelCard(
            title = "Email Support",
            subtitle = "support@fastxbet.com",
            tag = "Official Inquiry",
            icon = Icons.Default.Email,
            brandColor = PrimaryGreen,
            buttonText = "Send Email",
            testTag = "support_email_btn",
            onClick = { launchEmailSupport() }
        )

        // Quick Transaction Help Tool
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "à¶œà¶±à·”à¶¯à·™à¶±à·” à·ƒà·„à·à¶º à¶½à¶¶à·à¶œà·à¶±à·“à¶¸ (Quick Support Helper)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "à¶”à¶¶à¶œà·š 1xBet Player ID à¶‘à¶š à¶‡à¶­à·”à·…à¶­à·Š à¶šà¶» à·ƒà·„à·à¶ºà¶š à¶šà¶«à·Šà¶©à·à¶ºà¶¸ à·€à·™à¶­ à¶šà·Šà·‚à¶«à·’à¶š à¶´à¶«à·’à·€à·’à¶©à¶ºà¶šà·Š à¶ºà·€à¶±à·Šà¶±:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = playerId,
                    onValueChange = { playerId = it },
                    label = { Text("1xBet Player ID (e.g. 849204812)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("support_player_id_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val copyText = "Hello FastXbet Support,\nPlayer ID: ${if (playerId.isNotBlank()) playerId else "N/A"}\nIssue: $issueType"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Support Template", copyText))
                            Toast.makeText(context, "Message template copied!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy Template",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { launchWhatsAppSupport() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Send WhatsApp",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // FAQs Section
        Text(
            text = "à¶±à·’à¶­à¶» à¶…à·ƒà¶± à¶´à·Šâ€à¶»à·à·Šà¶± (Common Support Questions)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        FaqItemCard(
            question = "à¶­à·à¶±à·Šà¶´à¶­à·”à·€ à¶œà·’à¶«à·”à¶¸à¶§ à¶‘à¶šà¶­à·” à·€à·“à¶¸à¶§ à¶šà·œà¶´à¶¸à¶« à·€à·šà¶½à·à·€à¶šà·Š à¶œà¶­à·€à·šà¶¯? (How long does deposit take?)",
            answer = "à·ƒà·à¶¸à·à¶±à·Šâ€à¶ºà¶ºà·™à¶±à·Š à·€à·’à¶±à·à¶©à·’ 5 à·ƒà·’à¶§ 15 à¶¯à¶šà·Šà·€à· à¶šà·à¶½à¶ºà¶šà·Š à¶‡à¶­à·”à·…à¶­ à¶­à·à¶±à·Šà¶´à¶­à·” à·ƒà·à¶»à·Šà¶®à¶šà·€ à·ƒà·’à¶¯à·” à·€à·š. à¶»à·’à·ƒà·’à¶§à·Šà¶´à¶­ à¶´à·à·„à·à¶¯à·’à¶½à·’à·€ upload à¶šà¶» à¶‡à¶­à·’ à¶¶à·€ à¶­à·„à·€à·”à¶»à·” à¶šà¶»à¶œà¶±à·Šà¶±."
        )

        FaqItemCard(
            question = "à¶¸à·”à¶¯à¶½à·Š à¶†à¶´à·ƒà·” à¶œà·à¶±à·“à¶¸ à¶´à·Šâ€à¶»à¶¸à·à¶¯ à·€à·”à·€à·„à·œà¶­à·Š à¶šà·”à¶¸à¶šà·Š à¶šà·… à¶ºà·”à¶­à·”à¶¯? (What if withdrawal is delayed?)",
            answer = "1xBet Cashier à¶´à¶¯à·Šà¶°à¶­à·’à¶º à¶¸à¶œà·’à¶±à·Š à¶½à¶¶à· à¶¯à·”à¶±à·Š Withdrawal Code à¶‘à¶š à·ƒà·„ à¶”à¶¶à¶œà·š Player ID à¶‘à¶š à¶…à¶´à¶œà·š WhatsApp à·ƒà·„à·à¶ºà¶š à¶šà¶«à·Šà¶©à·à¶ºà¶¸ à·€à·™à¶­ à¶ºà·€à¶±à·Šà¶±."
        )

        FaqItemCard(
            question = "FastXbet Cashier à¶±à·’à¶½ à·ƒà·šà·€à·à·€à¶šà·Šà¶¯? (Is FastXbet official?)",
            answer = "à¶”à·€à·Š, FastXbet à¶ºà¶±à·” à·à·Šâ€à¶»à·“ à¶½à¶‚à¶šà·à·€ à·ƒà¶³à·„à· à·€à¶± à¶±à·’à¶½ 1xBet Cashier à·ƒà·„à¶šà¶»à·” à·€à·š. à¶”à¶¶à¶œà·š à¶œà¶±à·”à¶¯à·™à¶±à·” 100% à¶†à¶»à¶šà·Šà·‚à·’à¶­ à·€à·š."
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SupportChannelCard(
    title: String,
    subtitle: String,
    tag: String,
    icon: ImageVector,
    brandColor: Color,
    buttonText: String,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, brandColor.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = brandColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = brandColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = brandColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag(testTag)
            ) {
                Text(
                    text = buttonText,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun FaqItemCard(
    question: String,
    answer: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.QuestionAnswer,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = answer,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 26.dp)
            )
        }
    }
}
