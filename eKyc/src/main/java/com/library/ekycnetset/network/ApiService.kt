package com.application.linkodes.network

import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

//by : Deepak Kumar
//at : Netset Software
//in : Kotlin

interface ApiService {

    @POST("auth/base-check")
    fun baseCheck(@Body body: RequestBody): Single<Any>

    @POST("auth/send-sms")
    fun sendSMS(@Body body: RequestBody): Single<Any>

    @POST("auth/check-sms-code ")
    fun checkSMSCode(@Body body: RequestBody): Single<Any>

}

