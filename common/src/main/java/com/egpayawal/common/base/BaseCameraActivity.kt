package com.egpayawal.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.ninjaTap
import com.egpayawal.common.utils.launchWithTimber
import com.egpayawal.mediapicker.MediaPicker
import com.egpayawal.mediapicker.models.MediaPickerOptions
import com.egpayawal.mediapicker.models.MediaPickerOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

abstract class BaseCameraActivity<B : ViewDataBinding, VM : BaseViewModel> :
    BaseViewModelActivity<B, VM>() {

    private var captureCameraPaths: MutableList<File> = mutableListOf()

    val cameraPathUrls: List<File> get() = captureCameraPaths
    private lateinit var imagePickerBottomSheetDialog: BottomSheetDialog

    private val mediaPicker: MediaPicker by lazy {
        MediaPicker(
            owner = MediaPickerOwner.OwnerActivity(this),
            options = MediaPickerOptions(
                authority = "$packageName.fileprovider",
                providerFilePath = "."
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupImagePickerDialog()
    }

    @SuppressLint("ResourceType")
    private fun setupImagePickerDialog() {
        imagePickerBottomSheetDialog = BottomSheetDialog(this)

        val dialogView = this.layoutInflater.inflate(R.layout.bottom_sheet_upload_image, null)

        imagePickerBottomSheetDialog.setContentView(dialogView)

        val takePhoto = dialogView.findViewById<AppCompatButton>(R.id.btnTakePhoto)
        val gallery = dialogView.findViewById<AppCompatButton>(R.id.btnGallery)
        val cancelBtn = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)

        takePhoto
            .ninjaTap(lifecycleScope) {
                hidePickerDialog()
                lifecycleScope.launchWithTimber {
                    mediaPicker.takePicture(
                        true
                    ).onSuccess { result ->
                        addImage(imagePath = result.file)
                    }
                }
            }

        gallery
            .ninjaTap(lifecycleScope) {
                hidePickerDialog()
                if (getMultipleImageEnabled()) {
                    lifecycleScope.launchWithTimber {
                        mediaPicker.pickMultipleVisualMedia(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .onSuccess { result ->
                                setListOfImageUrls(mediaPicker.convertUriToFile(result))
                            }
                    }
                } else {
                    lifecycleScope.launchWithTimber {
                        mediaPicker.pickVisualMedia(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .onSuccess { result ->
                                addImage(imagePath = mediaPicker.convertUriToFile(result))
                            }
                    }
                }
            }
    }

    fun hidePickerDialog() {
        imagePickerBottomSheetDialog.dismiss()
    }

    fun showPickerDialog() {
        imagePickerBottomSheetDialog.show()
    }

    abstract fun setImagePathUrl(captureCameraPath: String)
    protected open fun getMultipleImageEnabled(): Boolean {
        return false
    }

    abstract fun setListOfImageUrls(captureCameraPaths: List<File>)

    private fun addImage(imagePath: File) {
        if (captureCameraPaths.isNotEmpty()) {
            captureCameraPaths.clear()
        }
        captureCameraPaths.add(imagePath)
        setListOfImageUrls(captureCameraPaths)
    }
}
