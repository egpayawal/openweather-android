package com.egpayawal.mediapicker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.egpayawal.mediapicker.models.CapturedData
import com.egpayawal.mediapicker.models.MediaPickerCancelled
import com.egpayawal.mediapicker.models.MediaPickerError
import com.egpayawal.mediapicker.models.MediaPickerOptions
import com.egpayawal.mediapicker.models.MediaPickerOwner
import com.egpayawal.mediapicker.utils.FileUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume

class MediaPicker(
    private val owner: MediaPickerOwner,
    private val options: MediaPickerOptions,
    maxVisualMediaItems: Int? = null
) {

    private var onTakeOrCaptureResult: ((Boolean) -> Unit)? = null
    private var onPickResult: ((Result<Uri>) -> Unit)? = null
    private var onMultiplePickResult: ((Result<List<Uri>>) -> Unit)? = null
    private var onPermissionResult: ((deniedPermissions: List<String>) -> Unit)? = null

    private var startWithFrontCamera: Boolean = false

    private val now: String
        get() = Instant.now()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    private val imageName: String
        get() = "IMG_$now.jpg"

    private val videoName: String
        get() = "VID_$now.mp4"

    private fun File.getToFileUri(): Uri = FileProvider.getUriForFile(
        owner.context.applicationContext,
        options.authority,
        this
    )

    private val permissionRequest = owner.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { map ->
        val deniedPermissions = map.entries
            .filter { !it.value }
            .map { it.key }

        onPermissionResult?.invoke(deniedPermissions)
    }

    private val pickVisualMediaContract = owner.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val result = uri?.let { Result.success(it) } ?: Result.failure(MediaPickerCancelled)
        onPickResult?.invoke(result)
    }

    private val pickMultipleVisualMediaContract = owner.registerForActivityResult(
        // hack to set max items as contract uses an internal function. TODO improve
        maxVisualMediaItems?.let {
            ActivityResultContracts.PickMultipleVisualMedia(it)
        } ?: ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        val result = if (uris.isNotEmpty()) {
            Result.success(uris)
        } else {
            Result.failure(MediaPickerCancelled)
        }
        onMultiplePickResult?.invoke(result)
    }

    private val takePictureContract = owner.registerForActivityResult(
        object : ActivityResultContracts.TakePicture() {
            override fun createIntent(context: Context, input: Uri): Intent {
                val intent = super.createIntent(context, input)
                if (startWithFrontCamera) {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                    intent.putExtra("camerafacing", "front")
                    intent.putExtra("previous_mode", "front")
                }
                return intent
            }
        }
    ) { success ->
        onTakeOrCaptureResult?.invoke(success)
    }

    private val captureVideoContract = owner.registerForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        onTakeOrCaptureResult?.invoke(success)
    }

    suspend fun pickVisualMedia(
        mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
    ): Result<Uri> = suspendCancellableCoroutine {
        onPickResult = { result ->
            if (it.isActive) {
                it.resume(result)
            }
        }
        pickVisualMediaContract.launch(PickVisualMediaRequest(mediaType))
    }

    suspend fun pickMultipleVisualMedia(
        mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
    ): Result<List<Uri>> = suspendCancellableCoroutine {
        onMultiplePickResult = { result ->
            if (it.isActive) {
                it.resume(result)
            }
        }
        pickMultipleVisualMediaContract.launch(PickVisualMediaRequest(mediaType))
    }

    suspend fun takePicture(
        startWithFrontCamera: Boolean = false
    ): Result<CapturedData> = suspendCancellableCoroutine {
        this.startWithFrontCamera = startWithFrontCamera

        val file = FileUtils.privateFile(
            context = owner.context,
            directory = options.providerFilePath,
            fileName = imageName
        )
        val fileUri = file.getToFileUri()

        onTakeOrCaptureResult = { success ->
            val result = if (success) {
                Result.success(CapturedData(fileUri, file))
            } else {
                Result.failure(MediaPickerCancelled)
            }
            if (it.isActive) {
                it.resume(result)
            }
        }

        onPermissionResult = { deniedPermissions ->
            if (deniedPermissions.isEmpty()) {
                takePictureContract.launch(fileUri)
            } else {
                if (it.isActive) {
                    it.resume(Result.failure(MediaPickerError("permissions denied: $deniedPermissions")))
                }
            }
        }

        permissionRequest.launch(arrayOf(Manifest.permission.CAMERA))
    }

    suspend fun captureVideo(): Result<CapturedData> = suspendCancellableCoroutine {
        val file = FileUtils.privateFile(
            context = owner.context,
            directory = options.providerFilePath,
            fileName = videoName
        )
        val fileUri = file.getToFileUri()

        onTakeOrCaptureResult = { success ->
            val result = if (success) {
                Result.success(CapturedData(fileUri, file))
            } else {
                Result.failure(MediaPickerCancelled)
            }
            if (it.isActive) {
                it.resume(result)
            }
        }

        onPermissionResult = { deniedPermissions ->
            if (deniedPermissions.isEmpty()) {
                captureVideoContract.launch(fileUri)
            } else {
                it.resume(Result.failure(MediaPickerError("permissions denied: $deniedPermissions")))
            }
        }

        permissionRequest.launch(arrayOf(Manifest.permission.CAMERA))
    }

    fun convertUriToFile(uris: List<Uri>): List<File> {
        return uris.map { convertUriToFile(it) }
    }

    fun convertUriToFile(uri: Uri): File {
        return FileUtils.processPickedFile(owner.context, options.providerFilePath, uri)
    }
}
