package com.mullaihub.hiper.http.interfaces

import com.mullaihub.hiper.http.data.HiperResponse

interface Listener {
    fun onSuccess(response: HiperResponse)
    fun onFail(e: Exception)
}