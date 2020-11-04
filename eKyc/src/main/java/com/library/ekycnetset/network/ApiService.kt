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

    @Multipart
    @POST("auth/send-document")
    fun sendDocument(@Query("user_hash") userHash : String,
                     @Query("check_id") userId : Int,
                     @Query("step") side : Int,
                     @Part mFile: MultipartBody.Part) : Single<Any>

    @Multipart
    @POST("auth/{path}")
    fun sendFile(@Path("path") path : String,
                 @Query("user_hash") userHash : String,
                 @Query("check_id") userId : Int,
                 @Part mFile: MultipartBody.Part) : Single<Any>

//    @Multipart
//    @POST("auth/send-statement")
//    fun sendStatement(@Query("user_hash") userHash : String,
//                     @Query("check_id") userId : Int,
//                     @Part mFile: MultipartBody.Part) : Single<Any>
//
//    @Multipart
//    @POST("auth/send-proof-income")
//    fun sendIncomeStatement(@Query("user_hash") userHash : String,
//                     @Query("check_id") userId : Int,
//                     @Part mFile: MultipartBody.Part) : Single<Any>
//
//    @Multipart
//    @POST("auth/send-image")
//    fun sendImage(@Query("user_hash") userHash : String,
//                     @Query("check_id") userId : Int,
//                     @Part mFile: MultipartBody.Part) : Single<Any>
//
//    @Multipart
//    @POST("auth/send-video")
//    fun sendVideo(@Query("user_hash") userHash : String,
//                     @Query("check_id") userId : Int,
//                     @Part mFile: MultipartBody.Part) : Single<Any>

}

