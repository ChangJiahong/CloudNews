package com.cjh.cloudnews

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import com.cjh.cloudnews.pojo.News
import kotlinx.android.synthetic.main.activity_news.*



class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val news = intent.getSerializableExtra("news") as News

        webView.loadUrl(news.url)
        val webSettings = webView.settings
        //允许使用js
        webSettings.javaScriptEnabled = true
        webSettings.blockNetworkImage = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
        }

    }
}
