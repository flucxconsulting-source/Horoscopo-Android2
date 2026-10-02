package com.flucx.horoscopoandroid2.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flucx.horoscopoandroid2.R
import com.flucx.horoscopoandroid2.data.FavouriteStore
import com.flucx.horoscopoandroid2.data.HoroscopePeriod
import com.flucx.horoscopoandroid2.data.HoroscopeRepository
import com.flucx.horoscopoandroid2.data.HoroscopeSign
import com.google.android.material.button.MaterialButtonToggleGroup

class DetailActivity : AppCompatActivity() {
    private lateinit var favouriteStore: FavouriteStore
    private lateinit var sign: HoroscopeSign
    private lateinit var favoriteImageButton: ImageButton
    private lateinit var readingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signId = intent.getStringExtra(EXTRA_SIGN_ID)
        sign = HoroscopeRepository.getById(signId) ?: run {
            finish()
            return
        }

        favouriteStore = FavouriteStore(this)
        favoriteImageButton = findViewById(R.id.favoriteImageButton)
        readingTextView = findViewById(R.id.readingTextView)

        setupToolbar()
        bindSign()
        bindFavourite()
        bindPeriodToggle()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(sign.nameRes)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun bindSign() {
        findViewById<TextView>(R.id.symbolTextView).text = sign.symbol
        findViewById<TextView>(R.id.nameTextView).setText(sign.nameRes)
        findViewById<TextView>(R.id.datesTextView).setText(sign.datesRes)
        findViewById<TextView>(R.id.summaryTextView).setText(sign.summaryRes)
        findViewById<TextView>(R.id.elementTextView).text = getString(R.string.label_element) + "\n" + getString(sign.elementRes)
        findViewById<TextView>(R.id.planetTextView).text = getString(R.string.label_planet) + "\n" + getString(sign.planetRes)
        findViewById<TextView>(R.id.colorTextView).text = getString(R.string.label_color) + "\n" + getString(sign.colorRes)
        renderReading(HoroscopePeriod.Today)
    }

    private fun bindFavourite() {
        renderFavourite()
        favoriteImageButton.setOnClickListener {
            favouriteStore.toggleFavourite(sign.id)
            renderFavourite()
        }
    }

    private fun bindPeriodToggle() {
        findViewById<MaterialButtonToggleGroup>(R.id.periodToggleGroup).addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val period = when (checkedId) {
                R.id.weekButton -> HoroscopePeriod.Week
                R.id.monthButton -> HoroscopePeriod.Month
                else -> HoroscopePeriod.Today
            }
            renderReading(period)
        }
    }

    private fun renderFavourite() {
        val isFavourite = favouriteStore.isFavourite(sign.id)
        favoriteImageButton.setImageResource(
            if (isFavourite) R.drawable.ic_favorite_selected else R.drawable.ic_favorite,
        )
        favoriteImageButton.contentDescription = getString(
            if (isFavourite) R.string.action_remove_favourite else R.string.action_mark_favourite,
        )
    }

    private fun renderReading(period: HoroscopePeriod) {
        readingTextView.text = when (period) {
            HoroscopePeriod.Today -> getString(sign.summaryRes)
            HoroscopePeriod.Week -> getString(sign.summaryRes) + "\n\n" + getString(sign.elementRes) + " energy supports steady progress this week."
            HoroscopePeriod.Month -> getString(sign.planetRes) + " sets the tone this month. Keep " + getString(sign.colorRes).lowercase() + " close as your reminder."
        }
    }

    companion object {
        const val EXTRA_SIGN_ID = "extra_sign_id"
    }
}
