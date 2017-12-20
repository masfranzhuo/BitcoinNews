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
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var apiKey:String = BuildConfig.NEWSAPI_API_KEY
    var listNews = ArrayList<News>()
    var adapter:myNewsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        // dummy data
        listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "dummy"))
        listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "dummy"))
        adapter = myNewsAdapter(this, listNews)
        lvNews.adapter = adapter
        */
        listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "loading"))
        adapter = myNewsAdapter(this, listNews)
        lvNews.adapter = adapter

        GetNews(this)
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
            } else {
                val myView = layoutInflater.inflate(R.layout.news_ticket, null)
                myView.tvPublishedAt.text = news.publishedAt
                //myView.tvPublishedAt.text = ConvertUTCToLocale(news.publishedAt!!).toString()
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
        val url = "https://newsapi.org/v2/top-headlines?q=bitcoin&apiKey="+apiKey
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
            try{
                listNews.clear()
                var json = JSONObject(values[0])
                val status = json.getString("status") // use for check condition
                if(status.equals("ok")) {
                    val totalResults = json.getInt("totalResults") // use for check condition
                    if(totalResults > 0) {
                        val articles = json.getJSONArray("articles")
                        for (i in 0..(articles.length() - 1)) {
                            var checkNull = false;
                            val article = articles.getJSONObject(i)
                            val source = article.getJSONObject("source")
                            var sourceName:String? = null
                            if (source.has("name") && !source.isNull("name")) {
                                sourceName = source.getString("name")
                            } else {
                                checkNull = true
                            }
                            var author:String? = null
                            if (article.has("author") && !article.isNull("author")) {
                                author = article.getString("author")
                            } else {
                                checkNull = true
                            }
                            var title:String? = null
                            if (article.has("title") && !article.isNull("title")) {
                                title = article.getString("title")
                            } else {
                                checkNull = true
                            }
                            var description:String? = null
                            if (article.has("description") && !article.isNull("description")) {
                                description = article.getString("description")
                            } else {
                                checkNull = true
                            }
                            var url:String? = null
                            if (article.has("url") && !article.isNull("url")) {
                                url = article.getString("url")
                            } else {
                                checkNull = true
                            }
                            var urlToImage:String? = null
                            if (article.has("urlToImage") && !article.isNull("urlToImage")) {
                                urlToImage = article.getString("urlToImage")
                            } else {
                                checkNull = true
                            }
                            var publishedAt:String? = null
                            if (article.has("publishedAt") && !article.isNull("publishedAt")) {
                                publishedAt = article.getString("publishedAt")
                            } else {
                                checkNull = true
                            }
                            // add to list if no param has null value
                            if (!checkNull) listNews.add(News(sourceName!!, author!!, title!!, description!!, url!!, urlToImage!!, publishedAt!!, "data"))
                            //else listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "data"))
                        }
                        adapter = myNewsAdapter(this.context!!, listNews)
                        lvNews.adapter = adapter
                    } else {
                        listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "nodata"))
                        adapter = myNewsAdapter(this.context!!, listNews)
                        lvNews.adapter = adapter
                    }
                } else if(status.equals("error")) {
                    listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "error"))
                    adapter = myNewsAdapter(this.context!!, listNews)
                    lvNews.adapter = adapter
                } else {
                    // TO DO: make ui say it error
                    listNews.add(News("Source", "Author", "Title", "Description", "URL", "imageURL", "Published At", "error"))
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

    fun ConvertUTCToLocale(date:String):Date {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        val myDate = simpleDateFormat.parse(date)
        return myDate
    }
}