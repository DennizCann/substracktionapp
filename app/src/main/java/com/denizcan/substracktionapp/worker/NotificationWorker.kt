package com.denizcan.substracktionapp.worker

import android.content.Context
import androidx.work.*
import com.denizcan.substracktionapp.notification.NotificationHelper
import com.denizcan.substracktionapp.util.localized
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val subscriptionName = inputData.getString("subscription_name") ?: return Result.failure()
        val amount = inputData.getString("amount") ?: return Result.failure()

        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showPaymentReminder(
            title = "payment_reminder".localized("tr"),
            message = "payment_reminder_message".localized("tr")
                .format(subscriptionName, amount)
        )

        return Result.success()
    }

    companion object {
        fun schedule(
            context: Context,
            subscriptionName: String,
            amount: String,
            notificationTime: Long
        ) {
            val data = workDataOf(
                "subscription_name" to subscriptionName,
                "amount" to amount
            )

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data)
                .setInitialDelay(notificationTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "subscription_notification_$subscriptionName",
                    ExistingWorkPolicy.REPLACE,
                    notificationWork
                )
        }
    }
} 