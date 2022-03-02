package com.library.ekycnetset.fragment

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.efx.auth.TermsAndPrivacyWebViewFragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.BubbleDialog
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.base.FileUtils
import com.library.ekycnetset.base.adapter.RecyclerViewGenricAdapter
import com.library.ekycnetset.compressor.CompressionListener
import com.library.ekycnetset.compressor.VideoCompressor
import com.library.ekycnetset.compressor.VideoQuality
import com.library.ekycnetset.compressor.getMediaPath
import com.library.ekycnetset.databinding.*
import com.library.ekycnetset.document.DocumentPresenter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class TakeSelfieFragment : EKycBaseFragment<FragmentTakeLayoutBinding>() {

    private var isSelfieSend = false
    private var isVideoSend = false
    private var isFirstBoxChecked = false
    private var isSecondBoxChecked = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.takeSelfieClick.uploadTxt.setOnClickListener {
            val list = ArrayList<String>()
            list.add("Please ensure that your whole face is in the image.")
            list.add("Please ensure that the image is taken with proper lighting. (e.g. no glare, not too dark)")
            list.add("Do not wear any glasses or any type of things that will hide your face.")
            instructions(list, true)
        }

        setGlide(R.drawable.ic_take_video, viewDataBinding.takeVideoClick.iconOne)
        viewDataBinding.takeVideoClick.titleTxt.text = getString(R.string.take_a_video)

        if (getContainerActivity().getAdminSettings()[17].value)
            viewDataBinding.takeSelfieClick.astTxt.visibility = View.VISIBLE
        else
            viewDataBinding.takeSelfieClick.astTxt.visibility = View.GONE


        if (getContainerActivity().getAdminSettings()[18].value)
            viewDataBinding.takeVideoClick.astTxt.visibility = View.VISIBLE
        else
            viewDataBinding.takeVideoClick.astTxt.visibility = View.GONE

        viewDataBinding.takeVideoClick.uploadTxt.setOnClickListener {
            val list = ArrayList<String>()
            list.add("Please ensure that your whole face is in the Video.")
            list.add("Please ensure that the video is taken with proper lighting. (e.g. no glare, not too dark)")
            list.add("Look 5 seconds straight.")
            list.add("Look 5 seconds left.")
            list.add("Look 5 seconds right.")
            list.add("Take Photo ID near your face and look into camera for 5 seconds.")
            instructions(list, false)
        }

        viewDataBinding.nextClick.setOnClickListener {
//            if (isSelfieSend && isVideoSend)
            if (validateSelfies())
                success()
            else
                showToast("Please upload mandatory files.")
        }
    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.take_selfie_or_vid)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_take_layout
    }

    private var isMandatory = true

    private fun validateSelfies(): Boolean {
        if (getContainerActivity().getAdminSettings()[17].value){
            isMandatory = isSelfieSend
            if (!isMandatory) {
                return false
            }
        }

        if (getContainerActivity().getAdminSettings()[18].value){
            isMandatory = isVideoSend
            if (!isMandatory) {
                return false
            }
        }

        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1000 -> {
                if (resultCode == Activity.RESULT_OK) {

                    val uri: Uri = data!!.getParcelableExtra("path")!!
                    Log.e("Path", uri.path!!)

                    sendFileApi("send-image", uri.path!!, object : OnSuccess {

                        override fun onRes() {
                            isSelfieSend = true
                            viewDataBinding.takeSelfieClick.uploadTxt.text =
                                fromHtml(getString(R.string.upload_again))
                            viewDataBinding.takeSelfieClick.uploadedTxt.visibility = View.VISIBLE
                            viewDataBinding.takeSelfieClick.bg.background =
                                ContextCompat.getDrawable(
                                    context!!,
                                    R.drawable.green_stroke_rect
                                )
                        }
                    })
                }
            }
            1, 2 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri: Uri = data!!.data!!
                    Log.e("Path", FileUtils.getRealPath(getContainerActivity(), uri))
                    val desFile = saveVideoFile(FileUtils.getRealPath(getContainerActivity(), uri))
                    VideoCompressor.start(
                        FileUtils.getRealPath(getContainerActivity(), uri),
                        desFile!!.path,
                        object : CompressionListener {
                            override fun onProgress(percent: Float) {
                                // Update UI with progress value

                                //Update UI
                                if (percent <= 100 && percent.toInt() % 5 == 0)
                                    getContainerActivity().runOnUiThread {
                                        viewDataBinding.precentProgressTxt.text =
                                            "${percent.toLong()}%"
                                    }
                            }

                            override fun onStart() {
                                // Compression start

                                viewDataBinding.percentView.visibility = View.VISIBLE
                            }

                            override fun onSuccess() {

                                viewDataBinding.percentView.visibility = View.GONE

                                sendFileApi(
                                    "send-video",
                                    desFile.path,
                                    object : OnSuccess {

                                        override fun onRes() {

                                            isVideoSend = true

                                            viewDataBinding.takeVideoClick.uploadTxt.text =
                                                fromHtml(
                                                    getString(
                                                        R.string.upload_again
                                                    )
                                                )
                                            viewDataBinding.takeVideoClick.uploadedTxt.visibility =
                                                View.VISIBLE
                                            viewDataBinding.takeVideoClick.bg.background =
                                                ContextCompat.getDrawable(
                                                    context!!,
                                                    R.drawable.green_stroke_rect
                                                )

                                        }

                                    })

                            }

                            override fun onFailure(failureMessage: String) {
                                // On Failure
                            }

                            override fun onCancelled() {
                                // On Cancelled
                            }

                        },
                        VideoQuality.MEDIUM,
                        isMinBitRateEnabled = false,
                        keepOriginalResolution = false
                    )
                }
            }
        }
    }

    private fun success() {
        LocalBroadcastManager.getInstance(getContainerActivity()).unregisterReceiver(broadCastReceiver)

        BubbleDialog(context, getContainerActivity(), R.layout.dialog_terms_layout,
            object : BubbleDialog.LinkodesDialogBinding<DialogTermsLayoutBinding> {
                override fun onBind(
                    binder: DialogTermsLayoutBinding,
                    dialog: Dialog
                ) {
                    binder.textOne.setMovementMethod(LinkMovementMethod.getInstance())
                    val wordtoSpan: Spannable = SpannableString(getContainerActivity().getString(R.string.check_box_line_one_))
                    val spanForTermsAndConditions: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            dialog.dismiss()
                            LocalBroadcastManager.getInstance(getContainerActivity()).registerReceiver(broadCastReceiver, IntentFilter("broadcast"))
                           displayIt(TermsAndPrivacyWebViewFragment(getContainerActivity().getString(R.string.terms_conditions)), TermsAndPrivacyWebViewFragment::class.java.canonicalName, true)
                        }
                    }
                    val spanForPrivacyPolicy: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            dialog.dismiss()
                            LocalBroadcastManager.getInstance(getContainerActivity()).registerReceiver(broadCastReceiver, IntentFilter("broadcast"))
                            displayIt(TermsAndPrivacyWebViewFragment(getContainerActivity().getString(R.string.privacy_policy)), TermsAndPrivacyWebViewFragment::class.java.canonicalName, true)
                        }
                    }
                    wordtoSpan.setSpan(ForegroundColorSpan(Color.parseColor("#007BB1")), 71, 91, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(UnderlineSpan(), 71, 91, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(spanForTermsAndConditions, 71, 91, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    wordtoSpan.setSpan(ForegroundColorSpan(Color.parseColor("#007BB1")), 96, 111, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(UnderlineSpan(), 96, 111, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(spanForPrivacyPolicy, 96, 111, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    binder.textOne.text = wordtoSpan

                    binder.cbOne.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                        override fun onCheckedChanged(
                            buttonView: CompoundButton?,
                            isChecked: Boolean
                        ) {
                            isFirstBoxChecked = isChecked

                        }
                    })

                    binder.cbTwo.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                        override fun onCheckedChanged(
                            buttonView: CompoundButton?,
                            isChecked: Boolean
                        ) {
                            isSecondBoxChecked = isChecked
                        }
                    })

                    if (isFirstBoxChecked) {
                        binder.cbOne.isChecked = true
                    }
                    if (isSecondBoxChecked) {
                        binder.cbTwo.isChecked = true
                    }
                    binder.goToHomeClick.setOnClickListener {
                        Log.e("CB 1", binder.cbOne.isChecked.toString())
                        Log.e("CB 2", binder.cbTwo.isChecked.toString())

                        if (binder.cbOne.isChecked) {
                            kycPref.storeUserAppInfo(
                                getContainerActivity(),
                                Constants.CHECK_ONE,
                                binder.cbOne.isChecked.toString()
                            )
                            kycPref.storeUserAppInfo(
                                getContainerActivity(),
                                Constants.CHECK_TWO,
                                binder.cbTwo.isChecked.toString()
                            )

                            dialog.dismiss()
                            getContainerActivity().setResultOk()

                        } else {
                            showToast("Please accept our terms & conditions and privacy policy.")
                        }
                    }
                }
            })
    }

//    private fun success() {
//
//        BubbleDialog(getContainerActivity(), R.layout.dialog_success_layout,
//            object : BubbleDialog.LinkodesDialogBinding<DialogSuccessLayoutBinding> {
//
//                override fun onBind(
//                    binder: DialogSuccessLayoutBinding,
//                    dialog: Dialog
//                ) {
//
//                    binder.goToHomeClick.setOnClickListener {
//                        dialog.dismiss()
//                        getContainerActivity().setResultOk()
//                    }
//
//                }
//
//            })
//    }

    private fun instructions(list: ArrayList<String>, isForCamera: Boolean) {

        BubbleDialog(context, getContainerActivity(), R.layout.dialog_inst_layout,
            object : BubbleDialog.LinkodesDialogBinding<DialogInstLayoutBinding> {

                override fun onBind(binder: DialogInstLayoutBinding, dialog: Dialog) {

                    val mAdapter = RecyclerViewGenricAdapter<String, ItemInstLayoutBinding>(
                        list,
                        R.layout.item_inst_layout
                    ) { binderInner, model, position, itemView ->


                        binderInner.srNum.text = (position + 1).toString()
                        binderInner.text.text = model

                    }

                    val mLayoutManager = LinearLayoutManager(
                        getContainerActivity(),
                        RecyclerView.VERTICAL,
                        false
                    )
                    binder.instRV.layoutManager = mLayoutManager
                    binder.instRV.itemAnimator = DefaultItemAnimator()
                    binder.instRV.adapter = mAdapter

                    if (isForCamera) binder.title.text =
                        getString(R.string.selfie_instructions) else binder.title.text = getString(
                        R.string.video_instructions
                    )
                    binder.agreeClick.setOnClickListener {
                        dialog.dismiss()
                        if (isForCamera) {
                            DocumentPresenter(
                                getContainerActivity(),
                                this@TakeSelfieFragment,
                                1000
                            ).onSelfieClick()
                        } else {
                            DocumentPresenter(
                                getContainerActivity(),
                                this@TakeSelfieFragment,
                                1000
                            ).onVideoPickerClick()
                        }
                    }

                    binder.notAgreeClick.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            })
    }

    @Suppress("DEPRECATION")
    private fun saveVideoFile(filePath: String?): File? {
        filePath?.let {
            val videoFile = File(filePath)
            val videoFileName = "${System.currentTimeMillis()}_${videoFile.name}"
            val folderName = Environment.DIRECTORY_MOVIES
            if (Build.VERSION.SDK_INT >= 30) {

                val values = ContentValues().apply {

                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        videoFileName
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileUri = getContainerActivity().contentResolver.insert(collection, values)

                fileUri?.let {
                    getContainerActivity().contentResolver.openFileDescriptor(fileUri, "rw")
                        .use { descriptor ->
                            descriptor?.let {
                                FileOutputStream(descriptor.fileDescriptor).use { out ->
                                    FileInputStream(videoFile).use { inputStream ->
                                        val buf = ByteArray(4096)
                                        while (true) {
                                            val sz = inputStream.read(buf)
                                            if (sz <= 0) break
                                            out.write(buf, 0, sz)
                                        }
                                    }
                                }
                            }
                        }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    getContainerActivity().contentResolver.update(fileUri, values, null, null)

                    return File(getMediaPath(getContainerActivity(), fileUri))
                }
            } else {
                val downloadsPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, videoFileName)

                if (desFile.exists())
                    desFile.delete()

                try {
                    desFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return desFile
            }
        }
        return null
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
      //  LocalBroadcastManager.getInstance(getContainerActivity()).unregisterReceiver(broadCastReceiver)
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                "broadcast" -> {
                    success()
                }
            }
        }
    }
}