package com.library.ekycnetset.fragment

import android.app.Dialog
import android.os.Bundle
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

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class TakeSelfieFragment : EKycBaseFragment<FragmentTakeLayoutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewDataBinding.takeSelfieClick.uploadTxt.setOnClickListener {

            viewDataBinding.takeSelfieClick.uploadTxt.text = fromHtml(getString(R.string.upload_again))
            viewDataBinding.takeSelfieClick.uploadedTxt.visibility = View.VISIBLE
            viewDataBinding.takeSelfieClick.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)

        }


        setGlide(R.drawable.ic_take_video,viewDataBinding.takeVideoClick.iconOne)
        viewDataBinding.takeVideoClick.titleTxt.text = getString(R.string.take_a_video)


        viewDataBinding.takeVideoClick.uploadTxt.setOnClickListener {

            viewDataBinding.takeVideoClick.uploadTxt.text = fromHtml(getString(R.string.upload_again))
            viewDataBinding.takeVideoClick.uploadedTxt.visibility = View.VISIBLE
            viewDataBinding.takeVideoClick.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)

        }

        viewDataBinding.nextClick.setOnClickListener {
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

    private fun success(){

        BubbleDialog(getContainerActivity(), R.layout.dialog_success_layout,
            object : BubbleDialog.LinkodesDialogBinding<DialogSuccessLayoutBinding> {

                override fun onBind(
                    binder: DialogSuccessLayoutBinding,
                    dialog: Dialog) {



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

                override fun onBind(
                    binder: DialogInstLayoutBinding,
                    dialog: Dialog) {

                    val list = ArrayList<String>()
                    list.add("Please ensure that your whole face is in the image.")
                    list.add("Please ensure that the image is taken with proper lighting (e.g. no glare, not too dark).")
                    list.add("Do not wear any glasses or any type d things that will hide your face.")
                    list.add("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever.")

                    val mAdapter = RecyclerViewGenricAdapter<String, ItemInstLayoutBinding>(list,R.layout.item_inst_layout){
                            binderInner, model, position, itemView ->


                        binderInner.srNum.text = (position+1).toString()
                        binderInner.text.text = model

                    }

                    val mLayoutManager = LinearLayoutManager(getContainerActivity(), RecyclerView.VERTICAL, false)
                    binder.instRV.layoutManager = mLayoutManager
                    binder.instRV.itemAnimator = DefaultItemAnimator()
                    binder.instRV.adapter = mAdapter



                    binder.notAgreeClick.setOnClickListener {
                        dialog.dismiss()
                    }


                }

            })

    }


}
