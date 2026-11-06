package com.example.ui.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionType
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.Localization
import com.example.util.ParsedVoiceResult
import com.example.util.SpeechParser

@Composable
fun VoiceInputDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onApply: (ParsedVoiceResult) -> Unit
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    var spokenText by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf<ParsedVoiceResult?>(null) }
    var isListening by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                spokenText = spoken
                parsedResult = SpeechParser.parse(spoken)
            }
        }
    }

    fun startListening() {
        isListening = true
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage.speechLocale)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage.speechLocale)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, selectedLanguage.speechLocale)
            putExtra(RecognizerIntent.EXTRA_PROMPT, Localization.voicePrompt(selectedLanguage))
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            isListening = false
            // Speech recognizer not available, allow typing
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("voice_input_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.voiceEntry(selectedLanguage),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = Localization.cancel(selectedLanguage)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Language Selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = selectedLanguage == lang
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CscNavyDark else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { selectedLanguage = lang }
                        ) {
                            Text(
                                text = lang.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Big Animated Microphone Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isListening) CscGold else CscNavyDark)
                        .clickable { startListening() }
                        .testTag("voice_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isListening) Localization.listening(selectedLanguage) else "Tap Mic to Speak",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isListening) CscGold else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = Localization.voicePrompt(selectedLanguage),
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Editable Spoken Text Input
                OutlinedTextField(
                    value = spokenText,
                    onValueChange = {
                        spokenText = it
                        parsedResult = if (it.isNotBlank()) SpeechParser.parse(it) else null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("spoken_text_input"),
                    placeholder = { Text("Or type sentence here...") },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                // Parsed Preview Card
                AnimatedVisibility(visible = parsedResult != null) {
                    parsedResult?.let { result ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Extracted Details:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Amount: ₹${result.amount ?: "--"}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (result.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                    )
                                    Text(
                                        text = result.type.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (result.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Category: ${result.category.defaultName} • Mode: ${result.paymentMode.name}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (result.customerName.isNotBlank()) {
                                    Text(
                                        text = "Customer: ${result.customerName}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Localization.cancel(selectedLanguage))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val res = parsedResult ?: if (spokenText.isNotBlank()) SpeechParser.parse(spokenText) else null
                            if (res != null) {
                                onApply(res)
                            }
                        },
                        enabled = parsedResult != null || spokenText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = CscNavyDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("apply_voice_entry_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Apply",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Day-Book")
                    }
                }
            }
        }
    }
}
