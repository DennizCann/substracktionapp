package com.denizcan.substracktionapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.denizcan.substracktionapp.model.Notification
import com.denizcan.substracktionapp.model.NotificationType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsDialog(
    notifications: List<Notification>,
    onDismiss: () -> Unit,
    currentLanguage: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dialog başlığı
                TopAppBar(
                    title = { 
                        Text(if (currentLanguage == "tr") "Bildirimler" else "Notifications") 
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentLanguage == "tr") 
                                "Bildirim bulunmamaktadır" 
                            else 
                                "No notifications",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(notifications.sortedByDescending { it.timestamp }) { notification ->
                            NotificationItem(
                                notification = notification,
                                currentLanguage = currentLanguage
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    currentLanguage: String
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bildirim tipine göre icon
        Icon(
            imageVector = when (notification.type) {
                NotificationType.PAYMENT_DUE -> Icons.Default.Payment
                NotificationType.PRICE_CHANGE -> Icons.Default.PriceChange
                NotificationType.SUBSCRIPTION_END -> Icons.Default.EventBusy
            },
            contentDescription = null,
            tint = if (!notification.isRead) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (!notification.isRead) 
                    MaterialTheme.colorScheme.onSurface 
                else 
                    MaterialTheme.colorScheme.outline
            )
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (!notification.isRead) 
                    MaterialTheme.colorScheme.onSurfaceVariant 
                else 
                    MaterialTheme.colorScheme.outline
            )
            Text(
                text = dateFormat.format(notification.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
} 