package com.example.pizzackt.data

import com.google.gson.annotations.SerializedName

data class ResponseListPizza(
    @SerializedName("data")
    var data: List<DataPizza>?,

    @SerializedName("status")
    var status: Int,

    @SerializedName("message")
    var message: String,
)