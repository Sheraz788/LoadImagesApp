package com.sheraz.loadimagesapp.model

import com.google.gson.annotations.SerializedName

data class AlternativeSlugs(

    @SerializedName("en" ) var en : String? = null,
    @SerializedName("es" ) var es : String? = null,
    @SerializedName("ja" ) var ja : String? = null,
    @SerializedName("fr" ) var fr : String? = null,
    @SerializedName("it" ) var it : String? = null,
    @SerializedName("ko" ) var ko : String? = null,
    @SerializedName("de" ) var de : String? = null,
    @SerializedName("pt" ) var pt : String? = null

)
