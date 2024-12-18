package com.example.networkcallmonitoring

import com.example.networkcallmonitorinterceptor.HttpMonitorInterceptor
import okhttp3.OkHttpClient

class NetworkClient {

    companion object {
        private var okHttpClient: OkHttpClient? = null

        // Singleton instance of OkHttpClient
        fun getHttpClient(): OkHttpClient? {
            if (okHttpClient == null) {
                // Initialize OkHttpClient with the custom interceptor
                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpMonitorInterceptor())
                    .build()
            }
            return okHttpClient
        }
    }

}