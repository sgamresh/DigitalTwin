package com.example.digitaltwin

import android.app.Application
import com.example.digitaltwin.di.AppContainer

class DigitalTwinApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}

