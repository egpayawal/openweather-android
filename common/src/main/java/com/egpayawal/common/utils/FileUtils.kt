package com.egpayawal.common.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID
import kotlin.math.min
import kotlin.math.roundToInt

object FileUtils {

    fun getUrlForResource(
        applicationId: String,
        resourceId: Int
    ): String {
        return Uri.parse("android.resource://$applicationId/$resourceId")
            .toString()
    }

    const val TEMP_FILE_PREFIX = "the_baseplate_temp_"
    const val PHOTO_EXTENSION = "jpg"

    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        filename: String,
        quality: Int,
        recycle: Boolean
    ): Uri {
        val fileOutputStream: FileOutputStream
        val photo = File(context.filesDir.path, filename)
        val mimeType = getMimeType(context, Uri.parse(photo.absolutePath))
        Timber.d("mimeType: %s", mimeType)

        try {
            fileOutputStream = FileOutputStream(photo)

            val bos = BufferedOutputStream(fileOutputStream)
            var compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG

            if (mimeType.isNotBlank()) {
                val lowerMimeType = mimeType.toLowerCase(Locale.getDefault())

                if (lowerMimeType.contains("png")) {
                    compressFormat = Bitmap.CompressFormat.PNG
                } else if (lowerMimeType.contains("webp")) {
                    compressFormat = Bitmap.CompressFormat.WEBP
                }
            }

            bitmap.compress(compressFormat, quality, bos)
            Timber.d("photo saved: %s", photo.toString())

            try {
                bos.flush()
                bos.close()
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: IOException) {
                Timber.e(e, "Error in cleaning up streams: %s", filename)
            }
        } catch (e: IOException) {
            Timber.e(e, "IOException in saving bitmap: %s", filename)
        } catch (e: Exception) {
            Timber.e(e, "Error in saving bitmap: %s", filename)
        }

        if (recycle) {
            bitmap.recycle()
        }

        return Uri.parse("file:" + photo.absolutePath)
    }

    fun getMimeType(
        context: Context,
        uri: Uri
    ): String {
        var mimeType: String?

        if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            mimeType = cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase(Locale.getDefault())
            )

            if (mimeType.isNullOrEmpty()) {
                mimeType = if ("json" == fileExtension) {
                    "application/json"
                } else {
                    "text/$fileExtension"
                }
            }
        }

        return mimeType ?: ""
    }

    suspend fun deleteFile(filePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            val path = filePath.replaceFirst("[^:]+:".toRegex(), "")

            Timber.d("Deleting file: %s", path)

            try {
                val fileToDelete = File(path)
                fileToDelete.delete()
            } catch (e: Exception) {
                Timber.e(e, "Error in deleting file: %s", path)
                throw e
            }
        }
    }

    suspend fun scaledownBitmap(bitmap: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        return withContext(Dispatchers.Default) {
            if (bitmap.width < maxImageSize && bitmap.height < maxImageSize) {
                return@withContext bitmap
            }

            val ratio = min(maxImageSize / bitmap.width, maxImageSize / bitmap.height)
            val width = (ratio * bitmap.width).roundToInt()
            val height = (ratio * bitmap.height).roundToInt()

            Bitmap.createScaledBitmap(bitmap, width, height, filter)
        }
    }

    suspend fun scaleDownPhoto(
        photoPath: String,
        context: Context,
        maxDimension: Float,
        compressionQuality: Int,
        filter: Boolean
    ): Uri {
        Timber.d("resizePhoto start --> path: %s", photoPath)

        val photoUri = Uri.parse(photoPath)

        val futureTarget = Glide.with(context)
            .asBitmap()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .load(photoUri)
            .submit()

        val bitmap = withContext(Dispatchers.Default) {
            futureTarget.get()
        }

        if (bitmap == null) {
            Timber.d("cannot convert")
            return photoUri
        }

        if (bitmap.width < maxDimension && bitmap.height < maxDimension) {
            return photoUri
        } else {
            val ratio = min(maxDimension / bitmap.width, maxDimension / bitmap.height)
            val width = (ratio * bitmap.width).roundToInt()
            val height = (ratio * bitmap.height).roundToInt()
            val scaledDownBitmap = Bitmap.createScaledBitmap(bitmap, width, height, filter)
            val scaledDownUri = saveBitmap(
                context,
                scaledDownBitmap,
                photoUri.lastPathSegment!!,
                compressionQuality,
                true
            )

            Timber.d(
                "resizePhotos finish --> path: %s, scaledDown: %s",
                photoUri,
                scaledDownUri
            )

            return scaledDownUri
        }
    }

    fun getOutputDirectory(context: Context, appName: String): String {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(
                it,
                appName
            ).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir.absolutePath
        } else {
            appContext.filesDir.absolutePath
        }
    }

    fun createTempFile(context: Context, extension: String, appName: String) =
        File(getOutputDirectory(context, appName), "$TEMP_FILE_PREFIX${UUID.randomUUID()}.$extension")

    fun deleteTempFiles(context: Context, appName: String) {
        File(getOutputDirectory(context, appName))
            .walk()
            .filter { it.name.contains(TEMP_FILE_PREFIX) }
            .forEach { file ->
                if (file.extension == PHOTO_EXTENSION) {
                    deleteFileFromMediaStore(file, context)
                } else {
                    file.delete()
                }
            }
    }

    private fun deleteFileFromMediaStore(file: File, context: Context) {
        // Set up the projection (we only need the ID)
        val projection =
            arrayOf<String>(MediaStore.Images.Media._ID)

        // Match on the file path
        val selection: String = MediaStore.Images.Media.DATA + " = ?"
        val selectionArgs =
            arrayOf<String>(file.absolutePath)

        // Query for the ID of the media matching the file path
        val queryUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val c: Cursor? =
            context.contentResolver.query(
                queryUri,
                projection,
                selection,
                selectionArgs,
                null
            )

        c?.let {
            if (c.moveToFirst()) {
                val id: Long =
                    c.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                val deleteUri: Uri =
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                context.contentResolver.delete(deleteUri, null, null)
            }

            c.close()
        }
    }
}
