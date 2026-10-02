package com.flucx.horoscopoandroid2.data.remote

import com.squareup.moshi.Json

data class DivineHoroscopeResponse(
    val data: DivineHoroscopeData? = null,
    val prediction: Map<String, String>? = null,
    val horoscope: String? = null,
)

data class DivineHoroscopeData(
    val date: String? = null,
    val period: String? = null,
    val sign: String? = null,
    val horoscope: String? = null,
    val prediction: Map<String, String>? = null,
    @param:Json(name = "bot_response") val botResponse: String? = null,
)
