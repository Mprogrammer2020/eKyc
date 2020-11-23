package com.library.ekycnetset.document

import android.content.Intent
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class GalleryPicker(val activity: AppCompatActivity, val frag : Fragment){

    private val GALLERY = 1
    private val CAMERA = 2

     fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select video from gallery", "Record video from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> chooseVideoFromGallary()
                1 -> takeVideoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun chooseVideoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        frag.startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takeVideoFromCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        frag.startActivityForResult(intent, CAMERA)
    }


}