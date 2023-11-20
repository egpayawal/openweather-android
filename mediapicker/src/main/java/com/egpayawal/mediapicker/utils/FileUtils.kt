package com.egpayawal.mediapicker.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.InputStream
import java.util.UUID

object FileUtils {

    fun processPickedFile(context: Context, providerPath: String, uri: Uri): File {
        val fileName = resolveFileName(context, uri)
        return saveFile(context, uri, fileName, providerPath)
    }

    fun resolveFileName(context: Context, uri: Uri): String {
        val nameColumn = MediaStore.Images.Media.DISPLAY_NAME

        val projection = arrayOf(nameColumn)

        context.contentResolver.query(uri, projection, null, null, null)?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(nameColumn))
            }
        }

        Log.w("MediaPicker", "Failed to resolve data")
        return UUID.randomUUID().toString()
    }

    fun saveFile(context: Context, uri: Uri, fileName: String, providerPath: String): File {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val destination = privateFile(context, providerPath, fileName)
            destination.copyInputStreamToFile(inputStream)
            return destination
        }
        throw IllegalStateException("Failed to process picked file")
    }

    fun privateFile(context: Context, directory: String, fileName: String): File {
        val dir = File(context.filesDir, directory)
        dir.mkdirs()
        return File(dir, fileName)
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
}
