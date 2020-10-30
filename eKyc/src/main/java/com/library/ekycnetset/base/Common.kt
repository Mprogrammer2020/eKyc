package com.library.ekycnetset.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import com.library.ekycnetset.R
import java.security.MessageDigest

class Common {

    companion object {

        fun setLoadingDialog(activity: AppCompatActivity): Dialog {

//            val dialog = Dialog(activity,android.R.style.Theme_Light_NoTitleBar)
//            val window = dialog.window
//
//            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            window.setBackgroundDrawableResource(R.color.colorWhiteTransparent)
//            dialog.show()

            val dialog = Dialog(activity)
            dialog.show()
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3ffffff")))
            }
            dialog.setContentView(R.layout.loader)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            return dialog
        }

        fun hashString256(input: String): String {
            return MessageDigest
                .getInstance("SHA-256")
                .digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
        }

    }

}