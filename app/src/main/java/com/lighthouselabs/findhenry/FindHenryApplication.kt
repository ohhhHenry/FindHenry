package com.lighthouselabs.findhenry

import android.app.Application
import com.lighthouselabs.findhenry.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FindHenryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@FindHenryApplication)
            modules(appModule)
        }
    }

}