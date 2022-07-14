package com.library.ekycnetset.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.library.ekycnetset.EKycBaseFragment
import com.library.ekycnetset.R
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


class DocumentTypeSelectionFragment: EKycBaseFragment<FragmentDocumentSelectionBinding>() {

    public var inputtedData: String? = null
    public var countrySelected: String? = null
    private var mPresenter : DocumentSelectionPresenter ?= null
    private var videoIdForOnfido: String? = ""
    private var keysToCreateCheck = ""
    private lateinit var client: Onfido

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
            inputtedData = bundle.getString("data")
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
//            defaultStepsWithWelcomeScreen.set(2, faceCaptureStep)
            keysToCreateCheck = "watchlist_standard," + "document" + ",facial_similarity_photo"
        } else {
            if (getContainerActivity().getRejectedItems().contains("document")) {
                defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                    FlowStep.WELCOME,  //Welcome step with a step summary, optional
                    documentCapture,  //Document capture step
                    FlowStep.FINAL //Final screen step, optional
                )
                keysToCreateCheck = "watchlist_standard," + "document"
            }
            if (getContainerActivity().getRejectedItems().contains("facial_similarity_photo")) {
                defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                    FlowStep.WELCOME,  //Welcome step with a step summary, optional
                    faceCaptureStep,
                    FlowStep.FINAL //Final screen step, optional
                )
//                defaultStepsWithWelcomeScreen.set(1, faceCaptureStep)
                keysToCreateCheck = "watchlist_standard," + "facial_similarity_photo"
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
}