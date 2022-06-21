package com.library.ekycnetset.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EKycModel {

    class BaseCheck{
        var api_access_token: String ?= null
        var api_refresh_token: String ?= null
        var blockchain_read_token: String ?= null
        var status: String ?= null
        var user_hash: String ?= null
        var user_id: Int ?= null
        var phone: String ?= null
    }

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("applicantId")
        @Expose
        var applicantId: String? = null

        @SerializedName("token")
        @Expose
        var token: String? = null
    }

}