package com.example.groww_assignment.data.remote.interceptors

import com.example.groww_assignment.utils.Constants
import com.example.groww_assignment.utils.Constants.CACHE_DURATION_MINUTES
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val cacheControl = CacheControl.Builder()
            .maxAge(CACHE_DURATION_MINUTES.toInt(), TimeUnit.MINUTES)
            .build()

        return response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
}