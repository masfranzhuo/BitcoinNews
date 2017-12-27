package com.hodiau.bitcoinnews

import android.content.Context
import android.os.AsyncTask
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import android.app.Activity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

/**
 * Created by OS on 12/25/2017.
 */
class NewsAsyncTask: AsyncTask<String, String, String> {
    var context: Context? = null
    var listNews = ArrayList<News>()
    var adapter:NewsAdapter? = null
    var mWeakActivity: WeakReference<Activity>? = null
    var activity:Activity? = null
    var page:Int? = 1
    constructor(context: Context, listNews: ArrayList<News>, adapter: NewsAdapter, activity: Activity, page: Int) {
        this.context = context
        this.listNews = listNews
        this.adapter = adapter
        mWeakActivity = WeakReference(activity)
        this.activity = mWeakActivity!!.get()!!
        this.page = page
    }
    override fun onPreExecute() {
        //before task started
        activity!!.swiperefresh.setRefreshing(true)
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
                        var publishedAt: Date? = article.publishedAt
                        var checkNull = false
                        if(sourceName == null || author == null || title == null || description == null || url == null
                                || urlToImage == null || publishedAt == null) {
                            checkNull = true
                        }
                        if (!checkNull) listNews.add(News(sourceName!!, author!!, title!!, description!!, url!!, urlToImage!!, publishedAt!!, "data"))
                        //else listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "data"))
                    }
                } else {
                    Toast.makeText(context!!, "No data to retrieve", Toast.LENGTH_SHORT).show()
                }
            } else if(status.equals("error")) {
                Toast.makeText(context!!, "Retrieve data error", Toast.LENGTH_SHORT).show()
            } else {
                // TO DO: make ui say it error
                Toast.makeText(context!!, "Retrieve data error", Toast.LENGTH_SHORT).show()
            }
        } catch(ex:Exception){}
    }
    override fun onPostExecute(result: String?) {
        //after task done
        adapter!!.notifyDataSetChanged()
        activity!!.swiperefresh.setRefreshing(false)
    }

    fun ConvertStreamToString(inputStream: InputStream):String{
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