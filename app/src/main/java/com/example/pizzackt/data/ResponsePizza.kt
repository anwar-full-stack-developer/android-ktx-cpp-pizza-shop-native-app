package com.example.pizzackt.data

import com.google.gson.annotations.SerializedName


data class ResponsePizza(
    @SerializedName("data")
    var data: DataPizza?,

    @SerializedName("status")
    var status: Int,

    @SerializedName("message")
    var message: String?,
)