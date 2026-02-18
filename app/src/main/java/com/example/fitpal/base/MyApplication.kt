package com.example.fitpal.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MyApplication: Application() {
    @SuppressLint("StaticFieldLeak")
    object Globals {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
        FirebaseApp.initializeApp(this)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }
}