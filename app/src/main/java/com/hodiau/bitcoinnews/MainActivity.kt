package com.hodiau.bitcoinnews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.hodiau.startup.R
import com.hodiau.startup.BuildConfig;

class MainActivity : AppCompatActivity() {
    var apiKey:String = BuildConfig.NEWSAPI_API_KEY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}