package com.example.networkcallmonitorinterceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.StandardCharsets


class HttpMonitorInterceptor : Interceptor {

    companion object{
        val networkDetails : MutableMap<String,String> = mutableMapOf()
    }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        // Log Request Details
        val requestStartTime = System.nanoTime()
        logRequest(request)

        // Proceed with the request
        val response: Response = chain.proceed(request)

        // Log Response Details
        val requestEndTime = System.nanoTime()
        logResponse(response, requestEndTime - requestStartTime)
        return response
    }

    private fun logRequest(request: Request) {
        networkDetails.put("url",request.url.toString())
        networkDetails.put("method",request.method)
        Log.d("Network Call","HTTP Request:")
        Log.d("Network Call","Network Call URL: " + request.url)
        Log.d("Network Call","Network Call Method: " + request.method)
        if (request.body != null) {
            try {
                // Log request body if it's a text-based body
                val requestBody: RequestBody = request.body!!
                if (requestBody != null && requestBody.contentType() != null && requestBody.contentType()
                        .toString().contains("text")
                ) {
                    val buffer: Buffer = Buffer()
                    requestBody.writeTo(buffer)
                    System.out.println("Body: " + buffer.readString(StandardCharsets.UTF_8))
                }
            } catch (e: Exception) {
                Log.e("Network Call","Failed to read request body: " + e.message)
            }
        }
    }

    @Throws(IOException::class)
    private fun logResponse(response: Response, duration: Long) {
        Log.d("Network Call","HTTP Response:")
        Log.d("Network Call","Network Call status code: " + response.code)
        Log.d("Network Call","Network Call Duration: " + duration / 1e6 + " ms")
        val time = (duration / 1e6);
        "$(duration / 1e6) ms";
        networkDetails.put("status_code",response.code.toString())
        networkDetails.put("duration",time.toString() + " ms")
        if (response.body != null) {
            val responseBody = response.peekBody(Long.MAX_VALUE)
            val contentLength = response.header("Content-Length")
            if (contentLength != null) {
                networkDetails.put("size","$contentLength bytes")
                Log.d("Network Call", "Response Size (Content-Length): $contentLength bytes")
            } else {
                // If Content-Length is not available, log the size by reading the response body
                val bodySize = responseBody.contentLength()
                networkDetails.put("size","$bodySize bytes")

                Log.d("Network Call", "Response Size (calculated): $bodySize bytes")
            }
            val contentType = responseBody.contentType()
            if (contentType != null && contentType.toString().contains("text")) {
                Log.d("Network Call","Body: " + responseBody.string())
            }
        }
    }

}