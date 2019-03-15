package com.example.week4.PlaceSearchModal

import com.google.gson.annotations.SerializedName

data class Prediction(
    val description: String,
    val id: String,
    @SerializedName("matched_substrings")
    val matchedSubstrings: ArrayList<MatchedSubstring>,
    @SerializedName("place_id")
    val placeId: String,
    val reference: String,
    @SerializedName("structured_formatting")
    val structuredFormatting: StructuredFormatting,
    val terms: ArrayList<Term>,
    val types: ArrayList<String>
)