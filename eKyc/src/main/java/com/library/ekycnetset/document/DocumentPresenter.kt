package com.library.ekycnetset.document

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.R
import com.library.ekycnetset.fragment.UploadDocumentFragment
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.obsez.android.lib.filechooser.ChooserDialog
import java.util.regex.Pattern


class DocumentPresenter(
    private var mActivity: EKycActivity,
    private var frag: Fragment,
    private var req: Int
) {

    private fun showImagePickerOptions() {
        SimpleImagePickerActivity.showImagePickerOptions(
            mActivity,
            object : SimpleImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            },
            mActivity.getString(R.string.lbl_set_choose_option)
        )
    }

    fun onVideoPickerClick() {
        Dexter.withActivity(mActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val galleryPicker = GalleryPicker(mActivity, frag)
                        galleryPicker.showPictureDialog()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun onFilePickerClick() {
        Dexter.withActivity(mActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
//                        openFilePicker()
//                        filePickerV2()
                        filePickerNewV2()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun filePickerNewV2() {
        // getDocument();
        ChooserDialog().with(mActivity)
            .withStartFile(Environment.getExternalStorageDirectory().absolutePath)
            .withFilter(false, false, "pdf", "docx", "doc", "xlsx", "xls", "ppt", "pptx", "png", "jpg", "jpeg")
            .withResources(R.string.title_choose, R.string.title_choose, R.string.dialog_cancel)
            .withChosenListener(ChooserDialog.Result { s, file ->
                (frag as UploadDocumentFragment).fileWithRequestCode(req, s)
            })
            .build().show()
    }

    private fun filePickerV2(){

        MaterialFilePicker()
            // Pass a source of context. Can be:
            //    .withActivity(Activity activity)
            //    .withFragment(Fragment fragment)
            //    .withSupportFragment(androidx.fragment.app.Fragment fragment)
            .withSupportFragment(frag)
            // With cross icon on the right side of toolbar for closing picker straight away
            .withCloseMenu(true)
            // Entry point path (user will start from it)
            // Root path (user won't be able to come higher than it)
            // Showing hidden files
            .withHiddenFiles(true)
            // Want to choose only jpg images
            .withFilter(Pattern.compile(".*\\.(jpg|jpeg|pdf|png|gif|tiff)$"))
            // Don't apply filter to directories names
            .withFilterDirectories(false)
            .withTitle("Pick a file")
            .withRequestCode(req)
            .start()

    }

//    private fun openFilePicker(){
//
////        val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
////        chooseFile.setType("*/*")
////        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
////        frag.startActivityForResult(chooseFile, req)
//
////        rPath.contains("jpg") || rPath.contains("jpeg") || rPath.contains("png") ||
////                rPath.contains("gif") || rPath.contains("tiff") || rPath.contains("pdf")){
//
//        val intent = Intent(mActivity, FilePickerActivity::class.java)
//        intent.putExtra(
//            FilePickerActivity.CONFIGS, Configurations.Builder()
//                .setCheckPermission(true)
//                .setShowImages(false)
//                .setShowVideos(false).setShowFiles(true).setSuffixes("jpg","jpeg","pdf","png","gif","tiff","pdf")
//                .enableImageCapture(false)
//                .setMaxSelection(1)
//                .setSkipZeroSizeFiles(true)
//                .build()
//        )
//        frag.startActivityForResult(intent, req)
//    }

    private fun launchCameraIntent() {
        val intent = Intent(mActivity, SimpleImagePickerActivity::class.java)
        intent.putExtra(
            SimpleImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            SimpleImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(SimpleImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(SimpleImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(SimpleImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(SimpleImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(SimpleImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(SimpleImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        frag.startActivityForResult(intent, req)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(mActivity, SimpleImagePickerActivity::class.java)
        intent.putExtra(
            SimpleImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            SimpleImagePickerActivity.REQUEST_GALLERY_IMAGE
        )

        // setting aspect ratio
        intent.putExtra(SimpleImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(SimpleImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(SimpleImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        frag.startActivityForResult(intent, req)
    }

    fun onSelfieClick() {
        Dexter.withActivity(mActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        launchCameraIntent()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    fun onScanClick() {
        Dexter.withActivity(mActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(mActivity)
        builder.setTitle(mActivity.getString(R.string.dialog_permission_title))
        builder.setMessage(mActivity.getString(R.string.dialog_permission_message))
        builder.setPositiveButton(mActivity.getString(R.string.go_to_settings)) { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(mActivity.getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", mActivity.packageName, null)
        intent.data = uri
        mActivity.startActivityForResult(intent, 101)
    }

}