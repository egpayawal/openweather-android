package com.egpayawal.module.network.di

import android.content.Context
import android.content.Intent
import com.egpayawal.module.network.BuildConfig
import com.egpayawal.module.network.R
import com.egpayawal.module.network.utils.API
import com.egpayawal.module.network.utils.ENDPOINT_FORMAT
import com.egpayawal.module.network.utils.VERSION
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private fun providesIoExceptionInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            try {
                chain.proceed(chain.request())
            } catch (exception: IOException) {
                context.applicationContext.sendBroadcast(
                    Intent(
                        context.getString(R.string.intent_action_io_exception)
                    )
                )

                throw exception
            }
        }
    }

    @Provides
    @Singleton
    fun providesChuckerInterceptor(@ApplicationContext context: Context) =
        ChuckerInterceptor
            .Builder(context)
            .collector(ChuckerCollector(context, false))
            .maxContentLength(200_000L)
            .alwaysReadResponseBody(true)
            .build()

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @ApplicationContext context: Context,
        authenticatedInterceptor: Interceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            logging.redactHeader("Cookie")
            builder.addInterceptor(logging)
        }

        builder
            .addInterceptor(authenticatedInterceptor)
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(providesIoExceptionInterceptor(context))
            .retryOnConnectionFailure(true)
            .connectTimeout(50, TimeUnit.SECONDS)
            .callTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)

        return builder.build()
    }

    @Singleton
    @Provides
    fun providesCustomHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build()
            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(
                String.format(
                    ENDPOINT_FORMAT,
                    BuildConfig.BASE_URL,
                    API,
                    VERSION
                )
            )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }
}
