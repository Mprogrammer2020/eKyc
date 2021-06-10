package com.library.ekycnetset

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.library.ekycnetset.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws


// by :- Deepak Kumar
// at :- Netset Software
// in :- kotlin

abstract class EKycBaseFragment<T : ViewDataBinding?> : BaseFragment<T>(), FragmentView {

    var isExternalPermissionGranted: Boolean = false
    private lateinit var mContainerActivity: EKycActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContainerActivity = context as EKycActivity
    }

    fun getContainerActivity(): EKycActivity {
        return mContainerActivity
    }

    override fun onStart() {
        super.onStart()
        mContainerActivity.getCurrentFragment(getCurrentFragment())
        mContainerActivity.setTitle(setTitle())
    }

    /* Validate EditText that checks empty. */
    fun validateEditText(et: EditText): Boolean {

        return if (et.text.toString() == "") {

            et.requestFocus()
            et.error = "This field can't be empty"

            false
        } else
            true
    }


    fun validateEditText(et: EditText, msg: String): Boolean {

        return if (et.text.toString() == "") {

            alertBox(msg)

            false
        } else
            true
    }

    fun alertBox(msg: String){
        showToast(msg)
    }

    /* Checks entered email is valid or not. */
    fun isEmailValid(et: EditText): Boolean {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(et.text.toString()).matches()

        return if (isValid) {
            true
        } else {
            et.requestFocus()
            et.error = "Entered email address is not valid"
            false
        }
    }

    fun isEmailValid(et: EditText, msg: String): Boolean {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(et.text.toString()).matches()

        return if (isValid) {
            true
        } else {
            alertBox(msg)
            false
        }
    }


    /* Checks both passwords are same or not. */
    fun comparePassword(etOne: EditText, etTwo: EditText): Boolean {

        if (etOne.text.toString() == etTwo.text.toString()) {
            return true
        } else {
//            etTwo.requestFocus()
//            etTwo.error = "Password and Confirm password doesn't match"
            alertBox("Password and Confirm password doesn't match")
            return false
        }
    }

    fun isPasswordValid(et: EditText, msg: String): Boolean {

        return if (et.text.toString().length >= 6) {
            true
        } else {
            alertBox(msg)
            false
        }
    }

    fun notWithSpace(et: EditText) {
        val myWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (et.text.toString() == " ")
                    et.setText("")
            }
        }
        et.addTextChangedListener(myWatcher)
    }

    fun validatePhoneNumber(et: EditText): Boolean {

        return if (et.text.toString().length < 8) {
            et.requestFocus()
            et.error = "Entered phone number is not valid"
            false
        } else
            true
    }

    fun showError(e: Throwable, activity: AppCompatActivity) {

//        var isNetworkException: Boolean

//        try {
//            val obj = JSONObject((e as HttpException).response().errorBody()!!.string())
//            val error = obj.optString("message")
//            val code = e.code()
//
//            isNetworkException = false
//
//            Log.e("Error Message", "$error $code")
//
//            setResponseDialog(activity, error)
//
//
//        } catch (e: Exception) {
//            isNetworkException = true
//            e.printStackTrace()
//        }
//
//        Log.e("TAG", "onError: " + e.message)
//        if (isNetworkException) {
//
//            if (e.message!!.contains("Failed to connect") || e.message!!.contains("Unable to resolve"))
//                setResponseDialog(
//                    activity,
//                    activity.getString(R.string.internet_error)
//                )
//            else
                setResponseDialog(activity, e.message!!)
//
//        }
    }

    fun setResponseDialog(activity: AppCompatActivity, message: String) {
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

    fun getPhoneNumber(number: String): String {
        val input = number
        return input.replace(" ", "")
    }

    fun commonDOBSelection(tv: EditText, userDOB: OnSelectedDOB, isMaxReq: Boolean) {

        val c = Calendar.getInstance()

        val mYear: Int
        val mMonth: Int
        val mDay: Int

        if (tv.text.toString().isNotEmpty()) {
            Log.e("Filled DOB", tv.text.toString())

            c.timeInMillis = getDateInMillies(tv.text.toString())

            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

        } else {

            if (isMaxReq)
                mYear = c.get(Calendar.YEAR) - 16
            else
                mYear = c.get(Calendar.YEAR)

            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

        }

//        val c = Calendar.getInstance()
//
//        val mYear = c.get(Calendar.YEAR) - 16
//        val mMonth = c.get(Calendar.MONTH)
//        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            getContainerActivity(),
            R.style.DatePickerDialogTheme,
            { _, year, monthOfYear, dayOfMonth ->

                val calendar = Calendar.getInstance()
                calendar.set(year, monthOfYear, dayOfMonth)

                val formatMain = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
                tv.setText(formatMain.format(calendar.time))

                val apiFormatDate = SimpleDateFormat("dd", Locale.ENGLISH)
                val apiFormatMonth = SimpleDateFormat("MM", Locale.ENGLISH)
                userDOB.selectedDate(
                    apiFormatDate.format(calendar.time), apiFormatMonth.format(
                        calendar.time
                    ), year
                )

            },
            mYear,
            mMonth,
            mDay
        )

        if (isMaxReq)
            datePickerDialog.datePicker.maxDate =
                (System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 365.25 * 16)).toLong()
        else
            datePickerDialog.datePicker.maxDate = (System.currentTimeMillis() - 1000)

        datePickerDialog.show()
    }

    interface OnSelectedDOB {
        fun selectedDate(date: String, month: String, year: Int)
    }

    @Throws(ParseException::class)
    private fun getDateInMillies(dateInString: String): Long {
        val format = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
//            format.timeZone = TimeZone.getTimeZone("UTC")
        val result = format.parse(dateInString)
        return result!!.time
    }

    fun setEditableET(editText: EditText, isEditable: Boolean) {
        editText.isFocusable = isEditable
        editText.isFocusableInTouchMode = isEditable
        editText.isClickable = isEditable
        editText.isCursorVisible = isEditable
        editText.requestFocus()
        editText.setSelection(editText.text.length)

        if (isEditable) {
            val inputMethodManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(
                editText.applicationWindowToken,
                InputMethodManager.SHOW_FORCED,
                0
            )
        }
    }

    fun sendFileApi(urlPath: String, filePath: String, listener : OnSuccess) {

        showLoading()

        val file = File(filePath)

        val mBuilder = MultipartBody.Builder()
        mBuilder.setType(MultipartBody.FORM)
            .addFormDataPart("user_hash",kycPref.getHash(getContainerActivity())!!)
            .addFormDataPart("check_id", kycPref.getUserId(getContainerActivity())!!.toString())
            .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .build()

        val requestBody = mBuilder.build()

        disposable.add(
            apiService.sendFile(urlPath, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Any>() {

                    override fun onSuccess(model: Any) {
                        hideLoading()
                        listener.onRes()
                    }

                    override fun onError(e: Throwable) {
                        hideLoading()
                        showError(e, getContainerActivity())
                    }
                })
        )
    }

    interface OnSuccess{
        fun onRes()
    }

}
