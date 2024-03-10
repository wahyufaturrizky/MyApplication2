package com.example.myapplication.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

public interface UserApi {
    @POST("auth/login")
    fun login(@Body user: Any): Call<Any?>?
}