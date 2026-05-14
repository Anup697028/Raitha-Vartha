package com.raitha.vartha

import android.app.Application
import com.google.firebase.FirebaseApp

class RaithaVarthaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
