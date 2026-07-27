package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ContactHelper {
    const val DEFAULT_WHATSAPP_NUMBER = "94776763093"
    const val DEFAULT_TELEGRAM_HANDLE = "fast_xbet"
    const val DEFAULT_SUPPORT_EMAIL = "support@fastxbet.com"

    fun openWhatsApp(
        context: Context,
        phoneNumber: String = DEFAULT_WHATSAPP_NUMBER,
        message: String = ""
    ) {
        val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").trim()
        val encodedMsg = if (message.isNotBlank()) Uri.encode(message) else ""

        val urisToTry = mutableListOf<Uri>()
        if (encodedMsg.isNotBlank()) {
            urisToTry.add(Uri.parse("whatsapp://send?phone=$cleanNumber&text=$encodedMsg"))
            urisToTry.add(Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMsg"))
            urisToTry.add(Uri.parse("https://wa.me/$cleanNumber?text=$encodedMsg"))
        } else {
            urisToTry.add(Uri.parse("whatsapp://send?phone=$cleanNumber"))
            urisToTry.add(Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber"))
            urisToTry.add(Uri.parse("https://wa.me/$cleanNumber"))
        }

        var opened = false
        for (uri in urisToTry) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                opened = true
                break
            } catch (e: Exception) {
                // Try next URI variant
            }
        }

        if (!opened) {
            Toast.makeText(context, "WhatsApp open à¶šà·’à¶»à·“à¶¸à¶§ à¶±à·œà·„à·à¶šà·’ à·€à·’à¶º (Phone: +$cleanNumber)", Toast.LENGTH_LONG).show()
        }
    }

    fun openTelegram(
        context: Context,
        handle: String = DEFAULT_TELEGRAM_HANDLE
    ) {
        val cleanHandle = handle.replace("@", "").trim()
        val urisToTry = listOf(
            Uri.parse("tg://resolve?domain=$cleanHandle"),
            Uri.parse("https://t.me/$cleanHandle")
        )

        var opened = false
        for (uri in urisToTry) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                opened = true
                break
            } catch (e: Exception) {
                // Try next URI
            }
        }

        if (!opened) {
            Toast.makeText(context, "Telegram open à¶šà·’à¶»à·“à¶¸à¶§ à¶±à·œà·„à·à¶šà·’ à·€à·’à¶º (@$cleanHandle)", Toast.LENGTH_LONG).show()
        }
    }

    fun openEmail(
        context: Context,
        email: String = DEFAULT_SUPPORT_EMAIL,
        subject: String = "",
        body: String = ""
    ) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Email à¶ºà·™à¶¯à·”à¶¸ à¶±à·œà¶¸à·à¶­ ($email)", Toast.LENGTH_SHORT).show()
        }
    }
}
