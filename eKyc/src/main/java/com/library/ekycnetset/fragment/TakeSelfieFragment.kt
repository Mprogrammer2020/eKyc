package com.library.ekycnetset.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentTakeLayoutBinding

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class TakeSelfieFragment : EKycBaseFragment<FragmentTakeLayoutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewDataBinding.takeSelfieClick.uploadTxt.setOnClickListener {

            viewDataBinding.takeSelfieClick.uploadTxt.text = getString(R.string.upload_again)
            viewDataBinding.takeSelfieClick.uploadedTxt.visibility = View.VISIBLE
            viewDataBinding.takeSelfieClick.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)

        }


        setGlide(R.drawable.ic_take_video,viewDataBinding.takeVideoClick.iconOne)
        viewDataBinding.takeVideoClick.titleTxt.text = getString(R.string.take_a_video)


        viewDataBinding.takeVideoClick.uploadTxt.setOnClickListener {

            viewDataBinding.takeVideoClick.uploadTxt.text = getString(R.string.upload_again)
            viewDataBinding.takeVideoClick.uploadedTxt.visibility = View.VISIBLE
            viewDataBinding.takeVideoClick.bg.background = ContextCompat.getDrawable(context!!, R.drawable.green_stroke_rect)

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


}

