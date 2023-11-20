package com.egpayawal.openweather.core

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class TestUtils {

    companion object {

        fun buildErrorResponseCheckEmail(): Response<String> {
            val sdf = "{\n" +
                "    \"message\": \"We couldn't find any records that matches your email.\",\n" +
                "    \"error_code\": \"EMAIL_NOT_FOUND\",\n" +
                "    \"http_status\": 404,\n" +
                "    \"success\": false\n" +
                "}"

            return Response.error<String>(404, sdf.toResponseBody("application/json".toMediaTypeOrNull()))
        }
    }
}
