package com.egpayawal.module.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesBasePlateApiService(
        retrofit: Retrofit
    ): BaseplateApiServices = retrofit.create(BaseplateApiServices::class.java)
}
