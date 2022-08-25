package com.library.ekycnetset.fragment

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
import com.library.ekycnetset.databinding.FragmentDocumentSelectionBinding
import com.library.ekycnetset.presenter.DocumentSelectionPresenter
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.FaceCaptureStepBuilder
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder.forDrivingLicence
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder.forNationalIdentity
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder.forPassport
import com.onfido.android.sdk.capture.upload.Captures
import com.onfido.android.sdk.capture.utils.CountryCode
import kotlinx.android.synthetic.main.fragment_document_selection.*


class DocumentTypeSelectionFragment: EKycBaseFragment<FragmentDocumentSelectionBinding>(),
    EKycActivity.NotifyForSuccessDialogAfterKyc {

    var cameFromScreen: String? = ""
    var inputtedData: String? = null
    var countrySelected: String? = null
    private var mPresenter : DocumentSelectionPresenter ?= null
    private var videoIdForOnfido: String? = ""
    private var keysToCreateCheck = ""
    private lateinit var client: Onfido
    private var isFirstBoxChecked = false
    private var isSecondBoxChecked = false

    override fun getCurrentFragment(): Fragment {
        return this
    }

    override fun setTitle(): String {
        return getString(R.string.verify_your_identity)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_document_selection
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        client = OnfidoFactory.create(getContainerActivity()).client
        if (mPresenter == null){
            mPresenter = DocumentSelectionPresenter(getContainerActivity(),this, viewDataBinding)
        }

        val bundle = arguments
        if (bundle != null) {
            cameFromScreen = bundle.getString("from")
            if (cameFromScreen.equals("normalFlow")) {
                inputtedData = bundle.getString("data")
            } else {
                getContainerActivity().notifyForSuccessDialogAfterKyc = this
            }
            countrySelected = bundle.getString("countryCode")
        }
        if (countrySelected != "MYS") {
            nationalIdLay.visibility = View.GONE
            lineThree.visibility = View.GONE
        }

    }

    fun startDocumentVerification(sdkToken: String, documentType: String) {
        lateinit var defaultStepsWithWelcomeScreen: Array<FlowStep>
        lateinit var documentCapture: FlowStep

        if (documentType == "passport") {
            documentCapture = forPassport()
                .build()
        } else {
            documentCapture = forNationalIdentity()
                .withCountry(CountryCode.MY)
                .build()
        }

        val faceCaptureStep = FaceCaptureStepBuilder.forVideo()
            .withIntro(true)
            .withConfirmationVideoPreview(false)
            .build()


        if (getContainerActivity().getRejectedItems().isEmpty()
            || (getContainerActivity().getRejectedItems().contains("facial_similarity_photo") && getContainerActivity().getRejectedItems().contains("document"))) {
            defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                FlowStep.WELCOME,  //Welcome step with a step summary, optional
                documentCapture,  //Document capture step
                faceCaptureStep,
                FlowStep.FINAL //Final screen step, optional
            )
            keysToCreateCheck = "watchlist_standard," + "document" + ",facial_similarity_photo"
        } else {
            if (getContainerActivity().getRejectedItems().contains("document")) {
                defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                    FlowStep.WELCOME,  //Welcome step with a step summary, optional
                    documentCapture,  //Document capture step
                    FlowStep.FINAL //Final screen step, optional
                )
                keysToCreateCheck = "watchlist_standard," + "document" + ",facial_similarity_photo"
            }
            if (getContainerActivity().getRejectedItems().contains("facial_similarity_photo")) {
                defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                    FlowStep.WELCOME,  //Welcome step with a step summary, optional
                    faceCaptureStep,
                    FlowStep.FINAL //Final screen step, optional
                )
                keysToCreateCheck = "facial_similarity_photo"
            }
        }

        val onfidoConfig = OnfidoConfig.builder(getContainerActivity())
            .withCustomFlow(defaultStepsWithWelcomeScreen)
            .withSDKToken(sdkToken)
            .build()



        client.startActivityForResult(this, 1, onfidoConfig)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        client.handleActivityResult(resultCode, data, object : Onfido.OnfidoResultListener {
            override fun onError(exception: OnfidoException) {
                exception.printStackTrace();
                showToast("Unknown error");
            }

            override fun userCompleted(captures: Captures) {
                videoIdForOnfido = captures.face?.id
                if (videoIdForOnfido == null) {
                    videoIdForOnfido = "-1"
                }
                getContainerActivity().notifyForSuccessDialogAfterKyc.notifyForSuccess(videoIdForOnfido.toString(), keysToCreateCheck)
            }

            override fun userExited(exitCode: ExitCode) {
                showToast("User cancelled.");
            }

        })
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