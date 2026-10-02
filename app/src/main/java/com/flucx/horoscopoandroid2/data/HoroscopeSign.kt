package com.flucx.horoscopoandroid2.data

import androidx.annotation.StringRes

data class HoroscopeSign(
    val id: String,
    @param:StringRes val nameRes: Int,
    @param:StringRes val datesRes: Int,
    val symbol: String,
    @param:StringRes val elementRes: Int,
    @param:StringRes val planetRes: Int,
    @param:StringRes val colorRes: Int,
    @param:StringRes val summaryRes: Int,
) {
    val englishName: String
        get() = id.replaceFirstChar { it.uppercase() }
}
