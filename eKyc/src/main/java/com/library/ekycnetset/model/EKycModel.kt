package com.library.ekycnetset.model

class EKycModel {

    class BaseCheck{
        var api_access_token: String ?= null
        var api_refresh_token: String ?= null
        var blockchain_read_token: String ?= null
        var status: String ?= null
        var user_hash: String ?= null
        var user_id: Int ?= null
    }
}