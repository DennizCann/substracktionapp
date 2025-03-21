package com.denizcan.substracktionapp.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.denizcan.substracktionapp.util.localized
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit,
    currentLanguage: String
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("ok".localized(currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("cancel".localized(currentLanguage))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
} 