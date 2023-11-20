package com.egpayawal.module.local

import android.app.Application
import android.content.SharedPreferences
import com.egpayawal.module.local.utils.LocalSharedPrefProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return LocalSharedPrefProvider.create(application, BuildConfig.DEBUG)
    }
}
