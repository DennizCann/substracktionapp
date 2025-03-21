package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.denizcan.substracktionapp.model.Notification
import com.denizcan.substracktionapp.model.NotificationType
import java.util.*
import com.denizcan.substracktionapp.components.NotificationsDialog
import com.denizcan.substracktionapp.util.localized

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    currentLanguage: String
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showNotifications by remember { mutableStateOf(false) }
    
    // Boş bildirim listesi
    val notifications = remember { listOf<Notification>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "app_name".localized(currentLanguage),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("profile".localized(currentLanguage)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(Screen.Profile.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Subscriptions, contentDescription = null) },
                    label = { Text("subscriptions".localized(currentLanguage)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(Screen.Subscriptions.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    label = { Text("analytics".localized(currentLanguage)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(Screen.Analytics.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    label = { Text("calendar".localized(currentLanguage)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(Screen.Calendar.route)
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("settings".localized(currentLanguage)) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(Screen.Settings.route)
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    label = { Text("sign_out".localized(currentLanguage)) },
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.LoginOptions.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("app_name".localized(currentLanguage)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showNotifications = true }) {
                            BadgedBox(
                                badge = {
                                    val unreadCount = notifications.count { !it.isRead }
                                    if (unreadCount > 0) {
                                        Badge { Text(unreadCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "notifications".localized(currentLanguage)
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "home".localized(currentLanguage),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showNotifications) {
        NotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotifications = false },
            currentLanguage = currentLanguage
        )
    }
} 