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
import com.denizcan.substracktionapp.model.PlanType
import com.denizcan.substracktionapp.model.User
import com.denizcan.substracktionapp.navigation.Screen
import com.denizcan.substracktionapp.util.localized
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.denizcan.substracktionapp.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentLanguage: String,
    dataStoreRepository: DataStoreRepository
) {
    var isLanguageDialogVisible by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }
    var userPlanInfo by remember { mutableStateOf<User?>(null) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }
    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Kullanıcı bilgilerini yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .get()
                .await()
                .toObject(User::class.java)
                ?.let { userPlanInfo = it }
        }
        
        // Bildirim durumunu yükle
        dataStoreRepository.getNotificationsEnabled().collect {
            notificationsEnabled = it
        }
    }

    // Mevcut tema tercihini yükle
    LaunchedEffect(Unit) {
        dataStoreRepository.getThemeMode().collect { theme ->
            currentTheme = ThemeMode.fromString(theme)
        }
    }

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

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Bildirim ayarları
            Text(
                text = "notifications".localized(currentLanguage),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "notification_reminders".localized(currentLanguage),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "notification_description".localized(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        if (userPlanInfo?.planType == PlanType.PREMIUM) {
                            scope.launch {
                                dataStoreRepository.setNotificationsEnabled(enabled)
                            }
                        } else {
                            showUpgradeDialog = true
                        }
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Tema Ayarları
            Text(
                text = "theme_settings".localized(currentLanguage),
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
                        when (currentTheme) {
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                            ThemeMode.SYSTEM -> Icons.Default.SettingsSuggest
                        },
                        contentDescription = null
                    )
                    Column {
                        Text(
                            text = "app_theme".localized(currentLanguage),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = when (currentTheme) {
                                ThemeMode.LIGHT -> "theme_light"
                                ThemeMode.DARK -> "theme_dark"
                                ThemeMode.SYSTEM -> "theme_system"
                            }.localized(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = { showThemeDialog = true }) {
                    Text("change".localized(currentLanguage))
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

        // Premium dialog
        if (showUpgradeDialog) {
            AlertDialog(
                onDismissRequest = { showUpgradeDialog = false },
                title = { Text("premium_feature".localized(currentLanguage)) },
                text = { Text("notifications_premium_message".localized(currentLanguage)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showUpgradeDialog = false
                            navController.navigate(Screen.Premium.route)
                        }
                    ) {
                        Text("upgrade_to_premium".localized(currentLanguage))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUpgradeDialog = false }) {
                        Text("close".localized(currentLanguage))
                    }
                }
            )
        }
    }

    // Tema Seçme Dialog'u
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("select_theme".localized(currentLanguage)) },
            text = {
                Column {
                    ThemeMode.values().forEach { theme ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    when (theme) {
                                        ThemeMode.LIGHT -> "theme_light"
                                        ThemeMode.DARK -> "theme_dark"
                                        ThemeMode.SYSTEM -> "theme_system"
                                    }.localized(currentLanguage)
                                )
                            },
                            leadingContent = {
                                RadioButton(
                                    selected = currentTheme == theme,
                                    onClick = {
                                        scope.launch {
                                            dataStoreRepository.setThemeMode(theme.name.lowercase())
                                            showThemeDialog = false
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("cancel".localized(currentLanguage))
                }
            }
        )
    }
} 