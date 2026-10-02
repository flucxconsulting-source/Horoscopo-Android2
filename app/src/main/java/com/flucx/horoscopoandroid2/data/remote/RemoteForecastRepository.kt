package com.flucx.horoscopoandroid2.data.remote

import com.flucx.horoscopoandroid2.BuildConfig
import com.flucx.horoscopoandroid2.data.HoroscopePeriod
import com.flucx.horoscopoandroid2.data.HoroscopeSign
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RemoteForecastRepository(
    private val api: DivineHoroscopeApi,
    private val apiKey: String,
    private val authToken: String,
    private val timeZone: String = "1",
    private val language: String = "en",
) {
    suspend fun getForecast(sign: HoroscopeSign, period: HoroscopePeriod): String? {
        if (apiKey.isBlank() || authToken.isBlank()) return null

        val authorization = "Bearer $authToken"
        val response = when (period) {
            HoroscopePeriod.Today -> api.getDailyHoroscope(
                authorization = authorization,
                apiKey = apiKey,
                sign = sign.englishName,
                timeZone = timeZone,
                language = language,
            )
            HoroscopePeriod.Week -> api.getWeeklyHoroscope(
                authorization = authorization,
                apiKey = apiKey,
                sign = sign.englishName,
                timeZone = timeZone,
                language = language,
            )
            HoroscopePeriod.Month -> api.getMonthlyHoroscope(
                authorization = authorization,
                apiKey = apiKey,
                sign = sign.englishName,
                timeZone = timeZone,
                language = language,
            )
        }

        return response.extractReading()
    }

    private fun DivineHoroscopeResponse.extractReading(): String? {
        return listOfNotNull(
            data?.horoscope,
            data?.botResponse,
            data?.prediction?.readablePrediction(),
            prediction?.readablePrediction(),
            horoscope,
        ).firstOrNull { it.isNotBlank() }
    }

    private fun Map<String, String>.readablePrediction(): String? {
        return values
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n\n")
            .takeIf { it.isNotBlank() }
    }

    companion object {
        private const val BASE_URL = "https://astroapi-5.divineapi.com/"

        fun create(): RemoteForecastRepository {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return RemoteForecastRepository(
                api = retrofit.create(DivineHoroscopeApi::class.java),
                apiKey = BuildConfig.DIVINE_API_KEY,
                authToken = BuildConfig.DIVINE_AUTH_TOKEN,
            )
        }
    }
}
