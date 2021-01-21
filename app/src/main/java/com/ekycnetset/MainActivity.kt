package com.ekycnetset

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.library.ekycnetset.EKycActivity
import com.library.ekycnetset.base.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eKyc.setOnClickListener {

            val intent = Intent(this,EKycActivity::class.java)
            intent.putExtra(Constants.API_KEY, "prod-GyNtgjCefFzWrOCMjYFCdoxpZTumokrl")



            startActivityForResult(intent,Constants.BASIS_REQ_CODE)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.BASIS_REQ_CODE) {

            if (resultCode == Activity.RESULT_OK) {

                Log.e("RESULT","OK")

                Log.e(Constants.BASIS_USER_HASH, data!!.getStringExtra(Constants.BASIS_USER_HASH)!!)
                Log.e(Constants.BASIS_USER_ID, data.getIntExtra(Constants.BASIS_USER_ID,0).toString())

            }

            if (resultCode == RESULT_CANCELED){
                Log.e("RESULT","CANCELED")
            }

        }

    }
}