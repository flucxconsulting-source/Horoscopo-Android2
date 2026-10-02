package com.flucx.horoscopoandroid2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flucx.horoscopoandroid2.R
import com.flucx.horoscopoandroid2.data.HoroscopeSign

class HoroscopeAdapter(
    private var signs: List<HoroscopeSign>,
    private val itemLayout: Int,
    private var favouriteSignId: String?,
    private val onSignClick: (HoroscopeSign) -> Unit,
    private val onFavouriteClick: (HoroscopeSign) -> Unit,
) : RecyclerView.Adapter<HoroscopeAdapter.HoroscopeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoroscopeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
        return HoroscopeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HoroscopeViewHolder, position: Int) {
        holder.bind(signs[position], signs[position].id == favouriteSignId)
    }

    override fun getItemCount(): Int = signs.size

    fun updateFavourite(favouriteSignId: String?) {
        this.favouriteSignId = favouriteSignId
        notifyDataSetChanged()
    }

    inner class HoroscopeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val symbolTextView: TextView = itemView.findViewById(R.id.symbolTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val datesTextView: TextView = itemView.findViewById(R.id.datesTextView)
        private val elementTextView: TextView = itemView.findViewById(R.id.elementTextView)
        private val favoriteImageButton: ImageButton = itemView.findViewById(R.id.favoriteImageButton)

        fun bind(sign: HoroscopeSign, isFavourite: Boolean) {
            val context = itemView.context
            symbolTextView.text = sign.symbol
            nameTextView.setText(sign.nameRes)
            datesTextView.setText(sign.datesRes)
            elementTextView.text = context.getString(R.string.label_element) + ": " + context.getString(sign.elementRes)
            favoriteImageButton.setImageResource(
                if (isFavourite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite,
            )
            favoriteImageButton.contentDescription = context.getString(
                if (isFavourite) R.string.action_remove_favourite else R.string.action_mark_favourite,
            )

            itemView.setOnClickListener { onSignClick(sign) }
            favoriteImageButton.setOnClickListener { onFavouriteClick(sign) }
        }
    }
}
