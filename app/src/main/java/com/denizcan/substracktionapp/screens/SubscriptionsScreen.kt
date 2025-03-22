package com.denizcan.substracktionapp.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.model.SubscriptionCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Subscriptions
import com.denizcan.substracktionapp.components.AddSubscriptionDialog
import com.denizcan.substracktionapp.components.CategoryHeader
import com.denizcan.substracktionapp.components.SubscriptionCard
import com.denizcan.substracktionapp.util.localized
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.denizcan.substracktionapp.data.DataStoreRepository
import com.denizcan.substracktionapp.model.User
import com.denizcan.substracktionapp.model.PlanType
import com.denizcan.substracktionapp.navigation.Screen
import com.google.firebase.firestore.FieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    navController: NavController,
    currentLanguage: String,
    dataStoreRepository: DataStoreRepository
) {
    var subscriptions by remember { mutableStateOf<List<Subscription>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedSubscription by remember { mutableStateOf<Subscription?>(null) }
    
    var country by remember { mutableStateOf("TR") }
    var currency by remember { mutableStateOf("TRY") }
    
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var showUpgradeDialog by remember { mutableStateOf(false) }
    var userPlanInfo by remember { mutableStateOf<User?>(null) }

    // Kullanıcı tercihlerini yükle
    LaunchedEffect(Unit) {
        dataStoreRepository.getCountry().collect { country = it }
        dataStoreRepository.getCurrency().collect { currency = it }
    }

    // Kullanıcı bilgilerini yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            try {
                // Kullanıcı dokümanını gerçek zamanlı dinleyelim
                db.collection("users")
                    .document(user.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        snapshot?.let { doc ->
                            userPlanInfo = User(
                                id = user.uid,
                                name = doc.getString("name") ?: "",
                                email = user.email ?: "",
                                planType = PlanType.valueOf(doc.getString("planType") ?: PlanType.FREE.name),
                                subscriptionCount = doc.getLong("subscriptionCount")?.toInt() ?: 0,
                                isTrialActive = doc.getBoolean("isTrialActive") ?: false
                            )
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Üyelikleri yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            db.collection("subscriptions")
                .whereEqualTo("userId", user.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Hata durumunu handle et
                        return@addSnapshotListener
                    }

                    snapshot?.let { documents ->
                        subscriptions = documents.mapNotNull { doc ->
                            doc.toObject(Subscription::class.java)
                        }.sortedBy { it.nextPaymentDate.toDate() }
                    }
                    isLoading = false
                }
        }
    }

    // Önce addSubscription fonksiyonunu tanımlayalım
    fun addSubscription(subscription: Subscription) {
        scope.launch {
            try {
                currentUser?.let { user ->
                    // Önce üyeliği ekle
                    db.collection("subscriptions")
                        .add(subscription.copy(userId = user.uid))
                        .await()

                    // Sonra sayacı güncelle
                    db.collection("users")
                        .document(user.uid)
                        .update("subscriptionCount", FieldValue.increment(1))
                        .await()

                    // Dialog'u kapat
                    showDialog = false
                }
            } catch (e: Exception) {
                // Hata durumunu handle et
                e.printStackTrace()
            }
        }
    }

    // Sonra checkSubscriptionLimit fonksiyonunu tanımlayalım
    fun checkSubscriptionLimit(subscription: Subscription) {
        userPlanInfo?.let { user ->
            when (user.planType) {
                PlanType.FREE -> {
                    if (user.subscriptionCount >= 10) {
                        // Premium olmayan kullanıcı limit aşımında
                        showUpgradeDialog = true
                    } else {
                        // Limiti aşmamış, üyelik eklenebilir
                        addSubscription(subscription)
                    }
                }
                PlanType.PREMIUM -> {
                    // Premium kullanıcı sınırsız ekleyebilir
                    addSubscription(subscription)
                }
            }
        } ?: run {
            // Kullanıcı bilgisi yoksa varsayılan olarak FREE plan kabul edelim
            if (subscriptions.size >= 10) {
                showUpgradeDialog = true
            } else {
                addSubscription(subscription)
            }
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "subscriptions".localized(currentLanguage),
                navController = navController,
                actions = {
                    IconButton(
                        onClick = {
                            // Kullanıcı premium değil VE 10 üyeliği varsa direkt Premium sayfasına git
                            if (userPlanInfo?.planType == PlanType.FREE && subscriptions.size >= 10) {
                                navController.navigate(Screen.Premium.route)
                                return@IconButton // Burada işlemi sonlandır
                            }
                            
                            // Diğer tüm durumlarda dialog'u göster
                            showDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "add_subscription".localized(currentLanguage)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (subscriptions.isEmpty()) {
            EmptySubscriptionsView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                currentLanguage = currentLanguage,
                onAddClick = { showDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Kategoriye göre grupla
                SubscriptionCategory.values().forEach { category ->
                    val categorySubscriptions = subscriptions.filter { it.category == category }
                    if (categorySubscriptions.isNotEmpty()) {
                        item {
                            CategoryHeader(
                                category = category,
                                currentLanguage = currentLanguage
                            )
                        }
                        
                        items(
                            items = categorySubscriptions,
                            key = { it.id }
                        ) { subscription ->
                            var showDeleteDialog by remember { mutableStateOf(false) }
                            
                            SubscriptionCard(
                                subscription = subscription,
                                currentLanguage = currentLanguage,
                                onEditClick = {
                                    selectedSubscription = subscription
                                    showDialog = true
                                },
                                onDeleteClick = { showDeleteDialog = true }
                            )
                            
                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("confirm_delete".localized(currentLanguage)) },
                                    text = { Text("delete_subscription_confirm".localized(currentLanguage)) },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        db.collection("subscriptions")
                                                            .whereEqualTo("id", subscription.id)
                                                            .whereEqualTo("userId", currentUser?.uid)
                                                            .get()
                                                            .await()
                                                            .documents
                                                            .firstOrNull()
                                                            ?.reference
                                                            ?.delete()
                                                            ?.await()
                                                        
                                                        showDeleteDialog = false
                                                    } catch (e: Exception) {
                                                        // Hata durumunu handle et
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("yes".localized(currentLanguage))
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = { showDeleteDialog = false }
                                        ) {
                                            Text("no".localized(currentLanguage))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog gösterimi
    if (showDialog) {
        AddSubscriptionDialog(
            onDismiss = {
                showDialog = false
                selectedSubscription = null
            },
            onAdd = { updatedSubscription ->
                if (selectedSubscription != null) {
                    // Düzenleme
                    scope.launch {
                        try {
                            db.collection("subscriptions")
                                .whereEqualTo("id", selectedSubscription?.id)
                                .whereEqualTo("userId", currentUser?.uid)
                                .get()
                                .await()
                                .documents
                                .firstOrNull()
                                ?.reference
                                ?.set(updatedSubscription)
                                ?.await()
                            
                            showDialog = false
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    // Yeni ekleme
                    checkSubscriptionLimit(updatedSubscription)
                }
            },
            currentLanguage = currentLanguage,
            existingSubscription = selectedSubscription
        )
    }

    // Premium dialog'unu güncelleyelim
    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = { 
                Text(
                    "subscription_limit_reached".localized(currentLanguage),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = { 
                Column {
                    Text("subscription_limit_message".localized(currentLanguage))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "current_subscription_count".localized(currentLanguage)
                            .format(userPlanInfo?.subscriptionCount ?: subscriptions.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
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
                TextButton(
                    onClick = { showUpgradeDialog = false }
                ) {
                    Text("close".localized(currentLanguage))
                }
            }
        )
    }
}

@Composable
private fun EmptySubscriptionsView(
    modifier: Modifier = Modifier,
    currentLanguage: String,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Subscriptions,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "no_subscriptions".localized(currentLanguage),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "add_first_subscription".localized(currentLanguage),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddClick) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("add_subscription".localized(currentLanguage))
        }
    }
}

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    currentLanguage: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ... diğer kodlar aynı ...
        }
    }
} 