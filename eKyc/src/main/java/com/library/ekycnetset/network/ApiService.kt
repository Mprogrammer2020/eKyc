package com.application.linkodes.network

import com.library.ekycnetset.model.EKycModel
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

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

    @POST("auth/send-document")
    fun sendDocument(@Body boby: RequestBody) : Single<Any>

    @POST("auth/{path}")
    fun sendFile(@Path("path") path : String,
                 @Body boby: RequestBody) : Single<Any>


    @POST("users/{id}/uploadDataForKYC")
    fun uploadBasicInfo(@Path("id") id: String, @Body body: RequestBody): Single<EKycModel>

    @POST("users/{id}/createCheck/{videoId}")
    fun createCheckApi(@Path("id") id: String, @Path("videoId") videoId: String, @Body body: RequestBody): Single<EKycModel>

    @GET("users/{id}/getSDKToken")
    fun getOnfidoSdkToken(@Path("id") userId: String, @Query("documentType") documentType: String): Single<EKycModel>
}

