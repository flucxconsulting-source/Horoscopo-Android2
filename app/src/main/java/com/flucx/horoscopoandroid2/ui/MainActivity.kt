package com.flucx.horoscopoandroid2.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flucx.horoscopoandroid2.R
import com.flucx.horoscopoandroid2.data.FavouriteStore
import com.flucx.horoscopoandroid2.data.HoroscopeRepository
import com.flucx.horoscopoandroid2.data.HoroscopeSign

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: View
    private lateinit var favouriteStore: FavouriteStore
    private lateinit var adapter: HoroscopeAdapter
    private var isGridMode = false
    private val signs = HoroscopeRepository.getAll()
    private var visibleSigns = signs
    private var favouriteOnly = false
    private var birthDate = ""
    private var selectedSignId: String? = null
    private var selectedElementRes: Int? = null
    private var selectedPlanetRes: Int? = null
    private var selectedColorRes: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_filter_list)
        toolbar.setNavigationContentDescription(R.string.action_open_filters)
        toolbar.setNavigationOnClickListener { showFilterDialog() }

        favouriteStore = FavouriteStore(this)
        recyclerView = findViewById(R.id.horoscopeRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        setupRecyclerView()
        renderEmptyState()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            applyFilters()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        updateViewModeIcon(menu.findItem(R.id.action_toggle_view))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_view -> {
                isGridMode = !isGridMode
                setupRecyclerView()
                updateViewModeIcon(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = HoroscopeAdapter(
            signs = visibleSigns,
            itemLayout = if (isGridMode) R.layout.item_horoscope_grid else R.layout.item_horoscope_list,
            favouriteSignId = favouriteStore.getFavouriteSignId(),
            onSignClick = ::openDetail,
            onFavouriteClick = ::toggleFavourite,
        )
        recyclerView.layoutManager = if (isGridMode) {
            GridLayoutManager(this, 2)
        } else {
            LinearLayoutManager(this)
        }
        recyclerView.adapter = adapter
    }

    private fun toggleFavourite(sign: HoroscopeSign) {
        favouriteStore.toggleFavourite(sign.id)
        applyFilters()
    }

    private fun openDetail(sign: HoroscopeSign) {
        val intent = Intent(this, DetailActivity::class.java)
            .putExtra(DetailActivity.EXTRA_SIGN_ID, sign.id)
        startActivity(intent)
    }

    private fun updateViewModeIcon(item: MenuItem) {
        if (isGridMode) {
            item.setIcon(R.drawable.ic_list_view)
            item.title = getString(R.string.action_list)
        } else {
            item.setIcon(R.drawable.ic_grid_view)
            item.title = getString(R.string.action_grid)
        }
    }

    private fun renderEmptyState() {
        emptyTextView.visibility = if (visibleSigns.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showFilterDialog() {
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val spacing = resources.getDimensionPixelSize(R.dimen.filter_dialog_spacing)
            setPadding(spacing, 0, spacing, 0)
        }
        val favouriteCheckBox = CheckBox(this).apply {
            text = getString(R.string.filter_favourite)
            isChecked = favouriteOnly
        }
        val dobEditText = EditText(this).apply {
            hint = getString(R.string.filter_dob_placeholder)
            setText(birthDate)
            setSingleLine(true)
        }

        contentLayout.addView(favouriteCheckBox)
        contentLayout.addView(labelView(R.string.filter_dob))
        contentLayout.addView(dobEditText)

        val signSpinner = contentLayout.addFilterSpinner(
            titleRes = R.string.filter_sign,
            options = listOf(FilterOption<String>(getString(R.string.filter_all), null)) +
                signs.map { FilterOption(getString(it.nameRes), it.id) },
            selectedValue = selectedSignId,
        )
        val elementSpinner = contentLayout.addFilterSpinner(
            titleRes = R.string.filter_element,
            options = stringResourceOptions(signs.map { it.elementRes }.distinct()),
            selectedValue = selectedElementRes,
        )
        val planetSpinner = contentLayout.addFilterSpinner(
            titleRes = R.string.filter_planet,
            options = stringResourceOptions(signs.map { it.planetRes }.distinct()),
            selectedValue = selectedPlanetRes,
        )
        val colorSpinner = contentLayout.addFilterSpinner(
            titleRes = R.string.filter_color,
            options = stringResourceOptions(signs.map { it.colorRes }.distinct()),
            selectedValue = selectedColorRes,
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.filters_title)
            .setView(contentLayout)
            .setPositiveButton(R.string.action_done) { _, _ ->
                favouriteOnly = favouriteCheckBox.isChecked
                birthDate = dobEditText.text.toString()
                selectedSignId = signSpinner.selectedValue()
                selectedElementRes = elementSpinner.selectedValue()
                selectedPlanetRes = planetSpinner.selectedValue()
                selectedColorRes = colorSpinner.selectedValue()
                applyFilters()
            }
            .setNegativeButton(R.string.action_clear) { _, _ ->
                clearFilters()
            }
            .show()
    }

    private fun applyFilters() {
        visibleSigns = signs.filter { sign ->
            val matchesFavourite = !favouriteOnly || favouriteStore.isFavourite(sign.id)
            val matchesBirthDate = birthDate.toBirthDateOrNull()?.let { sign.containsBirthDate(it) } ?: true
            val matchesSign = selectedSignId == null || sign.id == selectedSignId
            val matchesElement = selectedElementRes == null || sign.elementRes == selectedElementRes
            val matchesPlanet = selectedPlanetRes == null || sign.planetRes == selectedPlanetRes
            val matchesColor = selectedColorRes == null || sign.colorRes == selectedColorRes

            matchesFavourite &&
                matchesBirthDate &&
                matchesSign &&
                matchesElement &&
                matchesPlanet &&
                matchesColor
        }
        setupRecyclerView()
        renderEmptyState()
    }

    private fun clearFilters() {
        favouriteOnly = false
        birthDate = ""
        selectedSignId = null
        selectedElementRes = null
        selectedPlanetRes = null
        selectedColorRes = null
        applyFilters()
    }

    private fun labelView(@StringRes titleRes: Int): TextView {
        return TextView(this).apply {
            text = getString(titleRes)
        }
    }

    private fun <T> LinearLayout.addFilterSpinner(
        @StringRes titleRes: Int,
        options: List<FilterOption<T>>,
        selectedValue: T?,
    ): Spinner {
        addView(labelView(titleRes))
        return Spinner(this@MainActivity).also { spinner ->
            spinner.adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                options,
            )
            spinner.setSelection(options.indexOfFirst { it.value == selectedValue }.coerceAtLeast(0))
            addView(spinner)
        }
    }

    private fun stringResourceOptions(@StringRes values: List<Int>): List<FilterOption<Int>> {
        return listOf(FilterOption<Int>(getString(R.string.filter_all), null)) +
            values.map { FilterOption(getString(it), it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> Spinner.selectedValue(): T? {
        return (selectedItem as FilterOption<T>).value
    }

    private fun HoroscopeSign.containsBirthDate(date: BirthDate): Boolean {
        val (startText, endText) = getString(datesRes).split(" - ")
        val startDay = startText.toBirthDateFromRange().toDayOfYear()
        val endDay = endText.toBirthDateFromRange().toDayOfYear()
        val currentDay = date.toDayOfYear()

        return if (startDay <= endDay) {
            currentDay in startDay..endDay
        } else {
            currentDay >= startDay || currentDay <= endDay
        }
    }

    private fun String.toBirthDateOrNull(): BirthDate? {
        if (isBlank()) return null

        val parts = trim().split("-", "/")
        if (parts.size != 2) return null

        val month = parts[0].toIntOrNull() ?: return null
        val day = parts[1].toIntOrNull() ?: return null
        val daysInMonth = listOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        if (month !in 1..12) return null
        if (day !in 1..daysInMonth[month - 1]) return null

        return BirthDate(month = month, day = day)
    }

    private fun String.toBirthDateFromRange(): BirthDate {
        val parts = split(" ")
        return BirthDate(month = monthNumber(parts[0]), day = parts[1].toInt())
    }

    private fun BirthDate.toDayOfYear(): Int {
        val daysBeforeMonth = listOf(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335)
        return daysBeforeMonth[month - 1] + day
    }

    private fun monthNumber(month: String): Int = when (month) {
        "Jan" -> 1
        "Feb" -> 2
        "Mar" -> 3
        "Apr" -> 4
        "May" -> 5
        "Jun" -> 6
        "Jul" -> 7
        "Aug" -> 8
        "Sep" -> 9
        "Oct" -> 10
        "Nov" -> 11
        "Dec" -> 12
        else -> error("Unsupported month abbreviation: $month")
    }
}

private data class BirthDate(val month: Int, val day: Int)

private data class FilterOption<T>(val label: String, val value: T?) {
    override fun toString(): String = label
}
