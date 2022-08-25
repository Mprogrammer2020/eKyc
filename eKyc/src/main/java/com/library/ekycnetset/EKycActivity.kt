package com.library.ekycnetset

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.application.linkodes.network.ApiService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.library.ekycnetset.base.BaseActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.databinding.ActivityEKycBinding
import com.library.ekycnetset.fragment.TermsAndPrivacyWebViewFragment
import com.library.ekycnetset.fragment.WelcomeVerificationFragment
import com.library.ekycnetset.model.Data
import com.library.ekycnetset.model.EKycModel
import com.library.ekycnetset.network.ApiClient
import com.onfido.android.sdk.capture.ExitCode
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoConfig
import com.onfido.android.sdk.capture.OnfidoFactory
import com.onfido.android.sdk.capture.errors.OnfidoException
import com.onfido.android.sdk.capture.ui.camera.face.stepbuilder.FaceCaptureStepBuilder
import com.onfido.android.sdk.capture.ui.options.FlowStep
import com.onfido.android.sdk.capture.ui.options.stepbuilder.DocumentCaptureStepBuilder
import com.onfido.android.sdk.capture.upload.Captures
import com.onfido.android.sdk.capture.utils.CountryCode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

// Dependency e-KYC
// Developed by : Deepak Kumar

class EKycActivity : BaseActivity<ActivityEKycBinding>() {

    private var keysToCreateCheck: String = ""
    private var adminSettingsList = ArrayList<Data>()
    private var rejectedItemsList = ArrayList<String>()
    public lateinit var notifyForSuccessDialogAfterKyc: NotifyForSuccessDialogAfterKyc
    private lateinit var client: Onfido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        kycPref.clearPrefs(this)


        val bundle = intent.extras

        if (bundle != null){
            if (!bundle.getString(Constants.API_BASE_URL).isNullOrEmpty()){
                retrofitClient = ApiClient().getClient(this, bundle.getString(Constants.API_BASE_URL)?: "")
                service = ApiClient().getClient(this, bundle.getString(Constants.API_BASE_URL)?: "").create(ApiService::class.java)
            } else {
                showToast("Api Base Url key is missing.")
                setResultCancelled()
            }

            if (bundle.containsKey(Constants.USER_ID)){
                if (bundle.getString(Constants.USER_ID).isNullOrEmpty()){
                    showToast("User id key is missing.")
                    setResultCancelled()
                } else {
                    kycPref.storeAuthKey(this,bundle.getString(Constants.USER_AUTH_TOKEN)!!)
                    kycPref.storeUserAppInfo(this,Constants.USER_ID,bundle.getString(Constants.USER_ID)?: "")
                    kycPref.storeUserAppInfo(this,Constants.F_NAME,bundle.getString(Constants.F_NAME)?: "")
                    kycPref.storeUserAppInfo(this,Constants.L_NAME,bundle.getString(Constants.L_NAME)?: "")
                    kycPref.storeUserAppInfo(this,Constants.EMAIL,bundle.getString(Constants.EMAIL)?: "")
                    kycPref.storeUserAppInfo(this,Constants.PHONE_NUMBER,bundle.getString(Constants.PHONE_NUMBER)?: "")
                    kycPref.storeUserAppInfo(this,Constants.PHONE_CODE,bundle.getString(Constants.PHONE_CODE)?: "")
                    kycPref.storeUserAppInfo(this,Constants.ADDRESS,bundle.getString(Constants.ADDRESS)?: "")
                    kycPref.storeUserAppInfo(this,Constants.DOB,bundle.getString(Constants.DOB)?: "")
                    kycPref.storeUserAppInfo(this,Constants.NATIONALITY,bundle.getString(Constants.NATIONALITY)?: "")
                    kycPref.storeUserAppInfo(this,Constants.STREET,bundle.getString(Constants.STREET)?: "")
                    kycPref.storeUserAppInfo(this,Constants.CITY,bundle.getString(Constants.CITY)?: "")
                    kycPref.storeUserAppInfo(this,Constants.ZIP_CODE,bundle.getString(Constants.ZIP_CODE)?: "")


                    adminSettingsList = bundle.getSerializable(Constants.ADMIN_SETTINGS_LIST) as ArrayList<Data>

                    if (bundle.containsKey(Constants.REJECTED_ITEM_LIST)){
                        kycPref.storeUserAppInfo(this,Constants.REJECTED_DOCUMENT_TYPE,bundle.getString(Constants.REJECTED_DOCUMENT_TYPE)?: "")
                        rejectedItemsList = bundle.getStringArrayList(Constants.REJECTED_ITEM_LIST) as ArrayList<String>
                        Log.e("rejectedItemsList", rejectedItemsList.toString())
                    }
                }
            } else{
                showToast("User id key is missing.")
                setResultCancelled()
            }
        } else{
            showToast("User id key is missing.")
            setResultCancelled()
        }

        client = OnfidoFactory.create(this).client
        initialiseOnfidoOrMoveToWelcomeScreenForKyc()

        viewDataBinding.toolbarLeftMain.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initialiseOnfidoOrMoveToWelcomeScreenForKyc() {
        if (getRejectedItems().isEmpty()
            || (getRejectedItems().contains("facial_similarity_photo") && getRejectedItems().contains("document"))) {
            displayIt(WelcomeVerificationFragment(), WelcomeVerificationFragment::class.java.canonicalName, true)
            keysToCreateCheck = "watchlist_standard," + "document" + ",facial_similarity_photo"
        } else {
            getOnfidoSdkToken()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        client.handleActivityResult(resultCode, data, object : Onfido.OnfidoResultListener {
            override fun onError(exception: OnfidoException) {
                exception.printStackTrace();
                showToast("Unknown error");
            }

            override fun userCompleted(captures: Captures) {
                var videoIdForOnfido = captures.face?.id
                if (videoIdForOnfido == null) {
                    videoIdForOnfido = "-1"
                }
                createCheckApi(videoIdForOnfido.toString(), keysToCreateCheck)
            }

            override fun userExited(exitCode: ExitCode) {
                showToast("User cancelled.");
                finish()
            }

        })
    }

    private fun getOnfidoSdkToken() {
        showLoading()
        disposable.add(
            apiService.getOnfidoSdkToken(kycPref.getUserAppInfo(this, Constants.USER_ID).toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel>() {

                    override fun onSuccess(efx: EKycModel) {
                        hideLoading()
                        openOnfidoInterface(efx.data?.token)
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        showError(e, this@EKycActivity)
                    }
                })
        )
    }

    private fun openOnfidoInterface(token: String?) {
        lateinit var defaultStepsWithWelcomeScreen: Array<FlowStep>
        lateinit var documentCapture: FlowStep


        if (kycPref.getUserAppInfo(this, Constants.REJECTED_DOCUMENT_TYPE) == "passport") {
            documentCapture = DocumentCaptureStepBuilder.forPassport()
                .build()
        } else {
            documentCapture = DocumentCaptureStepBuilder.forNationalIdentity()
                .withCountry(CountryCode.MY)
                .build()
        }

        val faceCaptureStep = FaceCaptureStepBuilder.forVideo()
            .withIntro(true)
            .withConfirmationVideoPreview(false)
            .build()


        if (getRejectedItems().contains("document")) {
            defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                FlowStep.WELCOME,  //Welcome step with a step summary, optional
                documentCapture,  //Document capture step
                FlowStep.FINAL //Final screen step, optional
            )
            keysToCreateCheck = "watchlist_standard," + "document" + ",facial_similarity_photo"
        }
        if (getRejectedItems().contains("facial_similarity_photo")) {
            defaultStepsWithWelcomeScreen = arrayOf<FlowStep>(
                FlowStep.WELCOME,  //Welcome step with a step summary, optional
                faceCaptureStep,
                FlowStep.FINAL //Final screen step, optional
            )
            keysToCreateCheck = "facial_similarity_photo"
        }

        val onfidoConfig = OnfidoConfig.builder(this)
            .withCustomFlow(defaultStepsWithWelcomeScreen)
            .withSDKToken(token.toString())
            .build()

        client.startActivityForResult(this, 1, onfidoConfig)
    }

    fun showError(e: Throwable, activity: AppCompatActivity) {
        setResponseDialog(activity, e.message!!)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_e_kyc
    }

    override fun setContainerLayout(): Int {
        return viewDataBinding.frameContainer.id
    }

    fun getCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
    }

    fun setTitle(title: String){
        viewDataBinding.toolbarTitle.text = title
    }

    fun setResultCancelled() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    fun setResultOk() {
        val intent = Intent()
        if (kycPref.getUserAppInfo(this,Constants.CHECK_ONE) !=null ){
            intent.putExtra(Constants.CHECK_ONE,kycPref.getUserAppInfo(this,Constants.CHECK_ONE)!!)
            intent.putExtra(Constants.CHECK_TWO,kycPref.getUserAppInfo(this,Constants.CHECK_TWO)!!)
        }

        setResult(RESULT_OK, intent)
        this.finish()
    }

    fun getAdminSettings() : ArrayList<Data>{
        return adminSettingsList
    }

    fun getRejectedItems() : ArrayList<String>{
        return rejectedItemsList
    }


    override fun onBackPressed() {
        if (currentFragment is WelcomeVerificationFragment) {
            setResultCancelled()
        } else if (currentFragment is TermsAndPrivacyWebViewFragment) {
            (currentFragment as TermsAndPrivacyWebViewFragment).onBackPress()
        } else {
            super.onBackPressed()
        }

    }

    interface NotifyForSuccessDialogAfterKyc {
        fun notifyForSuccess(videoIdForOnfido: String, keysToCreateCheck: String)
    }

    private fun setResponseDialog(activity: AppCompatActivity, message: String) {
        val dialog = BottomSheetDialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_bottom_response_layout)

        val ok = dialog.findViewById<Button>(R.id.okClick)
        val msg = dialog.findViewById<TextView>(R.id.message)

        msg!!.text = message

        ok!!.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun createCheckApi(videoIdForOnfido: String?, keysToCreateCheck: String) {
        val jsonObject = JSONObject()
        jsonObject.put("reports", keysToCreateCheck)

        Log.e("Check Base", jsonObject.toString())

        val requestBody =
            jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())


        showLoading()
        disposable.add(
            apiService.createCheckApi(kycPref.getUserAppInfo(this, Constants.USER_ID).toString(),
                videoIdForOnfido.toString(), requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<EKycModel>() {
                    override fun onSuccess(model: EKycModel) {
                        hideLoading()
                        setResultOk()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        showError(e, this@EKycActivity)
                    }
                })
        )

    }


}