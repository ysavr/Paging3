package com.savr.paging3kotlin.repository.local

object LocalInjector {
    var appDatabase: AppDatabase? = null

    fun injectDb(): AppDatabase? {
        return appDatabase
    }
}