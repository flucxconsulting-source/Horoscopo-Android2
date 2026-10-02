package com.flucx.horoscopoandroid2.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

        favouriteStore = FavouriteStore(this)
        recyclerView = findViewById(R.id.horoscopeRecyclerView)
        emptyTextView = findViewById(R.id.emptyTextView)
        setupRecyclerView()
        renderEmptyState()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.updateFavourite(favouriteStore.getFavouriteSignId())
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
            signs = signs,
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
        val favouriteSignId = favouriteStore.toggleFavourite(sign.id)
        adapter.updateFavourite(favouriteSignId)
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
        emptyTextView.visibility = if (signs.isEmpty()) View.VISIBLE else View.GONE
    }
}
