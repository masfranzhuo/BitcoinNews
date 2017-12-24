package com.hodiau.bitcoinnews

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hodiau.startup.R
import com.hodiau.startup.BuildConfig
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.news_ticket.view.*
import android.os.AsyncTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_ticket.view.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.net.ConnectivityManager
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.load_ticket.view.*


class MainActivity : AppCompatActivity() {
    var apiKey:String = BuildConfig.NEWSAPI_API_KEY
    var listNews = ArrayList<News>()
    var adapter:myNewsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadData()
    }

    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    fun LoadData() {
        if(isNetworkConnected()) {
            // add loading progress bar before load data
            listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "loading"))
            adapter = myNewsAdapter(this, listNews)
            lvNews.adapter = adapter

            // get data
            GetNews(this)
        } else {
            listNews.clear()
            // show button ui to load data
            listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "loaddata"))
            adapter = myNewsAdapter(this, listNews)
            lvNews.adapter = adapter

            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    inner class myNewsAdapter:BaseAdapter {
        var context: Context? = null
        var myListNews = ArrayList<News>()
        constructor(context:Context, myListNews:ArrayList<News>):super() {
            this.context = context
            this.myListNews = myListNews
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val news = this.myListNews[p0]
            if (news.viewType.equals("loading")) {
                var myView = layoutInflater.inflate(R.layout.loading_ticket,null)
                return myView
            } else if (news.viewType.equals("nodata")) {
                var myView = layoutInflater.inflate(R.layout.message_ticket,null)
                myView.tvMessage.text = "Load data error"
                return myView
            } else if (news.viewType.equals("error")) {
                var myView = layoutInflater.inflate(R.layout.message_ticket,null)
                myView.tvMessage.text = "No data found"
                return myView
            } else if (news.viewType.equals("loaddata")) {
                var myView = layoutInflater.inflate(R.layout.load_ticket,null)
                myView.buGetData.setOnClickListener({
                    LoadData()
                })
                return myView
            } else {
                val myView = layoutInflater.inflate(R.layout.news_ticket, null)
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                myView.tvPublishedAt.text = sdf.format(news.publishedAt).toString()
                myView.tvTitle.text = news.title
                myView.tvDescription.text = news.description
                myView.tvAuthor.text = news.author
                myView.tvSource.text = news.source
                Picasso.with(context).load(news.imageURL).into(myView.ivImage)
                myView.llURL.setOnClickListener({
                    val url = news.URL
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                })
                return myView
            }
        }
        override fun getItem(p0: Int): Any {
            return this.myListNews[p0]
        }
        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }
        override fun getCount(): Int {
            return this.myListNews.size
        }
    }

    fun GetNews(context:Context){
		val url = "https://newsapi.org/v2/everything?q=bitcoin&sortBy=publishedAt&language=en&page=1&&apiKey="+apiKey
        MyAsyncTask(context).execute(url)
    }

    inner class MyAsyncTask: AsyncTask<String, String, String> {
        var context: Context? = null
        constructor(context:Context) {
            this.context = context
        }
        override fun onPreExecute() {
            //before task started
        }
        override fun doInBackground(vararg p0: String?): String {
            try{
                val url = URL(p0[0])
                val urlConnect = url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout = 1000*10

                var inString = ConvertStreamToString(urlConnect.inputStream)

                //cannot access ui here
                publishProgress(inString)
            } catch(ex:Exception){}
            return " "
        }
        override fun onProgressUpdate(vararg values: String?) {
            listNews.clear()
            try{
                var gsonBuilder = GsonBuilder()
                var gson = gsonBuilder.create()
                var news:News = gson.fromJson(values[0], News::class.java)

                if(news.status.equals("ok")) {
                    if(news.totalResults!! > 0) {
                        val articles = news.articles
                        for(article:Article in articles!!) {
                            var sourceName:String? = article.source!!.name
                            var author:String? = article.author
                            var title:String? = article.title
                            var description:String? = article.description
                            var url:String? = article.url
                            var urlToImage:String? = article.urlToImage
                            var publishedAt:Date? = article.publishedAt
                            var checkNull = false
                            if(sourceName == null || author == null || title == null || description == null || url == null
                                    || urlToImage == null || publishedAt == null) {
                                checkNull = true
                            }
                            if (!checkNull) listNews.add(News(sourceName!!, author!!, title!!, description!!, url!!, urlToImage!!, publishedAt!!, "data"))
                            //else listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "data"))
                        }
                        adapter = myNewsAdapter(this.context!!, listNews)
                        lvNews.adapter = adapter
                    } else {
                        listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "nodata"))
                        adapter = myNewsAdapter(this.context!!, listNews)
                        lvNews.adapter = adapter
                    }
                } else if(status.equals("error")) {
                    listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "error"))
                    adapter = myNewsAdapter(this.context!!, listNews)
                    lvNews.adapter = adapter
                } else {
                    // TO DO: make ui say it error
                    listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "error"))
                    adapter = myNewsAdapter(this.context!!, listNews)
                    lvNews.adapter = adapter
                }
            } catch(ex:Exception){}
        }
        override fun onPostExecute(result: String?) {
            //after task done
        }
    }

    fun ConvertStreamToString(inputStream:InputStream):String{
        val buffferReader = BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String = ""
        try{
            do{
                line = buffferReader.readLine()
                if(line != null) AllString += line
            } while (line!=null)
            inputStream.close()
        } catch(ex:Exception){}
        return AllString
    }
}