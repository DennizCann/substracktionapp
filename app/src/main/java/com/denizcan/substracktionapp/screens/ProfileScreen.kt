package com.denizcan.substracktionapp.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import com.denizcan.substracktionapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    currentLanguage: String
) {
    var name by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var profileImageBase64 by remember { mutableStateOf<String?>(null) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                // Uri'den Bitmap oluştur
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                // Bitmap'i yeniden boyutlandır
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true)

                // Base64'e dönüştür
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)

                // Firestore'a kaydet
                isLoading = true
                currentUser?.let { user ->
                    db.collection("users").document(user.uid)
                        .update("profileImage", base64String)
                        .addOnSuccessListener {
                            profileImageBase64 = base64String
                            isLoading = false
                        }
                        .addOnFailureListener { e ->
                            error = e.localizedMessage
                            isLoading = false
                        }
                }
            } catch (e: Exception) {
                error = e.localizedMessage
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
                    name = doc.getString("name") ?: ""
                    val countryCode = doc.getString("country")
                    selectedCountry = CountryData.countries.find { it.code == countryCode }
                    profileImageBase64 = doc.getString("profileImage")
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profil Fotoğrafı Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { launcher.launch("image/*") }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageBase64 != null) {
                            val imageBytes = Base64.decode(profileImageBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.size(120.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(24.dp)
                                        .size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // İsim Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showNameDialog = true }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "your_name".localized(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Ülke Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showCountryDialog = true }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "your_country".localized(currentLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        selectedCountry?.let { country ->
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${country.currency.name} (${country.currency.symbol})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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

                Spacer(modifier = Modifier.weight(1f)) // Boşluk ekleyerek silme butonunu en alta alıyoruz

                // Hesap Silme Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    onClick = { showDeleteAccountDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Column {
                            Text(
                                text = "delete_account".localized(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "delete_account_warning".localized(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }

    // İsim değiştirme dialogu
    if (showNameDialog) {
        var editedName by remember { mutableStateOf(name) }
        
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("your_name".localized(currentLanguage)) },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("your_name".localized(currentLanguage)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            currentUser?.let { user ->
                                isLoading = true
                                db.collection("users")
                                    .document(user.uid)
                                    .update("name", editedName)
                                    .addOnSuccessListener {
                                        name = editedName
                                        isLoading = false
                                        showNameDialog = false
                                    }
                                    .addOnFailureListener { e ->
                                        error = e.localizedMessage
                                        isLoading = false
                                    }
                            }
                        }
                    }
                ) {
                    Text("save".localized(currentLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("cancel".localized(currentLanguage))
                }
            }
        )
    }

    // Ülke seçme dialogu
    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = { Text("your_country".localized(currentLanguage)) },
            text = {
                Column {
                    CountryData.countries.forEach { country ->
                        ListItem(
                            headlineContent = { 
                                Text("${country.name} (${country.currency.code})") 
                            },
                            leadingContent = {
                                RadioButton(
                                    selected = selectedCountry == country,
                                    onClick = {
                                        currentUser?.let { user ->
                                            isLoading = true
                                            val updates = mapOf(
                                                "country" to country.code,
                                                "currency" to country.currency.code,
                                                "currencySymbol" to country.currency.symbol
                                            ) as Map<String, Any>
                                            
                                            db.collection("users")
                                                .document(user.uid)
                                                .update(updates)
                                                .addOnSuccessListener {
                                                    selectedCountry = country
                                                    isLoading = false
                                                    showCountryDialog = false
                                                }
                                                .addOnFailureListener { e ->
                                                    error = e.localizedMessage
                                                    isLoading = false
                                                }
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryDialog = false }) {
                    Text("close".localized(currentLanguage))
                }
            }
        )
    }

    // İlk Uyarı Dialog'u
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = {
                Text("delete_account_title".localized(currentLanguage))
            },
            text = {
                Text("delete_account_message".localized(currentLanguage))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        showDeleteConfirmDialog = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("continue".localized(currentLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("cancel".localized(currentLanguage))
                }
            }
        )
    }

    // Son Onay Dialog'u
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = { 
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("delete_account_confirm_title".localized(currentLanguage))
            },
            text = {
                Text("delete_account_confirm_message".localized(currentLanguage))
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentUser?.let { user ->
                            isLoading = true
                            // Önce üyelikleri sil
                            db.collection("subscriptions")
                                .whereEqualTo("userId", user.uid)
                                .get()
                                .addOnSuccessListener { documents ->
                                    documents.forEach { doc ->
                                        doc.reference.delete()
                                    }
                                    // Sonra kullanıcı bilgilerini sil
                                    db.collection("users")
                                        .document(user.uid)
                                        .delete()
                                        .addOnSuccessListener {
                                            // En son Firebase Auth'dan kullanıcıyı sil
                                            user.delete()
                                                .addOnSuccessListener {
                                                    FirebaseAuth.getInstance().signOut()
                                                    navController.navigate(Screen.LoginOptions.route) {
                                                        popUpTo(Screen.Home.route) { inclusive = true }
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    error = e.localizedMessage
                                                    isLoading = false
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            error = e.localizedMessage
                                            isLoading = false
                                        }
                                }
                                .addOnFailureListener { e ->
                                    error = e.localizedMessage
                                    isLoading = false
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("delete_account_confirm_button".localized(currentLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("cancel".localized(currentLanguage))
                }
            }
        )
    }
} 