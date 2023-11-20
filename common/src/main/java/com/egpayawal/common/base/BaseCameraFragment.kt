package com.egpayawal.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.egpayawal.baseplate.common.R
import com.egpayawal.common.extensions.ninjaTap
import com.egpayawal.common.utils.launchWithTimber
import com.egpayawal.common.utils.viewLifecycleScope
import com.egpayawal.mediapicker.mediaPickers
import com.egpayawal.mediapicker.models.MediaPickerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

abstract class BaseCameraFragment<B : ViewDataBinding, VM : BaseViewModel> :
    BaseViewModelFragment<B, VM>() {

    private var captureCameraPaths: MutableList<File> = mutableListOf()

    val cameraPathUrls: List<File> get() = captureCameraPaths

    private lateinit var imagePickerBottomSheetDialog: BottomSheetDialog

    protected val mediaPicker by mediaPickers {
        MediaPickerOptions(
            authority = "${requireContext().packageName}.fileprovider",
            providerFilePath = "."
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMediaPickerDialog()
    }

    @SuppressLint("ResourceType")
    private fun setUpMediaPickerDialog() {
        imagePickerBottomSheetDialog =
            BottomSheetDialog(requireContext())

        val dialogView = this.layoutInflater.inflate(R.layout.bottom_sheet_upload_image, null)

        imagePickerBottomSheetDialog.setContentView(dialogView)

        val takePhoto = dialogView.findViewById<AppCompatButton>(R.id.btnTakePhoto)
        val gallery = dialogView.findViewById<AppCompatButton>(R.id.btnGallery)
        val cancelBtn = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)

        takePhoto
            .ninjaTap(lifecycleScope) {
                hidePickerDialog()
                viewLifecycleScope.launchWithTimber {
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
                    viewLifecycleScope.launchWithTimber {
                        mediaPicker.pickMultipleVisualMedia(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .onSuccess { result ->
                                setListOfImageUrls(mediaPicker.convertUriToFile(result))
                            }
                    }
                } else {
                    viewLifecycleScope.launchWithTimber {
                        mediaPicker.pickVisualMedia(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .onSuccess { result ->
                                addImage(imagePath = mediaPicker.convertUriToFile(result))
                            }
                    }
                }
            }

        cancelBtn
            .ninjaTap(lifecycleScope) {
                hidePickerDialog()
            }
    }


    fun hidePickerDialog() {
        imagePickerBottomSheetDialog.dismiss()
    }

    fun showPickerDialog() {
        imagePickerBottomSheetDialog.show()
    }

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
