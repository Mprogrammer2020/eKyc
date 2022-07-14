package com.library.ekycnetset.fragment

//import com.library.ekycnetset.presenter.BaseCheckPresenter

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
import com.library.ekycnetset.base.BubbleDialog
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.DialogTermsLayoutBinding
import com.library.ekycnetset.databinding.FragmentStepOneLayoutBinding
import com.library.ekycnetset.presenter.BaseCheckPresenterUpdated
import com.onfido.android.sdk.capture.*
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.FaceCaptureStepBuilder.forVideo
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder
import com.onfido.android.sdk.capture.upload.Captures
import com.onfido.android.sdk.capture.utils.CountryCode


//by : Deepak Kumar
//at : Netset Software
//in : Kotlin
class StepOneFragment : EKycBaseFragment<FragmentStepOneLayoutBinding>(),
    EKycActivity.NotifyForSuccessDialogAfterKyc {

    private var videoIdForOnfido: String? = ""
    private var mPresenter : BaseCheckPresenterUpdated ?= null
    private var isFirstBoxChecked = false
    private var isSecondBoxChecked = false
    private var keysToCreateCheck = ""

    private lateinit var client: Onfido
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getContainerActivity().notifyForSuccessDialogAfterKyc = this
        if (mPresenter == null){
            mPresenter = BaseCheckPresenterUpdated(getContainerActivity(),this,viewDataBinding)
        }
        client = OnfidoFactory.create(getContainerActivity()).client
    }


    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.tell_us)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_step_one_layout
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
                            mPresenter?.createCheckApi(videoIdForOnfido, keysToCreateCheck)

                        } else {
                            showToast("Please accept our terms & conditions and privacy policy.")
                        }
                    }
                }
            })
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

    override fun notifyForSuccess(videoIdForOnfido: String, keysToCreateCheck: String) {
        this.videoIdForOnfido = videoIdForOnfido
        this.keysToCreateCheck = keysToCreateCheck
        success()
    }


}



