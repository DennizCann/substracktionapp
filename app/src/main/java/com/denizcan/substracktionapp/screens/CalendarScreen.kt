package com.denizcan.substracktionapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.denizcan.substracktionapp.components.CommonTopBar
import com.denizcan.substracktionapp.model.BillingPeriod
import com.denizcan.substracktionapp.model.Subscription
import com.denizcan.substracktionapp.util.formatCurrency
import com.denizcan.substracktionapp.util.localized
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    currentLanguage: String
) {
    var isLoading by remember { mutableStateOf(true) }
    val calendar = remember { Calendar.getInstance() }
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var subscriptions by remember { mutableStateOf<List<Subscription>>(emptyList()) }
    var paymentDays by remember { mutableStateOf<Map<Int, List<Subscription>>>(emptyMap()) }
    
    // Ay ve yıl bilgisini formatla
    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale(currentLanguage)) }
    val currentMonthYear = remember(calendar, currentLanguage) { 
        monthYearFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
    }
    
    // Aydaki gün sayısını hesapla
    val daysInMonth = remember(calendar) { 
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // Tek bir LaunchedEffect içinde hem yükleme hem hesaplama yapalım
    LaunchedEffect(Unit) {
        try {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                withContext(Dispatchers.IO) {
                    FirebaseFirestore.getInstance()
                        .collection("subscriptions")
                        .whereEqualTo("userId", user.uid)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val loadedSubscriptions = snapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(Subscription::class.java)?.also { sub ->
                                        android.util.Log.d("CalendarScreen", 
                                            "Loaded subscription: ${sub.name}, " +
                                            "Period: ${sub.billingPeriod}, " +
                                            "PaymentDay: ${sub.paymentDay}, " +
                                            "StartDate: ${sub.startDate?.toDate()}")
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("CalendarScreen", 
                                        "Error loading subscription", e)
                                    null
                                }
                            }
                            
                            subscriptions = loadedSubscriptions
                            
                            // Üyelikler yüklendikten hemen sonra ödeme günlerini hesapla
                            val payments = mutableMapOf<Int, List<Subscription>>()
                            
                            android.util.Log.d("CalendarScreen", 
                                "Calculating payments for ${loadedSubscriptions.size} subscriptions")
                            
                            loadedSubscriptions.forEach { subscription ->
                                when (subscription.billingPeriod) {
                                    BillingPeriod.WEEKLY -> {
                                        android.util.Log.d("CalendarScreen", 
                                            "Processing WEEKLY subscription: ${subscription.name}")
                                        calculateWeeklyPayments(calendar, subscription, payments)
                                    }
                                    BillingPeriod.MONTHLY -> {
                                        android.util.Log.d("CalendarScreen", 
                                            "Processing MONTHLY subscription: ${subscription.name}")
                                        calculateMonthlyPayments(calendar, subscription, payments)
                                    }
                                    else -> {
                                        android.util.Log.d("CalendarScreen", 
                                            "Processing OTHER subscription: ${subscription.name}")
                                        calculateOtherPayments(calendar, subscription, payments)
                                    }
                                }
                            }
                            
                            android.util.Log.d("CalendarScreen", 
                                "Payment days calculated: ${payments.keys.sorted()}")
                            
                            paymentDays = payments
                            isLoading = false
                        }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CalendarScreen", "Error in subscription listener", e)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "calendar".localized(currentLanguage),
                navController = navController
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
            CalendarContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                currentMonthYear = currentMonthYear,
                daysInMonth = daysInMonth,
                paymentDays = paymentDays,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                currentLanguage = currentLanguage,
                calendar = calendar,
                monthYearFormat = monthYearFormat
            )
        }
    }
}

// Yardımcı fonksiyonlar
private fun calculateWeeklyPayments(
    calendar: Calendar,
    subscription: Subscription,
    payments: MutableMap<Int, List<Subscription>>
) {
    android.util.Log.d("CalendarScreen", 
        "Weekly payment calculation started for ${subscription.name}, payment day: ${subscription.paymentDay}")

    // Ayın ilk gününden başla
    val paymentCal = Calendar.getInstance().apply {
        set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1)
    }

    // İstenen ödeme gününe kadar ilerle
    while (paymentCal.get(Calendar.DAY_OF_MONTH) < subscription.paymentDay) {
        paymentCal.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Bu ayın tüm ödeme günlerini hesapla
    while (paymentCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
        val day = paymentCal.get(Calendar.DAY_OF_MONTH)
        android.util.Log.d("CalendarScreen", 
            "Adding weekly payment for ${subscription.name} on day $day")
        
        payments[day] = (payments[day] ?: emptyList()) + subscription
        paymentCal.add(Calendar.DAY_OF_MONTH, 7)
    }
}

private fun calculateMonthlyPayments(
    calendar: Calendar,
    subscription: Subscription,
    payments: MutableMap<Int, List<Subscription>>
) {
    android.util.Log.d("CalendarScreen", 
        "Monthly payment calculation for ${subscription.name} on day ${subscription.paymentDay}")
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    if (subscription.paymentDay <= daysInMonth) {
        payments[subscription.paymentDay] = 
            (payments[subscription.paymentDay] ?: emptyList()) + subscription
        android.util.Log.d("CalendarScreen", 
            "Added monthly payment for ${subscription.name} on day ${subscription.paymentDay}")
    }
}

private fun calculateOtherPayments(
    calendar: Calendar,
    subscription: Subscription,
    payments: MutableMap<Int, List<Subscription>>
) {
    subscription.startDate?.toDate()?.let { startDate ->
        android.util.Log.d("CalendarScreen", 
            "Other payment calculation started for ${subscription.name} from ${startDate}")
        
        val startCal = Calendar.getInstance().apply { time = startDate }
        var nextPayment = startCal.clone() as Calendar
        
        // Mevcut aya gelene kadar ilerle
        while (nextPayment.get(Calendar.YEAR) <= calendar.get(Calendar.YEAR)) {
            if (nextPayment.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && 
                nextPayment.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                val day = nextPayment.get(Calendar.DAY_OF_MONTH)
                android.util.Log.d("CalendarScreen", 
                    "Adding payment for ${subscription.name} on day $day")
                payments[day] = (payments[day] ?: emptyList()) + subscription
                break
            }
            
            when (subscription.billingPeriod) {
                BillingPeriod.QUARTERLY -> nextPayment.add(Calendar.MONTH, 3)
                BillingPeriod.BIANNUALLY -> nextPayment.add(Calendar.MONTH, 6)
                BillingPeriod.YEARLY -> nextPayment.add(Calendar.YEAR, 1)
                else -> break
            }
        }
    }
}

@Composable
fun CalendarContent(
    modifier: Modifier,
    currentMonthYear: String,
    daysInMonth: Int,
    paymentDays: Map<Int, List<Subscription>>,
    selectedDay: Int?,
    onDaySelected: (Int) -> Unit,
    currentLanguage: String,
    calendar: Calendar,
    monthYearFormat: SimpleDateFormat
) {
    Column(
        modifier = modifier
    ) {
        // Ay ve yıl başlığı
        Text(
            text = currentMonthYear,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        
        // Günler grid'i
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items((1..daysInMonth).toList()) { day ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val hasPayment = paymentDays.containsKey(day)
                    
                    Surface(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxSize(),
                        shape = MaterialTheme.shapes.small,
                        color = if (hasPayment) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surface,
                        onClick = {
                            onDaySelected(day)
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (hasPayment)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
        
        // Seçili günün ödemeleri
        selectedDay?.let { day ->
            paymentDays[day]?.let { subscriptions ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (currentLanguage == "tr")
                            "$day ${monthYearFormat.format(calendar.time)} Ödemeleri"
                        else
                            "Payments for $day ${monthYearFormat.format(calendar.time)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    subscriptions.forEach { subscription ->
                        ListItem(
                            headlineContent = { Text(subscription.name) },
                            supportingContent = { 
                                Text(formatCurrency(subscription.amount))
                            }
                        )
                    }
                }
            }
        }
    }
} 