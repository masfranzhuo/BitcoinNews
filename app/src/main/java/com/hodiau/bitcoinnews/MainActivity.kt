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
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    val apiKey:String = BuildConfig.NEWSAPI_API_KEY
    var listNews = ArrayList<News>()
    var adapter:NewsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // swipe refresh feature on list view
        swiperefresh.setOnRefreshListener(object:SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                listNews.clear()
                adapter = NewsAdapter(applicationContext, listNews)
                lvNews.adapter = adapter
                LoadData(1)
            }
        })

        // load more on scrool at list view
        lvNews.setOnScrollListener(object:EndlessScrollListener(2, 0) {
            override fun onLoadMore(page: Int, totalItemsCount: Int): Boolean {
                LoadData(page)
                return true
            }
        })

        //initialize adapter and set it to list view
        adapter = NewsAdapter(this, listNews)
        lvNews.adapter = adapter
        LoadData(1)
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
                    listNews.clear()
                    adapter = NewsAdapter(this, listNews)
                    lvNews.adapter = adapter
                    LoadData(1)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    fun LoadData(startPage:Int) {
        if(isNetworkConnected()) {
            var url = "https://newsapi.org/v2/everything?q=bitcoin&sortBy=publishedAt&language=en&page="+startPage+"&apiKey="+apiKey
            NewsAsyncTask(this, listNews, adapter!!,this, startPage).execute(url)
        } else {
            swiperefresh.setRefreshing(false)
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
}