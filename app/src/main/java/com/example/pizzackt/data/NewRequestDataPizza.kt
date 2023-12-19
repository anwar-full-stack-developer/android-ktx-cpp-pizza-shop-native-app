package com.example.pizzackt.data

import com.google.gson.annotations.SerializedName

data class NewRequestDataPizza(

    @SerializedName("title")
    var title: String,

    @SerializedName("details")
    var details: String,

    @SerializedName("status")
    var status: String,

    @SerializedName("price")
    var price: String,
    )