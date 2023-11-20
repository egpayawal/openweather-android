package com.egpayawal.mediapicker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.egpayawal.mediapicker.models.ImageCompression
import com.egpayawal.mediapicker.models.MediaPickerError
import com.egpayawal.mediapicker.models.MediaPickerOptions
import com.egpayawal.mediapicker.models.VideoCompression
import com.egpayawal.mediapicker.utils.FileUtils
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import java.net.URLConnection
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

typealias MediaPickerVideoQuality = com.egpayawal.mediapicker.models.VideoQuality

class MediaCompressor(
    private val context: Context,
    private val options: MediaPickerOptions,
    private val imageCompression: ImageCompression = ImageCompression(),
    private val videoCompression: VideoCompression = VideoCompression()
) {

    sealed interface State {

        data class CompressedFile(val value: File) : State

        /**
         * only applicable for video compression
         */
        data class CompressionProgress(val value: Float) : State
        data class Failed(val value: Throwable) : State
        object Cancelled : State
    }

    private val MediaPickerVideoQuality.config: VideoQuality
        get() = VideoQuality.valueOf(this.name)

    private val now: String
        get() = Instant.now()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    fun compressedFile(file: File): Flow<State> {
        val mimeType = URLConnection.guessContentTypeFromName(file.path) ?: ""
        return when {
            mimeType.startsWith("image", true) -> file.compressedImage()
            mimeType.startsWith("video", true) ->
                FileProvider
                    .getUriForFile(context, options.authority, file)
                    .compressedVideo()

            else -> {
                Log.d("MediaPickerCompressor", "unable to compress $file")
                flowOf(State.CompressedFile(file))
            }
        }
    }

    fun compressedFile(uri: Uri): Flow<State> {
        val mimeType = context.contentResolver.getType(uri).orEmpty()
        return when {
            mimeType.startsWith("image", true) ->
                FileUtils
                    .processPickedFile(context, options.providerFilePath, uri)
                    .compressedImage()

            mimeType.startsWith("video", true) -> uri.compressedVideo()

            else -> {
                Log.d("MediaPickerCompressor", "unable to compress $uri")
                flowOf(
                    State.CompressedFile(
                        FileUtils.processPickedFile(context, options.providerFilePath, uri)
                    )
                )
            }
        }
    }

    private fun File.compressedImage(): Flow<State> = flow {
        try {
            val compressFile = Compressor.compress(context, this@compressedImage) {
                resolution(imageCompression.width, imageCompression.height)
                quality(imageCompression.quality)
                format(imageCompression.format)
                imageCompression.maxFileSize?.let { size(it) }
            }
            emit(State.CompressedFile(compressFile))
        } catch (e: Throwable) {
            emit(State.Failed(e))
        }
    }

    private fun Uri.compressedVideo(): Flow<State> = callbackFlow {
        VideoCompressor.start(
            context = context,
            uris = listOf(this@compressedVideo),
            isStreamable = false,
            sharedStorageConfiguration = if (videoCompression.isSharedStorage) {
                SharedStorageConfiguration(
                    saveAt = SaveLocation.movies, // => default is movies
                    videoName = "VID_$now" // => required name
                )
            } else {
                null
            },
            appSpecificStorageConfiguration = if (!videoCompression.isSharedStorage) {
                AppSpecificStorageConfiguration(
                    videoName = "VID_$now"
                )
            } else {
                null
            },
            configureWith = Configuration(
                quality = videoCompression.quality.config,
                isMinBitrateCheckEnabled = videoCompression.isMinBitrateCheckEnabled,
                videoBitrateInMbps = videoCompression.videoBitrateInMbps,
                disableAudio = videoCompression.disableAudio,
                keepOriginalResolution = videoCompression.keepOriginalResolution,
                videoHeight = videoCompression.videoHeight,
                videoWidth = videoCompression.videoWidth
            ),
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {
                    trySend(State.CompressionProgress(percent))
                }

                override fun onStart(index: Int) {
                    // Compression start
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    trySend(State.CompressedFile(File(path)))
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    trySend(State.Failed(MediaPickerError(failureMessage)))
                }

                override fun onCancelled(index: Int) {
                    trySend(State.Cancelled)
                }
            }
        )
        awaitClose { }
    }
}
