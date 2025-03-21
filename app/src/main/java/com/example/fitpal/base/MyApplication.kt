package com.example.fitpal.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class MyApplication: Application() {
    @SuppressLint("StaticFieldLeak")
    object Globals {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
        var x = FirebaseApp.initializeApp(applicationContext)
        val y = 3
   }
}