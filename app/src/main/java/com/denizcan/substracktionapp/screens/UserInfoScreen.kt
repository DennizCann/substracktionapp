package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.model.Country
import com.denizcan.substracktionapp.model.CountryData
import com.denizcan.substracktionapp.model.PlanType
import com.denizcan.substracktionapp.navigation.Screen
import com.denizcan.substracktionapp.util.localized
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    navController: NavController,
    currentLanguage: String
) {
    var name by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    // Google hesabıyla giriş yapıldıysa ismi otomatik doldur
    LaunchedEffect(Unit) {
        currentUser?.displayName?.let {
            if (it.isNotEmpty()) {
                name = it
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("profile_info".localized(currentLanguage))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "welcome_message".localized(currentLanguage),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { 
                    Text("your_name".localized(currentLanguage)) 
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.isEmpty() && error != null
            )

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
                        .menuAnchor(),
                    isError = selectedCountry == null && error != null
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

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    if (name.isEmpty() || selectedCountry == null) {
                        error = "please_fill_all".localized(currentLanguage)
                        return@Button
                    }

                    isLoading = true
                    error = null

                    currentUser?.let { user ->
                        val userInfo = hashMapOf(
                            "name" to name,
                            "country" to selectedCountry!!.code,
                            "currency" to selectedCountry!!.currency.code,
                            "currencySymbol" to selectedCountry!!.currency.symbol,
                            "profileCompleted" to true,
                            "planType" to PlanType.FREE.name,
                            "subscriptionCount" to 0,
                            "isTrialActive" to false
                        )

                        db.collection("users")
                            .document(user.uid)
                            .set(userInfo)
                            .addOnSuccessListener {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.UserInfo.route) { inclusive = true }
                                }
                            }
                            .addOnFailureListener { e ->
                                error = "error_saving".localized(currentLanguage)
                                isLoading = false
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("continue".localized(currentLanguage))
                }
            }
        }
    }
} 