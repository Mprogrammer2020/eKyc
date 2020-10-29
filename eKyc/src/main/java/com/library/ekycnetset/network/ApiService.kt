package com.application.linkodes.network

import com.library.ekycnetset.model.EKycModel
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

interface ApiService {

    @POST("auth/base-check")
    fun baseCheck(@Body body: RequestBody): Single<EKycModel.BaseCheck>
//    RESPONSE :
//    ----------
//    {
//        "api_access_token":"aat-70ee6cc3-1ed3-4541-8734-88c6f17f6197",
//        "api_refresh_token":"art-1052c5ca-470e-40db-816c-81cd73ec0cd8",
//        "blockchain_read_token":"",
//        "status":"ok",
//        "user_hash":"8d42a6fb-4e67-4a9a-a15f-b179996c1fcd",
//        "user_id":1515080
//    }

    @POST("auth/send-sms")
    fun sendSMS(@Body body: RequestBody): Single<Any>

    @POST("auth/check-sms-code ")
    fun checkSMSCode(@Body body: RequestBody): Single<Any>

    @GET("users/info/{id}/{api_key}/{signature}")
    fun getInfo(@Path("id") id: String, @Path("api_key") apiKey: String, @Path("signature") signature: String): Single<Any>

}

