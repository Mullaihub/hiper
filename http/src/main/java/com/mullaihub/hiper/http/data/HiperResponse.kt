package com.mullaihub.hiper.http.data

import java.io.IOException
import java.io.InputStream

data class HiperResponse(
    val isRedirect: Boolean = false,
    val statusCode: Int = -1,
    val statusMessage: String = "",
    var text: String? = null,
    var content: ByteArray? = null,
    var stream: InputStream? = null,
    val headers: Headers = Headers(),
    var isSuccessful: Boolean = false,
    var error: IOException? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HiperResponse

        if (isRedirect != other.isRedirect) return false
        if (statusCode != other.statusCode) return false
        if (statusMessage != other.statusMessage) return false
        if (text != other.text) return false
        if (content != null) {
            if (other.content == null) return false
            if (!content!!.contentEquals(other.content!!)) return false
        } else if (other.content != null) return false
        if (headers != other.headers) return false
        if (isSuccessful != other.isSuccessful) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isRedirect.hashCode()
        result = 31 * result + statusCode
        result = 31 * result + statusMessage.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (content?.contentHashCode() ?: 0)
        result = 31 * result + headers.hashCode()
        result = 31 * result + isSuccessful.hashCode()
        return result
    }

    fun close() {
        stream?.close()
    }
}