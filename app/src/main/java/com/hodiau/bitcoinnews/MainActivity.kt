package com.hodiau.bitcoinnews

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hodiau.startup.R
import com.hodiau.startup.BuildConfig
import kotlinx.android.synthetic.main.activity_main.*
import android.net.ConnectivityManager
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    val apiKey:String = BuildConfig.NEWSAPI_API_KEY
    val url = "https://newsapi.org/v2/everything?q=bitcoin&sortBy=publishedAt&language=en&page=1&apiKey="+apiKey

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swiperefresh.setOnRefreshListener(object:SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                LoadData()
            }
        })
        LoadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // make search work on UI
        val searchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Toast.makeText(applicationContext, p0, Toast.LENGTH_SHORT).show()
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null) {
            when(item.itemId) {
                R.id.menu_refresh -> {
                    LoadData()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    fun LoadData() {
        if(isNetworkConnected()) {
            NewsAsyncTask(this, this).execute(url)
        } else {
            swiperefresh.setRefreshing(false)
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}