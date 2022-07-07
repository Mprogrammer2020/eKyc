package com.ekycnetset

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.base.Constants
import com.library.ekycnetset.model.Data
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val mList = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        alertDialog()

        eKyc.setOnClickListener {
            move()
        }

        mList.add(Data("First Name",true))
        mList.add(Data("Last Name",true))
        mList.add(Data("Middle Name",false))
        mList.add(Data("Email Address",true))
        mList.add(Data("Mobile Number",true))
        mList.add(Data("Date of Birth",true))
        mList.add(Data("Gender",false))
        mList.add(Data("Occupation",false))
        mList.add(Data("Country of Citizenship",true))
        mList.add(Data("Country of Residence",false))
        mList.add(Data("Address",false))
        mList.add(Data("City",false))
        mList.add(Data("Zip Code",false))
        mList.add(Data("Mobile Number Verification",false))
        mList.add(Data("Customer's ID or Passport",true))
        mList.add(Data("Bank statement or Address proof",false))
        mList.add(Data("Income statement",false))
        mList.add(Data("Take a selfie",true))
        mList.add(Data("Take a video",true))


        val moviesAdapter = MoviesAdapter(mList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recView.layoutManager = layoutManager
        recView.itemAnimator = DefaultItemAnimator()
        recView.adapter = moviesAdapter

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        storageHelper.storage.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.BASIS_REQ_CODE) {

            if (resultCode == Activity.RESULT_OK) {

                Log.e("RESULT","OK")

                if (data?.getStringExtra(Constants.BASIS_USER_HASH) != null) {
                    Log.e(
                        Constants.BASIS_USER_HASH,
                        data.getStringExtra(Constants.BASIS_USER_HASH)!!
                    )
                    Log.e(
                        Constants.BASIS_USER_ID,
                        data.getIntExtra(Constants.BASIS_USER_ID, 0).toString()
                    )
                }


                if (data?.getStringExtra(Constants.CHECK_ONE) != null){
                    Log.e(Constants.CHECK_ONE, data.getStringExtra(Constants.CHECK_ONE)!!)
                    Log.e(Constants.CHECK_TWO, data.getStringExtra(Constants.CHECK_TWO)!!)
                }
            }

            if (resultCode == RESULT_CANCELED){
                Log.e("RESULT","CANCELED")
            }

        }
//        else if (requestCode == 5000){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    // perform action when allow permission success
//                    move()
//                } else {
//                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

    }

    fun move(){

        var arrary = arrayListOf<String>()
        val intent = Intent(this,EKycActivity::class.java)
//        intent.putExtra(Constants.API_KEY, "prod-GyNtgjCefFzWrOCMjYFCdoxpZTumokrl")
        intent.putExtra(Constants.USER_AUTH_TOKEN, "prod-XWzdnqGSToqEqoZaPrprUfUunGiirYav")
        intent.putExtra(Constants.USER_ID, "0c1bdb3f-a75a-4640-9187-4caa68125bc8")
        intent.putExtra(Constants.API_BASE_URL, "https://stagingapi.e-fx.asia/"+ "EFXUserManagement/v1/api/")

        intent.putExtra(Constants.F_NAME, "Steve")
        intent.putExtra(Constants.L_NAME, "Roger")
        intent.putExtra(Constants.EMAIL, "steve.roger@gmail.com")
        intent.putExtra(Constants.PHONE_CODE, "+1")
        intent.putExtra(Constants.PHONE_NUMBER, "9876543210")
        intent.putExtra(Constants.ADDRESS, "New York, USA")
        intent.putExtra(Constants.DOB, "1994-11-04")
        intent.putExtra(Constants.NATIONALITY, "IN")
        intent.putExtra(Constants.REJECTED_ITEM_LIST, arrary)

        // in case of update
            intent.putExtra(Constants.BASIS_USER_HASH, "09841284-20dd-4d00-851d-3b5b651d415e")
            intent.putExtra(Constants.BASIS_USER_ID, "1667451")

        intent.putExtra(Constants.ADMIN_SETTINGS_LIST, mList)

        startActivityForResult(intent,Constants.BASIS_REQ_CODE)

    }

//    @RequiresPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//    @RequiresApi(Build.VERSION_CODES.R)
//    fun requestFullStorageAccess() {
//        startActivityForResult(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),5000)
//    }

    private fun alertDialog(){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("eKYC")
        //set message for alert dialog
        builder.setMessage("Prefilled data will be comes from app end, in this app we send dummy data to ekyc library.")
//        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Ok"){dialogInterface, which ->
            dialogInterface.dismiss()
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

//    data class Data(val name: String, var value: Boolean)
}

