package com.library.ekycnetset.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentStepOneLayoutBinding
//import com.library.ekycnetset.presenter.BaseCheckPresenter
import com.library.ekycnetset.presenter.BaseCheckPresenterUpdated

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class StepOneFragment : EKycBaseFragment<FragmentStepOneLayoutBinding>() {

    private var mPresenter : BaseCheckPresenterUpdated ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mPresenter == null){
            mPresenter = BaseCheckPresenterUpdated(getContainerActivity(),this,viewDataBinding)
        }
    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.tell_us_one)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_step_one_layout
    }

}

