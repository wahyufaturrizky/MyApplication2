package com.example.myapplication.api

import com.example.myapplication.LoginReqModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

public interface UserApi {
    @Headers(
        "Accept: application/json"
    )
    @POST("auth/login")
    abstract fun login(@Body reqBodyLoginState: Any): Call<LoginReqModel?>?
}