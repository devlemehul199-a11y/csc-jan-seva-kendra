package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.credentials.CredentialManager
import com.example.data.model.AppLanguage
import com.example.data.model.VleProfile
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.SyncStatus
import com.example.util.Localization
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun SettingsScreen(
    profile: VleProfile,
    language: AppLanguage,
    currentUser: FirebaseUser?,
    syncStatus: SyncStatus,
    syncMessage: String,
    onLanguageChange: (AppLanguage) -> Unit,
    onProfileUpdate: (VleProfile) -> Unit,
    onSyncToCloud: () -> Unit,
    onRestoreFromCloud: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    onSignOut: () -> Unit,
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit,
    onRestoreCsv: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var showProfileDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onRestoreCsv(uri)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Center Settings & Cloud Sync",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Cloud Account & Authentication Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cloud_auth_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (currentUser != null) IncomeGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                                    contentDescription = "Cloud Status",
                                    tint = if (currentUser != null) IncomeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (currentUser != null) "Cloud Account Connected" else "Local Offline Mode",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentUser?.email ?: "Not signed in • Data stored locally",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (currentUser != null) {
                            IconButton(
                                onClick = onSignOut,
                                modifier = Modifier.testTag("sign_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentUser == null) {
                        Button(
                            onClick = onOpenAuthDialog,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CscNavyDark),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("connect_google_account_btn")
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = "Sync")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign in with Google for Cloud Sync")
                        }
                    } else {
                        // Cloud Sync & Restore Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onSyncToCloud,
                                enabled = syncStatus != SyncStatus.SYNCING,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sync_to_cloud_btn")
                            ) {
                                if (syncStatus == SyncStatus.SYNCING) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync to Cloud", fontSize = 12.5.sp)
                            }

                            OutlinedButton(
                                onClick = onRestoreFromCloud,
                                enabled = syncStatus != SyncStatus.SYNCING,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("restore_from_cloud_btn")
                            ) {
                                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Download")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restore Cloud", fontSize = 12.5.sp)
                            }
                        }

                        if (syncMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = syncMessage,
                                fontSize = 11.5.sp,
                                color = if (syncStatus == SyncStatus.ERROR) MaterialTheme.colorScheme.error else IncomeGreen
                            )
                        }
                    }
                }
            }
        }

        // VLE Center Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CscNavyDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = "Center",
                                    tint = CscGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = profile.centerName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "VLE: ${profile.vleName} • CSC ID: ${profile.cscId}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { showProfileDialog = true },
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Opening Cash Float",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${profile.openingCash.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Opening Bank Float",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${profile.openingBank.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Language Selection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "App Language (ભાષા / भाषा)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    AppLanguage.values().forEach { lang ->
                        val isSelected = language == lang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageChange(lang) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onLanguageChange(lang) },
                                modifier = Modifier.testTag("radio_lang_${lang.code}")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = lang.displayName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Excel & PDF Backup Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Data Backup, Export & Restore",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Export your Day-Book register as standard Excel (CSV) or PDF summary report.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Export PDF Button
                    Button(
                        onClick = onExportPdf,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_export_pdf_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CscNavyDark)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.exportPdf(language))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Export CSV Button
                    OutlinedButton(
                        onClick = onExportCsv,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_export_csv_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "CSV")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.exportCsv(language))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Restore CSV Button
                    OutlinedButton(
                        onClick = { filePickerLauncher.launch("text/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_restore_csv_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Upload, contentDescription = "Restore")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.restoreCsv(language))
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        EditProfileDialog(
            currentProfile = profile,
            onDismiss = { showProfileDialog = false },
            onSave = { updated ->
                onProfileUpdate(updated)
                showProfileDialog = false
            }
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentProfile: VleProfile,
    onDismiss: () -> Unit,
    onSave: (VleProfile) -> Unit
) {
    var centerName by remember { mutableStateOf(currentProfile.centerName) }
    var vleName by remember { mutableStateOf(currentProfile.vleName) }
    var cscId by remember { mutableStateOf(currentProfile.cscId) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var cashFloat by remember { mutableStateOf(currentProfile.openingCash.toInt().toString()) }
    var bankFloat by remember { mutableStateOf(currentProfile.openingBank.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Center & VLE Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = centerName,
                    onValueChange = { centerName = it },
                    label = { Text("Center Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = vleName,
                    onValueChange = { vleName = it },
                    label = { Text("VLE Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cscId,
                    onValueChange = { cscId = it },
                    label = { Text("CSC ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cashFloat,
                        onValueChange = { cashFloat = it },
                        label = { Text("Starting Cash (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bankFloat,
                        onValueChange = { bankFloat = it },
                        label = { Text("Starting Bank (₹)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            centerName = centerName.trim(),
                            vleName = vleName.trim(),
                            cscId = cscId.trim(),
                            phone = phone.trim(),
                            openingCash = cashFloat.toDoubleOrNull() ?: 0.0,
                            openingBank = bankFloat.toDoubleOrNull() ?: 0.0
                        )
                    )
                }
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
