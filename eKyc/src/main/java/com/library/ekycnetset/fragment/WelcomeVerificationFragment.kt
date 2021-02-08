package com.library.ekycnetset.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.databinding.FragmentWelcomeVerificationBinding

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class WelcomeVerificationFragment : EKycBaseFragment<FragmentWelcomeVerificationBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.doItLaterClick.setOnClickListener {
            getContainerActivity().setResultCancelled()
        }

        viewDataBinding.continueClick.setOnClickListener {
            displayIt(StepOneFragment(), StepOneFragment::class.java.canonicalName, true)
        }

    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.ekyc_verification)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_welcome_verification
    }

//    private fun success() {
//
//        BubbleDialog(getContainerActivity(), R.layout.dialog_terms_layout,
//            object : BubbleDialog.LinkodesDialogBinding<DialogTermsLayoutBinding> {
//
//                override fun onBind(
//                    binder: DialogTermsLayoutBinding,
//                    dialog: Dialog
//                ) {
//
//
//                    binder.textOne.setMovementMethod(LinkMovementMethod.getInstance())
//
//                    binder.goToHomeClick.setOnClickListener {
//
//
//                        Log.e("CB 1", binder.cbOne.isChecked.toString())
//                        Log.e("CB 2", binder.cbTwo.isChecked.toString())
//
//                        dialog.dismiss()
//
//                    }
//
//                }
//
//            })
//    }


}

