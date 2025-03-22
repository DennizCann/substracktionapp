package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.model.BillingPeriod
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.navigation.Screen
import com.denizcan.substracktionapp.util.localized
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*
import com.denizcan.substracktionapp.components.NotificationsDialog
import kotlinx.coroutines.launch
import com.denizcan.substracktionapp.data.DataStoreRepository
import com.denizcan.substracktionapp.util.ColorUtils
import com.denizcan.substracktionapp.util.formatCurrency
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    currentLanguage: String,
    dataStoreRepository: DataStoreRepository
) {
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showNotifications by remember { mutableStateOf(false) }
    var subscriptions by remember { mutableStateOf<List<Subscription>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var country by remember { mutableStateOf("TR") }
    var currency by remember { mutableStateOf("TRY") }
    var showActiveSubscriptions by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }
    
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Kullanıcı tercihlerini yükle
    LaunchedEffect(Unit) {
        dataStoreRepository.getCountry().collect { country = it }
        dataStoreRepository.getCurrency().collect { currency = it }
    }

    // Üyelikleri yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("subscriptions")
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener

                    snapshot?.let { documents ->
                        subscriptions = documents.mapNotNull { doc ->
                            doc.toObject(Subscription::class.java)
                        }
                    }
                    isLoading = false
                }
        }
    }

    // Kullanıcı bilgilerini yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            try {
                val doc = db.collection("users").document(user.uid).get().await()
                if (doc.exists()) {
                    userName = doc.getString("name") ?: ""
                    profileImageBase64 = doc.getString("profileImage")
                }
            } catch (e: Exception) {
                // Hata durumunu handle et
            }
        }
    }

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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showNotifications = true }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Hoşgeldin Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profil Fotoğrafı
                        if (profileImageBase64 != null) {
                            val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .size(32.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Hoşgeldin Mesajı
                        Column {
                            Text(
                                text = "welcome_back".localized(currentLanguage),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "subscription_summary".localized(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Özet Kartları
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Aylık Maliyet Kartı
                    SummaryCard(
                        title = "monthly_cost".localized(currentLanguage),
                        amount = calculateMonthlyCost(subscriptions),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Yıllık Maliyet Kartı
                    SummaryCard(
                        title = "yearly_cost".localized(currentLanguage),
                        amount = calculateYearlyCost(subscriptions),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Aktif Üyelik Kartı
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    onClick = { showActiveSubscriptions = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "active_subscriptions".localized(currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = subscriptions.count { it.isActive }.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Yaklaşan Ödemeler başlığı eklenebilir...
            }
        }
    }

    if (showNotifications) {
        NotificationsDialog(
            notifications = emptyList(),
            onDismiss = { showNotifications = false },
            currentLanguage = currentLanguage
        )
    }

    if (showActiveSubscriptions) {
        ActiveSubscriptionsDialog(
            subscriptions = subscriptions.filter { it.isActive },
            onDismiss = { showActiveSubscriptions = false },
            currentLanguage = currentLanguage
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    modifier: Modifier = Modifier,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = when {
                        amount >= 1000000 -> 22.sp
                        amount >= 100000 -> 24.sp
                        else -> 26.sp
                    }
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun calculateMonthlyCost(subscriptions: List<Subscription>): Double {
    return subscriptions.sumOf { subscription: Subscription ->
        when (subscription.billingPeriod) {
            BillingPeriod.WEEKLY -> subscription.amount * 4.0     // Ayda ortalama 4 hafta
            BillingPeriod.MONTHLY -> subscription.amount
            BillingPeriod.QUARTERLY -> subscription.amount / 3.0
            BillingPeriod.BIANNUALLY -> subscription.amount / 6.0
            BillingPeriod.YEARLY -> subscription.amount / 12.0
        }
    }
}

private fun calculateYearlyCost(subscriptions: List<Subscription>): Double {
    return subscriptions.sumOf { subscription: Subscription ->
        when (subscription.billingPeriod) {
            BillingPeriod.WEEKLY -> subscription.amount * 52.0    // Yılda 52 hafta
            BillingPeriod.MONTHLY -> subscription.amount * 12.0
            BillingPeriod.QUARTERLY -> subscription.amount * 4.0
            BillingPeriod.BIANNUALLY -> subscription.amount * 2.0
            BillingPeriod.YEARLY -> subscription.amount
        }
    }
}

@Composable
private fun ActiveSubscriptionsDialog(
    subscriptions: List<Subscription>,
    onDismiss: () -> Unit,
    currentLanguage: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("active_subscriptions".localized(currentLanguage))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                subscriptions.forEach { subscription ->
                    ListItem(
                        headlineContent = {
                            Text(subscription.name)
                        },
                        supportingContent = {
                            Column {
                                Text(
                                    text = formatCurrency(subscription.amount),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = when (subscription.billingPeriod) {
                                        BillingPeriod.WEEKLY -> "weekly"
                                        BillingPeriod.MONTHLY -> "monthly"
                                        BillingPeriod.QUARTERLY -> "quarterly"
                                        BillingPeriod.BIANNUALLY -> "biannually"
                                        BillingPeriod.YEARLY -> "yearly"
                                    }.localized(currentLanguage),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingContent = {
                            // Üyeliğin baş harfini içeren daire
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color(subscription.color ?: ColorUtils.subscriptionColors[0].toArgb())
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = subscription.name.firstOrNull()?.uppercase() ?: "?",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    )
                    if (subscription != subscriptions.last()) {
                        Divider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("close".localized(currentLanguage))
            }
        }
    )
} 