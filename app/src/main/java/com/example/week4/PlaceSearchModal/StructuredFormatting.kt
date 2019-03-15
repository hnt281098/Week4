package com.example.week4.PlaceSearchModal

import com.google.gson.annotations.SerializedName

data class StructuredFormatting(
    @SerializedName("main_text")
    val mainText: String,
    @SerializedName("main_text_matched_substrings")
    val mainTextMatchedSubstrings: ArrayList<MainTextMatchedSubstring>,
    @SerializedName("secondary_text")
    val secondaryText: String
)