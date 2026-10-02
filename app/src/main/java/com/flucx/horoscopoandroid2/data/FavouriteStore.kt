package com.flucx.horoscopoandroid2.data

import android.content.Context

class FavouriteStore(context: Context) {
    private val preferences = context.getSharedPreferences("horoscope_preferences", Context.MODE_PRIVATE)

    fun getFavouriteSignId(): String? = preferences.getString(FAVOURITE_SIGN_ID, null)

    fun isFavourite(signId: String): Boolean = getFavouriteSignId() == signId

    fun toggleFavourite(signId: String): String? {
        val nextFavourite = if (isFavourite(signId)) null else signId
        preferences.edit()
            .putString(FAVOURITE_SIGN_ID, nextFavourite)
            .apply()
        return nextFavourite
    }

    private companion object {
        const val FAVOURITE_SIGN_ID = "favourite_sign_id"
    }
}
