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

    @POST("auth/send-sms")
    fun sendSMS(@Body body: RequestBody): Single<EKycModel.BaseCheck>

    @POST("auth/check-sms-code ")
    fun checkSMSCode(@Body body: RequestBody): Single<EKycModel.BaseCheck>

    @GET("users/info/{id}/{api_key}/{signature}")
    fun getInfo(@Path("id") id: String, @Path("api_key") apiKey: String, @Path("signature") signature: String): Single<Any>

}

