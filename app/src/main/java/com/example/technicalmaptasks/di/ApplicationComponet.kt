package com.example.technicalmaptasks.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationComponet : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}