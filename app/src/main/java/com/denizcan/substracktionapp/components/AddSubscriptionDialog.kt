package com.denizcan.substracktionapp.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.denizcan.substracktionapp.model.BillingPeriod
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.model.SubscriptionCategory
import com.denizcan.substracktionapp.util.ColorUtils
import com.denizcan.substracktionapp.util.localized
import com.google.firebase.Timestamp
import java.util.*
import java.util.Calendar
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionDialog(
    onDismiss: () -> Unit,
    onAdd: (Subscription) -> Unit,
    currentLanguage: String,
    existingSubscription: Subscription? = null
) {
    var name by remember { mutableStateOf(existingSubscription?.name ?: "") }
    var description by remember { mutableStateOf(existingSubscription?.description ?: "") }
    var amount by remember { mutableStateOf(existingSubscription?.amount?.toString() ?: "") }
    var selectedColor by remember { mutableStateOf(existingSubscription?.color?.let { Color(it) } ?: ColorUtils.subscriptionColors[0]) }
    var selectedCategory by remember { mutableStateOf(existingSubscription?.category ?: SubscriptionCategory.OTHER) }
    var selectedPeriod by remember { mutableStateOf(existingSubscription?.billingPeriod ?: BillingPeriod.MONTHLY) }
    var paymentDay by remember { mutableStateOf(existingSubscription?.paymentDay?.toString() ?: "") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var notificationDays by remember { mutableStateOf("3") }
    var showError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(existingSubscription?.startDate?.toDate()) }

    val titleText = if (existingSubscription != null) {
        "edit_subscription".localized(currentLanguage)
    } else {
        "add_subscription".localized(currentLanguage)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("subscription_name".localized(currentLanguage)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("description".localized(currentLanguage)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) amount = it },
                    label = { Text("subscription_price".localized(currentLanguage)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && amount.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "select_category".localized(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = when (selectedCategory) {
                            SubscriptionCategory.ENTERTAINMENT -> "category_entertainment"
                            SubscriptionCategory.SHOPPING -> "category_shopping"
                            SubscriptionCategory.PRODUCTIVITY -> "category_productivity"
                            SubscriptionCategory.CLOUD -> "category_cloud"
                            SubscriptionCategory.EDUCATION -> "category_education"
                            SubscriptionCategory.HEALTH -> "category_health"
                            SubscriptionCategory.GAMING -> "category_gaming"
                            SubscriptionCategory.FINANCE -> "category_finance"
                            SubscriptionCategory.COMMUNICATION -> "category_communication"
                            SubscriptionCategory.OTHER -> "category_other"
                        }.localized(currentLanguage),
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SubscriptionCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (category) {
                                            SubscriptionCategory.ENTERTAINMENT -> "category_entertainment"
                                            SubscriptionCategory.SHOPPING -> "category_shopping"
                                            SubscriptionCategory.PRODUCTIVITY -> "category_productivity"
                                            SubscriptionCategory.CLOUD -> "category_cloud"
                                            SubscriptionCategory.EDUCATION -> "category_education"
                                            SubscriptionCategory.HEALTH -> "category_health"
                                            SubscriptionCategory.GAMING -> "category_gaming"
                                            SubscriptionCategory.FINANCE -> "category_finance"
                                            SubscriptionCategory.COMMUNICATION -> "category_communication"
                                            SubscriptionCategory.OTHER -> "category_other"
                                        }.localized(currentLanguage)
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "select_color".localized(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(ColorUtils.subscriptionColors) { color ->
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { selectedColor = color },
                            shape = CircleShape,
                            color = color,
                            border = if (selectedColor == color) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null
                        ) {}
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "subscription_period".localized(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(BillingPeriod.values()) { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { selectedPeriod = period },
                            label = {
                                Text(
                                    when (period) {
                                        BillingPeriod.WEEKLY -> "weekly"
                                        BillingPeriod.MONTHLY -> "monthly"
                                        BillingPeriod.QUARTERLY -> "quarterly"
                                        BillingPeriod.BIANNUALLY -> "biannually"
                                        BillingPeriod.YEARLY -> "yearly"
                                    }.localized(currentLanguage)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedPeriod) {
                    BillingPeriod.WEEKLY, BillingPeriod.MONTHLY -> {
                        OutlinedTextField(
                            value = paymentDay,
                            onValueChange = { if (it.toIntOrNull() in 1..31 || it.isEmpty()) paymentDay = it },
                            label = { Text("payment_date".localized(currentLanguage)) },
                            modifier = Modifier.fillMaxWidth(),
                            isError = showError && paymentDay.toIntOrNull() !in 1..31,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    else -> {
                        OutlinedTextField(
                            value = startDate?.let { SimpleDateFormat("dd/MM/yyyy").format(it) } ?: "",
                            onValueChange = { },
                            label = { Text("start_date".localized(currentLanguage)) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, contentDescription = null)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("cancel".localized(currentLanguage))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (name.isBlank() || amount.toDoubleOrNull() == null) {
                                showError = true
                                return@Button
                            }
                            
                            when (selectedPeriod) {
                                BillingPeriod.WEEKLY, BillingPeriod.MONTHLY -> {
                                    if (paymentDay.toIntOrNull() !in 1..31) {
                                        showError = true
                                        return@Button
                                    }
                                }
                                else -> {
                                    if (startDate == null) {
                                        showError = true
                                        return@Button
                                    }
                                }
                            }

                            val subscription = (existingSubscription ?: Subscription()).copy(
                                name = name,
                                description = description,
                                amount = amount.toDouble(),
                                category = selectedCategory,
                                billingPeriod = selectedPeriod,
                                color = selectedColor.toArgb(),
                                paymentDay = if (selectedPeriod in listOf(BillingPeriod.WEEKLY, BillingPeriod.MONTHLY)) {
                                    paymentDay.toInt()
                                } else 1,
                                startDate = startDate?.let { Timestamp(it) },
                                nextPaymentDate = calculateNextPaymentDate(
                                    period = selectedPeriod,
                                    paymentDay = paymentDay.toIntOrNull() ?: 1,
                                    startDate = startDate
                                )
                            )

                            onAdd(subscription)
                            onDismiss()
                        }
                    ) {
                        Text("save".localized(currentLanguage))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                startDate = date
                showDatePicker = false
            },
            currentLanguage = currentLanguage
        )
    }
}

private fun calculateNextPaymentDate(
    period: BillingPeriod,
    paymentDay: Int,
    startDate: Date?
): Timestamp {
    val calendar = Calendar.getInstance()
    
    when (period) {
        BillingPeriod.WEEKLY -> {
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (paymentDay < currentDayOfWeek) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
            calendar.set(Calendar.DAY_OF_WEEK, paymentDay)
        }
        BillingPeriod.MONTHLY -> {
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            if (paymentDay < currentDay) {
                calendar.add(Calendar.MONTH, 1)
            }
            calendar.set(Calendar.DAY_OF_MONTH, paymentDay)
        }
        BillingPeriod.QUARTERLY -> {
            calendar.time = startDate ?: calendar.time
            calendar.add(Calendar.MONTH, 3)
        }
        BillingPeriod.BIANNUALLY -> {
            calendar.time = startDate ?: calendar.time
            calendar.add(Calendar.MONTH, 6)
        }
        BillingPeriod.YEARLY -> {
            calendar.time = startDate ?: calendar.time
            calendar.add(Calendar.YEAR, 1)
        }
    }

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return Timestamp(calendar.time)
}