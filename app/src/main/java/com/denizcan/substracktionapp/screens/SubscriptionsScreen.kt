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

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "subscriptions".localized(currentLanguage),
                navController = navController,
                actions = {
                    IconButton(onClick = { showDialog = true }) {
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
                scope.launch {
                    try {
                        if (selectedSubscription != null) {
                            // Düzenleme
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
                        } else {
                            // Yeni ekleme
                            db.collection("subscriptions")
                                .add(updatedSubscription.copy(
                                    userId = currentUser?.uid ?: return@launch
                                ))
                                .await()
                        }
                    } catch (e: Exception) {
                        // Hata durumunu handle et
                    }
                }
            },
            currentLanguage = currentLanguage,
            existingSubscription = selectedSubscription
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