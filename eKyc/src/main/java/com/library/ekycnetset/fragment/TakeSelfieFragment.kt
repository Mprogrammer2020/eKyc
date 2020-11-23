package com.library.ekycnetset.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.BubbleDialog
import com.library.ekycnetset.base.adapter.RecyclerViewGenricAdapter
import com.library.ekycnetset.databinding.DialogInstLayoutBinding
import com.library.ekycnetset.databinding.DialogSuccessLayoutBinding
import com.library.ekycnetset.databinding.FragmentTakeLayoutBinding
import com.library.ekycnetset.databinding.ItemInstLayoutBinding
import com.library.ekycnetset.document.DocumentPresenter
import com.library.ekycnetset.view.RealPath


//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class TakeSelfieFragment : EKycBaseFragment<FragmentTakeLayoutBinding>() {

    private var isSelfieSend = false
    private var isVideoSend = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.takeSelfieClick.uploadTxt.setOnClickListener {

            DocumentPresenter(getContainerActivity(), this, 1000).onScanClick()

        }

//        Log.e("HASH", kycPref.getHash(getContainerActivity())!!)
//        Log.e("USER ID", kycPref.getUserId(getContainerActivity())!!.toString())

        setGlide(R.drawable.ic_take_video, viewDataBinding.takeVideoClick.iconOne)
        viewDataBinding.takeVideoClick.titleTxt.text = getString(R.string.take_a_video)

        viewDataBinding.takeVideoClick.uploadTxt.setOnClickListener {

            DocumentPresenter(getContainerActivity(), this, 1000).onVideoPickerClick()

//            viewDataBinding.takeVideoClick.uploadTxt.text = fromHtml(getString(R.string.upload_again))
//            viewDataBinding.takeVideoClick.uploadedTxt.visibility = View.VISIBLE
//            viewDataBinding.takeVideoClick.bg.background = ContextCompat.getDrawable(
//                context!!,
//                R.drawable.green_stroke_rect
//            )

        }

        viewDataBinding.nextClick.setOnClickListener {
            if (isSelfieSend && isVideoSend)
                success()
        }

        instructions()

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
            1,2 ->{

                if (resultCode == Activity.RESULT_OK) {

                    val uri: Uri = data!!.data!!
                    Log.e("Path", RealPath.getPathFromURI(getContainerActivity(),uri))

                    sendFileApi("send-video", RealPath.getPathFromURI(getContainerActivity(),uri), object : OnSuccess {

                        override fun onRes() {

                            isVideoSend = true

                            viewDataBinding.takeVideoClick.uploadTxt.text = fromHtml(getString(R.string.upload_again))
                            viewDataBinding.takeVideoClick.uploadedTxt.visibility = View.VISIBLE
                            viewDataBinding.takeVideoClick.bg.background = ContextCompat.getDrawable(
                                context!!,
                                R.drawable.green_stroke_rect
                            )

                        }

                    })

                }

            }
//            2 ->{
//
//                if (resultCode == Activity.RESULT_OK) {
//
//                    val uri: Uri = data!!.data!!
//                    Log.e("Path", RealPath.getPathFromURI(getContainerActivity(),uri))
//
//                }
//            }
        }
    }

    private fun success(){

        BubbleDialog(getContainerActivity(), R.layout.dialog_success_layout,
            object : BubbleDialog.LinkodesDialogBinding<DialogSuccessLayoutBinding> {

                override fun onBind(
                    binder: DialogSuccessLayoutBinding,
                    dialog: Dialog
                ) {

                    binder.goToHomeClick.setOnClickListener {
                        dialog.dismiss()
                        getContainerActivity().setResultOk()
                    }

                }

            })

    }

    private fun instructions(){

        BubbleDialog(getContainerActivity(), R.layout.dialog_inst_layout,
            object : BubbleDialog.LinkodesDialogBinding<DialogInstLayoutBinding> {

                override fun onBind(binder: DialogInstLayoutBinding, dialog: Dialog) {

                    val list = ArrayList<String>()
                    list.add("Please ensure that your whole face is in the image.")
                    list.add("Please ensure that the image is taken with proper lighting (e.g. no glare, not too dark).")
                    list.add("Do not wear any glasses or any type d things that will hide your face.")
                    list.add("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever.")

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



                    binder.agreeClick.setOnClickListener {
                        dialog.dismiss()
                    }


                }

            })
    }

}

