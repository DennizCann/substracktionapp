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
import com.denizcan.substracktionapp.model.Country
import com.denizcan.substracktionapp.model.CountryData
import com.denizcan.substracktionapp.util.localized
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    currentLanguage: String
) {
    var name by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Kullanıcı bilgilerini yükle
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            try {
                val doc = db.collection("users").document(user.uid).get().await()
                if (doc.exists()) {
                    name = doc.getString("name") ?: ""
                    val countryCode = doc.getString("country")
                    selectedCountry = CountryData.countries.find { it.code == countryCode }
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "profile".localized(currentLanguage),
                navController = navController,
                actions = {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                currentUser?.let { user ->
                                    isLoading = true
                                    error = null
                                    
                                    val userInfo = hashMapOf(
                                        "name" to name,
                                        "country" to (selectedCountry?.code ?: ""),
                                        "currency" to (selectedCountry?.currency?.code ?: ""),
                                        "currencySymbol" to (selectedCountry?.currency?.symbol ?: ""),
                                        "profileCompleted" to true
                                    )

                                    db.collection("users")
                                        .document(user.uid)
                                        .update(userInfo.toMap())
                                        .addOnSuccessListener {
                                            isEditing = false
                                            isLoading = false
                                        }
                                        .addOnFailureListener { e ->
                                            error = e.localizedMessage
                                            isLoading = false
                                        }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "save".localized(currentLanguage)
                            )
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "edit".localized(currentLanguage)
                            )
                        }
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profil başlığı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isEditing) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { 
                                    Text("your_name".localized(currentLanguage)) 
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        } else {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }

                // Ülke bilgisi
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "country_info".localized(currentLanguage),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isEditing) {
                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedCountry?.name ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { 
                                        Text("your_country".localized(currentLanguage)) 
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    CountryData.countries.forEach { country ->
                                        DropdownMenuItem(
                                            text = { 
                                                Text("${country.name} (${country.currency.code})") 
                                            },
                                            onClick = {
                                                selectedCountry = country
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            Column {
                                Text(
                                    text = selectedCountry?.name ?: "",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = selectedCountry?.currency?.let { 
                                        "${it.name} (${it.symbol})" 
                                    } ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
} 