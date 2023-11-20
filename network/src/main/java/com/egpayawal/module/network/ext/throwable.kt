package com.egpayawal.module.network.ext

import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.getThrowableError(): String {
    when (this) {
        is HttpException -> {
            val responseBody = this.response()?.errorBody()
            responseBody?.let {
                return getErrorMessage(it)
            }
        }
        is SocketTimeoutException -> return this.message!!
        is IOException -> return this.message!!
        else -> return this.message!!
    }
    return ""
}

private fun getErrorMessage(responseBody: ResponseBody): String {
    return try {
        val jsonObject = JSONObject(responseBody.string())
        return if (jsonObject.has("data") && jsonObject.get("data") is JSONArray) {
            jsonObject.getString("message")
        } else if (jsonObject.has("message") && jsonObject.get("message") is JSONArray) {
            // If message is a JsonArray
            val messageArray = jsonObject.getJSONArray("message")
            // Check if the array has items
            if (messageArray.length() >= 1) {
                // Get first item of array and return it
                return messageArray[0] as String
            }

            // If array is empty, return empty string
            ""
        } else {
            if (jsonObject.has("data")) {
                val dataObject = jsonObject.getJSONObject("data")
                if (dataObject.length() > 0) {
                    if (dataObject.has("error")) {
                        return dataObject.getString("error")
                    } else {
                        val name = dataObject.names()?.get(0) as String
                        if (dataObject.has(name)) {
                            dataObject.getJSONArray(name)[0] as String
                        } else {
                            jsonObject.getString(
                                "message"
                            )
                        }
                    }
                }
            }
            jsonObject.getString("message")
        }
    } catch (e: Exception) {
        e.message!!
    }
}
