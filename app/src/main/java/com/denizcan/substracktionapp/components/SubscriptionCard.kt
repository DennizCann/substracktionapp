package com.denizcan.substracktionapp.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denizcan.substracktionapp.model.BillingPeriod
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.util.localized
import com.denizcan.substracktionapp.util.ColorUtils
import com.denizcan.substracktionapp.util.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
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
            // onClick özelliğini kaldırdık
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf: Harf içeren daire ve isim
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = when (subscription.billingPeriod) {
                            BillingPeriod.WEEKLY -> "weekly".localized(currentLanguage)
                            BillingPeriod.MONTHLY -> "monthly".localized(currentLanguage)
                            BillingPeriod.YEARLY -> "yearly".localized(currentLanguage)
                            BillingPeriod.QUARTERLY -> "quarterly".localized(currentLanguage)
                            BillingPeriod.BIANNUALLY -> "biannually".localized(currentLanguage)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Sağ taraf: Fiyat ve menü
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatCurrency(subscription.amount),
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("edit".localized(currentLanguage)) },
                        onClick = {
                            showMenu = false
                            onEditClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("delete".localized(currentLanguage)) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
} 