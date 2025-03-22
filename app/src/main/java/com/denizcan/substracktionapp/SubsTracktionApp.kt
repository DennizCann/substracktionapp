package com.denizcan.substracktionapp

import android.app.Application
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen

class SubsTracktionApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Firebase'i başlat
        FirebaseApp.initializeApp(this)
        
        // Google Play Services'in kullanılabilir olup olmadığını kontrol et
        try {
            val availability = GoogleApiAvailability.getInstance()
            val resultCode = availability.isGooglePlayServicesAvailable(this)
            if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
                // Log hata durumunu
                android.util.Log.e("SubsTracktionApp", "Google Play Services is not available")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        AndroidThreeTen.init(this)
    }
} 