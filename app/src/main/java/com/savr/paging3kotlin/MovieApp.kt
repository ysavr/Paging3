package com.savr.paging3kotlin

import android.app.Application
import com.savr.paging3kotlin.repository.local.AppDatabase
import com.savr.paging3kotlin.repository.local.LocalInjector

class MovieApp: Application() {
    override fun onCreate() {
        super.onCreate()
        LocalInjector.appDatabase = AppDatabase.getInstance(this@MovieApp)
    }
}