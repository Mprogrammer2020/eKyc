package com.library.ekycnetset.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.library.ekycnetset.R


class BubbleDialog<T : ViewDataBinding>(activity: AppCompatActivity, layoutId : Int, binder : LinkodesDialogBinding<T>){

    init {

        val binding = DataBindingUtil.inflate<T>(
            LayoutInflater.from(activity),
            layoutId,
            null,
            false
        )

        dialog = Dialog(activity, R.style.mytheme)
        dialog!!.setContentView(binding.root)

        val window = dialog!!.window
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        window.setBackgroundDrawableResource(R.color.colorBlackTransparent)

        binder.onBind(binding,dialog!!)

        dialog!!.setOnKeyListener { _ , keyCode, event ->

            keyCode == KeyEvent.KEYCODE_BACK
        }

        dialog!!.show()
    }

    companion object{

        var dialog : Dialog ? = null

        fun dismissDialog(){
            if (dialog != null)
                dialog!!.dismiss()
        }

    }


    interface LinkodesDialogBinding<T : ViewDataBinding>{
        fun onBind(binder: T, dialog: Dialog)
    }
}