package com.library.ekycnetset.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.application.efx.auth.TermsAndPrivacyWebViewFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.BubbleDialog
import com.library.ekycnetset.databinding.DialogTermsLayoutBinding
import com.library.ekycnetset.databinding.DialogThirdPartyDisclaimerBinding
import com.library.ekycnetset.databinding.FragmentWelcomeVerificationBinding

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

class WelcomeVerificationFragment : EKycBaseFragment<FragmentWelcomeVerificationBinding>() {
    var rootView: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewDataBinding.doItLaterClick.setOnClickListener {
            getContainerActivity().setResultCancelled()
        }

        viewDataBinding.continueClick.setOnClickListener {
            openThirdPartyDisclaimerDialog()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    // perform action when allow permission success
//                    displayIt(StepOneFragment(), StepOneFragment::class.java.canonicalName, true)
//                } else {
//                    getContainerActivity().requestFullStorageAccess(this)
//                }
//            } else {
//                displayIt(StepOneFragment(), StepOneFragment::class.java.canonicalName, true)
//            }
        }

    }

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.ekyc_verification)
    }

    override fun getLayoutId(): Int {
        if (rootView == 0) {
            rootView = R.layout.fragment_welcome_verification
            //  setExternalPermissionDialog(getContainerActivity())
        }
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 5000) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    // perform action when allow permission success
//                    displayIt(StepOneFragment(), StepOneFragment::class.java.canonicalName, true)
//                } else {
//                    Toast.makeText(
//                        getContainerActivity(),
//                        "Allow permission for storage access!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//        }
    }


    private fun openThirdPartyDisclaimerDialog() {
        LocalBroadcastManager.getInstance(getContainerActivity()).unregisterReceiver(broadCastReceiver)
        BubbleDialog(context, getContainerActivity(), R.layout.dialog_third_party_disclaimer,
            object : BubbleDialog.LinkodesDialogBinding<DialogThirdPartyDisclaimerBinding> {
                override fun onBind(binder: DialogThirdPartyDisclaimerBinding, dialog: Dialog) {

                    binder.textOne.setMovementMethod(LinkMovementMethod.getInstance())
                    val wordtoSpan: Spannable = SpannableString(getContainerActivity().getString(R.string.third_party_disclaimer))
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

                    wordtoSpan.setSpan(ForegroundColorSpan(Color.parseColor("#007BB1")), 135, 155, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(UnderlineSpan(), 135, 155, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(spanForTermsAndConditions, 135, 155, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    wordtoSpan.setSpan(ForegroundColorSpan(Color.parseColor("#007BB1")), 160, 175, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(UnderlineSpan(), 160, 175, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    wordtoSpan.setSpan(spanForPrivacyPolicy, 160, 175, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    binder.textOne.text = wordtoSpan

                    binder.proceedToKycBtn.setOnClickListener {
                        dialog.dismiss()
                        displayIt(StepOneFragment(), StepOneFragment::class.java.canonicalName, true)
                    }

                    binder.notNowBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            })
    }


    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                "broadcast" -> {
                    openThirdPartyDisclaimerDialog()
                }
            }
        }
    }


}

