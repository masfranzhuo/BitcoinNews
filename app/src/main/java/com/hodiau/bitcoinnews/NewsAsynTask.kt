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
    constructor(context: Context, activity: Activity) {
        this.context = context
        mWeakActivity = WeakReference(activity)
        this.activity = mWeakActivity!!.get()!!
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
                        var publishedAt: Date? = article.publishedAt
                        var checkNull = false
                        if(sourceName == null || author == null || title == null || description == null || url == null
                                || urlToImage == null || publishedAt == null) {
                            checkNull = true
                        }
                        if (!checkNull) listNews.add(News(sourceName!!, author!!, title!!, description!!, url!!, urlToImage!!, publishedAt!!, "data"))
                        //else listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "data"))
                    }
                    adapter = NewsAdapter(context!!, listNews)
                    activity!!.lvNews.adapter = adapter
                } else {
                    listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "nodata"))
                    adapter = NewsAdapter(context!!, listNews)
                    activity!!.lvNews.adapter = adapter
                }
            } else if(status.equals("error")) {
                listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "error"))
                adapter = NewsAdapter(context!!, listNews)
                activity!!.lvNews.adapter = adapter
            } else {
                // TO DO: make ui say it error
                listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", Date(), "error"))
                adapter = NewsAdapter(context!!, listNews)
                activity!!.lvNews.adapter = adapter
            }
        } catch(ex:Exception){}
    }
    override fun onPostExecute(result: String?) {
        //after task done
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