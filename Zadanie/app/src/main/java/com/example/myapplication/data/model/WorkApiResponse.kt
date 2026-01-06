package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class WorkApiResponse(
    val description: Any?,
    @SerializedName("first_publish_date")
    val firstPublishDate: String?
)
