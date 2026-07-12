package com.lmstudio.chat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ModelsResponse(
    @SerializedName("object") val `object`: String = "list",
    @SerializedName("data") val data: List<ModelDto> = emptyList()
)

data class ModelDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("object") val `object`: String = "model",
    @SerializedName("created") val created: Long = 0,
    @SerializedName("owned_by") val ownedBy: String = "",
    @SerializedName("context_length") val contextLength: Int? = null,
    @SerializedName("max_context_length") val maxContextLength: Int? = null
)
