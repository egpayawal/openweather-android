package com.egpayawal.module.local.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE
import androidx.security.crypto.MasterKey.DEFAULT_MASTER_KEY_ALIAS

object LocalSharedPrefProvider {

    fun create(application: Application, isDebug: Boolean): SharedPreferences {
        val filename = application.packageName + "_preferences"

        return if (isDebug) {
            application.getSharedPreferences(filename, Context.MODE_PRIVATE)
        } else {
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()

            val masterKey = MasterKey.Builder(application.applicationContext, DEFAULT_MASTER_KEY_ALIAS)
                .setKeyGenParameterSpec(keyGenParameterSpec)
                .build()

            EncryptedSharedPreferences
                .create(
                    application,
                    filename,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
        }
    }
}
