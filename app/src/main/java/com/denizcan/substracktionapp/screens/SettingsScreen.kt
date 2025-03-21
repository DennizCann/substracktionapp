package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.data.DataStoreRepository
import com.denizcan.substracktionapp.util.localized
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentLanguage: String,
    dataStoreRepository: DataStoreRepository
) {
    var isLanguageDialogVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "settings".localized(currentLanguage),
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dil Ayarları
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "language_settings".localized(currentLanguage),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = null
                            )
                            Column {
                                Text(
                                    text = "app_language".localized(currentLanguage),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = if (currentLanguage == "tr") "Türkçe" else "English",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        TextButton(onClick = { isLanguageDialogVisible = true }) {
                            Text("change".localized(currentLanguage))
                        }
                    }
                }
            }
        }

        // Dil Seçme Dialog'u
        if (isLanguageDialogVisible) {
            AlertDialog(
                onDismissRequest = { isLanguageDialogVisible = false },
                title = { 
                    Text("select_language".localized(currentLanguage)) 
                },
                text = {
                    Column {
                        ListItem(
                            headlineContent = { Text("Türkçe") },
                            leadingContent = {
                                RadioButton(
                                    selected = currentLanguage == "tr",
                                    onClick = {
                                        scope.launch {
                                            dataStoreRepository.saveLanguage("tr")
                                            isLanguageDialogVisible = false
                                        }
                                    }
                                )
                            }
                        )
                        ListItem(
                            headlineContent = { Text("English") },
                            leadingContent = {
                                RadioButton(
                                    selected = currentLanguage == "en",
                                    onClick = {
                                        scope.launch {
                                            dataStoreRepository.saveLanguage("en")
                                            isLanguageDialogVisible = false
                                        }
                                    }
                                )
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { isLanguageDialogVisible = false }) {
                        Text("cancel".localized(currentLanguage))
                    }
                }
            )
        }
    }
} 