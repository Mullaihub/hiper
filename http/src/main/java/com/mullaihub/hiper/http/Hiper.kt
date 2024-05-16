package com.mullaihub.hiper.http

import org.json.JSONObject
import com.mullaihub.hiper.http.controllers.Caller
import com.mullaihub.hiper.http.data.HiperResponse
import java.io.File
import java.net.Proxy


/* Hiper */

class Hiper {
    fun get(
        url: String, isStream: Boolean = false, byteSize: Int = 4096,
        args: HashMap<String, Any> = hashMapOf(),
        headers: HashMap<String, Any> = hashMapOf(),
        cookies: HashMap<String, Any> = hashMapOf(),
        username: String? = null,
        password: String? = null,
        timeout: Long? = null,
        proxy: Proxy? = null
    ): HiperResponse {
        return GetRequest(url, isStream, byteSize, args=args, headers=headers, cookies=cookies,
            username = username, password = password, timeout=timeout, proxy=proxy).sync()
    }

    fun post(
        url: String, isStream: Boolean = false, byteSize: Int = 4096,
        args: HashMap<String, Any> = hashMapOf(),
        form: HashMap<String, Any> = hashMapOf(),
        files: List<File> = listOf(),
        json: JSONObject? = null,
        headers: HashMap<String, Any> = hashMapOf(),
        cookies: HashMap<String, Any> = hashMapOf(),
        username: String? = null,
        password: String? = null,
        timeout: Long? = null,
        proxy: Proxy? = null
    ): HiperResponse {
        return PostRequest(url, isStream, byteSize, args=args, form=form, files=files, json=json, headers=headers,
            cookies=cookies, username=username, password = password, timeout = timeout, proxy = proxy).sync()
    }

    fun head(
        url: String, isStream: Boolean = false, byteSize: Int = 4096,
        args: HashMap<String, Any> = hashMapOf(),
        headers: HashMap<String, Any> = hashMapOf(),
        cookies: HashMap<String, Any> = hashMapOf(),
        username: String? = null,
        password: String? = null,
        timeout: Long? = null
    ): HiperResponse {
        return HeadRequest(url, isStream, byteSize, args=args, headers=headers, cookies=cookies,
            username = username, password = password, timeout=timeout).sync()
    }

    fun async() = asyncInstance ?: synchronized(this) {
        asyncInstance ?: Asynchronous().also { asyncInstance = it }
    }


    inner class Asynchronous {
        fun get(
            url: String, isStream: Boolean = false, byteSize: Int = 4096,
            args: HashMap<String, Any> = hashMapOf(),
            headers: HashMap<String, Any> = hashMapOf(),
            cookies: HashMap<String, Any> = hashMapOf(),
            username: String? = null,
            password: String? = null,
            timeout: Long? = null,
            proxy: Proxy? = null,
            callback: ((response: HiperResponse) -> Unit)? = null
        ): Caller {
            val req = GetRequest(url, isStream, byteSize, args=args, headers=headers, cookies=cookies,
                username = username, password = password, timeout=timeout, proxy = proxy)
            return req.async(callback)
        }

        fun post(
            url: String, isStream: Boolean = false, byteSize: Int = 4096,
            args: HashMap<String, Any> = hashMapOf(),
            form: HashMap<String, Any> = hashMapOf(),
            files: List<File> = listOf(),
            json: JSONObject? = null,
            headers: HashMap<String, Any> = hashMapOf(),
            cookies: HashMap<String, Any> = hashMapOf(),
            username: String? = null,
            password: String? = null,
            timeout: Long? = null,
            proxy: Proxy? = null,
            callback: ((response: HiperResponse) -> Unit)? = null
        ): Caller {
            val req = PostRequest(url, isStream, byteSize, args=args, form=form, files=files, json=json,
                headers=headers, cookies=cookies, username=username, password=password, timeout = timeout, proxy = proxy)
            return req.async(callback)
        }

        fun head(
            url: String, isStream: Boolean = false, byteSize: Int = 4096,
            args: HashMap<String, Any> = hashMapOf(),
            headers: HashMap<String, Any> = hashMapOf(),
            cookies: HashMap<String, Any> = hashMapOf(),
            username: String? = null,
            password: String? = null,
            timeout: Long? = null,
            callback: ((response: HiperResponse) -> Unit)? = null
        ): Caller {
            val req = HeadRequest(url, isStream, byteSize, args=args, headers=headers, cookies=cookies,
                username = username, password = password, timeout=timeout)
            return req.async(callback)
        }
    }

    companion object {
        @Volatile private var instance: Hiper? = null
        @Volatile private var asyncInstance: Asynchronous? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: Hiper().also { instance = it }
        }
    }
}