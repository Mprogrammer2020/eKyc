package com.library.ekycnetset.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentStepOneLayoutBinding
import java.util.*

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class StepOneFragment : EKycBaseFragment<FragmentStepOneLayoutBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewDataBinding.prevClick.setOnClickListener {

            when (viewDataBinding.signUpViewFlipper.displayedChild) {

                1 -> {

                    viewDataBinding.prevClick.visibility = View.GONE
                    viewDataBinding.signUpViewFlipper.showPrevious()
                    viewDataBinding.lineOne.background = ContextCompat.getDrawable(context!!, R.color.grey)
                    viewDataBinding.textTwo.setTextColor(ContextCompat.getColor(context!!, R.color.colorBlack))
                    viewDataBinding.stepTwoView.background = ContextCompat.getDrawable(context!!, R.drawable.circle_unselected)

                }
                2 -> {
                    viewDataBinding.signUpViewFlipper.showPrevious()
                    viewDataBinding.lineTwo.background = ContextCompat.getDrawable(context!!, R.color.grey)
                    viewDataBinding.textThree.setTextColor(ContextCompat.getColor(context!!, R.color.colorBlack))
                    viewDataBinding.stepThreeView.background = ContextCompat.getDrawable(context!!, R.drawable.circle_unselected)
                }

            }

        }

        viewDataBinding.nextClick.setOnClickListener {

            when (viewDataBinding.signUpViewFlipper.displayedChild) {

                0 -> {

//                    if (validateEditText(viewDataBinding.firstnameView.nameET)) {

                        viewDataBinding.prevClick.visibility = View.VISIBLE
                        viewDataBinding.signUpViewFlipper.showNext()
                    viewDataBinding.lineOne.background = ContextCompat.getDrawable(context!!, R.color.colorEfxBlue)
                    viewDataBinding.textTwo.setTextColor(ContextCompat.getColor(context!!, R.color.colorEfxBlue))
                    viewDataBinding.stepTwoView.background = ContextCompat.getDrawable(context!!, R.drawable.circle_selected)
//                        viewDataBinding.lastnameView.tvTwo.text = getString(R.string.fname_view, viewDataBinding.firstnameView.nameET.text)

//                    }
                }

                1 -> {

//                    if (validateEditText(viewDataBinding.lastnameView.nameET)) {

                        viewDataBinding.signUpViewFlipper.showNext()
                    viewDataBinding.lineTwo.background = ContextCompat.getDrawable(context!!, R.color.colorEfxBlue)
                    viewDataBinding.textThree.setTextColor(ContextCompat.getColor(context!!, R.color.colorEfxBlue))
                    viewDataBinding.stepThreeView.background = ContextCompat.getDrawable(context!!, R.drawable.circle_selected)

//                        viewDataBinding.emailView.tvOne.text = getString(R.string.hi)
//                        viewDataBinding.emailView.tvTwo.text = getString(R.string.name_view, viewDataBinding.firstnameView.nameET.text, viewDataBinding.lastnameView.nameET.text)

//                    }
                }
                2 ->{

                    displayIt(MobileVerificationFragment(), MobileVerificationFragment::class.java.canonicalName, true)

                }

            }
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

