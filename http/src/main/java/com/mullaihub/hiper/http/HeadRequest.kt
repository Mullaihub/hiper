package com.mullaihub.hiper.http

import com.mullaihub.hiper.http.controllers.Caller
import com.mullaihub.hiper.http.data.Headers
import com.mullaihub.hiper.http.data.HiperResponse
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.riversun.okhttp3.OkHttp3CookieHelper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class HeadRequest(
    private val url: String,
    private val isStream: Boolean,
    private val byteSize: Int,
    private val args: HashMap<String, Any>,
    private val headers: HashMap<String, Any>,
    private val cookies: HashMap<String, Any>,
    private val username: String?,
    private val password: String?,
    private val timeout: Long?
) {
    private lateinit var client: OkHttpClient

    private fun build(): Request {
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder() ?: throw Exception("UrlBuilder returns null")
        val cookieManager = OkHttp3CookieHelper()
        val request = Request.Builder()

        for ((k, v) in headers) request.addHeader(k, v.toString())
        for ((k, v) in args) urlBuilder.addQueryParameter(k, v.toString())
        if (username != null && password != null) {
            request.addHeader("Authorization", Credentials.basic(username, password))
        }

        val u = urlBuilder.build().toString()
        for ((k, v) in cookies) cookieManager.setCookie(u, k, v.toString())
        val localClient = OkHttpClient.Builder()
            .cookieJar(cookieManager.cookieJar())
        if (timeout != null) {
            localClient.connectTimeout(timeout, TimeUnit.SECONDS)
            localClient.readTimeout(timeout, TimeUnit.SECONDS)
            localClient.writeTimeout(timeout, TimeUnit.SECONDS)
        }
        client = localClient.build()
        return request.url(urlBuilder.build().toString()).head().build()
    }

    fun sync(): HiperResponse {
        val request = build()
        val response = client.newCall(request).execute()
        val stream = response.body?.byteStream()
        var bytes: ByteArray? = null
        var text: String? = null
        val headers = Headers()

        if (!isStream) {
            bytes = readBytes(stream)
            text = String(bytes)
        }

        for ((k, v) in response.headers) headers[k.lowercase()] = v

        return HiperResponse(
            isRedirect = response.isRedirect,
            statusCode = response.code,
            statusMessage = response.message,
            text = text,
            content = bytes,
            stream = stream,
            headers = headers,
            isSuccessful = response.isSuccessful
        )
    }

    fun async(callback: ((response :HiperResponse) -> Unit)? = null): Caller {
        val request = build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback?.invoke(HiperResponse(error = e))
            }

            override fun onResponse(call: Call, response: Response) {
                val stream = response.body?.byteStream()
                var bytes: ByteArray? = null
                var text: String? = null
                val headers = Headers()

                if (!isStream) {
                    bytes = readBytes(stream)
                    text = String(bytes)
                }

                for ((k, v) in response.headers) headers[k.lowercase()] = v

                val hiperResponse = HiperResponse(
                    isRedirect = response.isRedirect,
                    statusCode = response.code,
                    statusMessage = response.message,
                    text = text,
                    content = bytes,
                    stream = stream,
                    headers = headers,
                    isSuccessful = response.isSuccessful
                )
                callback?.invoke(hiperResponse)
            }
        })

        return Caller(call)
    }

    private fun readBytes(inputStream: InputStream?): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val buffer = ByteArray(byteSize)
        var len: Int
        while (true) {
            if (inputStream == null) break
            len = inputStream.read(buffer)
            if (len == -1) break
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }
}