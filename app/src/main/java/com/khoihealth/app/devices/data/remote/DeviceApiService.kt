package com.khoihealth.app.devices.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

data class BindDeviceBody(val address: String, val name: String, val firmware: String?)

interface DeviceApiService {
    @POST("devices/bind")
    suspend fun bindDevice(@Body body: BindDeviceBody): Response<Unit>

    @DELETE("devices/{address}")
    suspend fun unbindDevice(@Path("address") address: String): Response<Unit>
}
