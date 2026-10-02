package com.flucx.horoscopoandroid2.data.remote

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DivineHoroscopeApi {
    @FormUrlEncoded
    @POST("api/v5/daily-horoscope")
    suspend fun getDailyHoroscope(
        @Header("Authorization") authorization: String,
        @Field("api_key") apiKey: String,
        @Field("sign") sign: String,
        @Field("h_day") day: String = "today",
        @Field("tzone") timeZone: String,
        @Field("lan") language: String = "en",
    ): DivineHoroscopeResponse

    @FormUrlEncoded
    @POST("api/v5/weekly-horoscope")
    suspend fun getWeeklyHoroscope(
        @Header("Authorization") authorization: String,
        @Field("api_key") apiKey: String,
        @Field("sign") sign: String,
        @Field("week") week: String = "current",
        @Field("tzone") timeZone: String,
        @Field("lan") language: String = "en",
    ): DivineHoroscopeResponse

    @FormUrlEncoded
    @POST("api/v5/monthly-horoscope")
    suspend fun getMonthlyHoroscope(
        @Header("Authorization") authorization: String,
        @Field("api_key") apiKey: String,
        @Field("sign") sign: String,
        @Field("month") month: String = "current",
        @Field("tzone") timeZone: String,
        @Field("lan") language: String = "en",
    ): DivineHoroscopeResponse
}
