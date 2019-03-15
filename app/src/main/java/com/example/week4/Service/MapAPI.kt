package com.example.week4.Service

import com.example.week4.PlaceSearchModal.Result
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapAPI {
    @GET("place/autocomplete/json")
    fun getPlacesAutocomplete(
        @Query("input") input: String,
        @Query("key") key: String
    ): Call<Result>
}