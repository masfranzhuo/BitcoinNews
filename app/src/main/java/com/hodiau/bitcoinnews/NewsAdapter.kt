package com.hodiau.bitcoinnews

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hodiau.startup.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_ticket.view.*
import kotlinx.android.synthetic.main.news_ticket.view.*
import java.text.SimpleDateFormat
import java.util.ArrayList

/**
 * Created by OS on 12/25/2017.
 */
class NewsAdapter: BaseAdapter {
    var context: Context? = null
    var layoutInflater:LayoutInflater? = null
    var listNews = ArrayList<News>()

    constructor(context: Context, myListNews: ArrayList<News>) : super() {
        this.context = context
        layoutInflater = LayoutInflater.from(context)
        listNews = myListNews
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val news = listNews[p0]
        if (news.viewType.equals("nodata")) {
            var myView = layoutInflater!!.inflate(R.layout.message_ticket, null)
            myView.tvMessage.text = "Load data error"
            return myView
        } else if (news.viewType.equals("error")) {
            var myView = layoutInflater!!.inflate(R.layout.message_ticket, null)
            myView.tvMessage.text = "No data found"
            return myView
        } else {
            val myView = layoutInflater!!.inflate(R.layout.news_ticket, null)
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
                context!!.startActivity(intent)
            })
            return myView
        }
    }

    override fun getItem(p0: Int): Any {
        return listNews[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return listNews.size
    }
}