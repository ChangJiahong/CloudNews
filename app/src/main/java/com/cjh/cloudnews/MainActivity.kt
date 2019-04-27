package com.cjh.cloudnews

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.cjh.cloudnews.adapter.NewsAdapter
import com.cjh.cloudnews.pojo.News
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import android.util.Log
import android.widget.AbsListView
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.support.v7.widget.DividerItemDecoration




class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NewsAdapter

    private val newsList = ArrayList<News>()

    private var start = 0

    private var count = 20



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        initData()

    }

    private fun initView() {


//
//        for (i in 0..5) {
//            val news = News()
//            news.title = "新闻$i"
//            newsList.add(news)
//        }

        adapter = NewsAdapter(data = newsList, recyclerView = listView)

        adapter.mIsHeaderEnable = true

        listView.layoutManager = LinearLayoutManager(this)

        listView.adapter = adapter

        // 下拉刷新
        refreshLayout.setOnRefreshListener {

            // 0,20,40,60,100
//            start += count
//            var end = start+count
//            if (end == 80){
//                end += count
//            }
//            getNews(start, end)

            doAsync {
                start = 0
                count = 20
                val data = getNews(0,20)
                if (data.isNotEmpty()){
                    newsList.clear()
                    newsList.addAll(data)
                }

                uiThread {
                    adapter.notifyDataSetChanged()
                    refreshLayout.isRefreshing = false
                    toast("刷新成功")
                }
            }


        }


        // 上拉加载
        listView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            /**
             * 向上滑动
             */
            var verticalSlideToUp = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG,"onScrolled-----$dx $dy")

                if (dx==0 && dy > 20){
                    verticalSlideToUp = true
                } else if (dx ==0 && dy < 20){
                    verticalSlideToUp = false
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)


                when(newState){
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE ->{
                        Log.d(TAG,"onScrollStateChanged-----$newState 滚动结束")


                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_FLING ->{
                        Log.d(TAG,"onScrollStateChanged-----$newState 正在滚动")
                    }
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ->{
                        Log.d(TAG,"onScrollStateChanged-----$newState 滚动开始")
                        val lm = recyclerView.layoutManager!!
                        val lastItemPosition = getLastVisiblePosition(lm)
                        val lastPosition = lm.itemCount-1


//                        recyclerView.
                        if (lastItemPosition == lastPosition && verticalSlideToUp){
                            Log.d(TAG,"onScrollStateChanged-----$lastItemPosition 到底了")

                            adapter.loadMore {
                                doAsync {


                                    // 0,20,40,60,100
                                    start += count
                                    var end = start+count
                                    if (end == 80){
                                        end += count
                                    }
                                    var data = getNews(start, end)

                                    if (data.isNotEmpty()) {
                                        newsList.addAll(data)
                                    }else{
                                        Thread.sleep(1000)
                                        uiThread {
                                            toast("已全部加载完成")
                                        }
                                    }

                                    Log.d("MAIN", "添加完datasize  ${newsList.size}")
                                    uiThread {
                                        adapter.loaded()
                                    }
                                }
                            }


                        }
                    }

                }

            }
        })



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        if (listView.layoutManager is LinearLayoutManager){
            listView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            //设置分隔线
//            listView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            listView.layoutManager = LinearLayoutManager(this)
        }

        listView.adapter = adapter

        return super.onOptionsItemSelected(item)
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private fun getLastVisiblePosition(layoutManager: RecyclerView.LayoutManager): Int {
        val position: Int

        position = when (layoutManager) {
            is LinearLayoutManager -> {
                layoutManager.findLastCompletelyVisibleItemPosition()
            }
            is GridLayoutManager -> {
                layoutManager.findLastCompletelyVisibleItemPosition()
            }
            is StaggeredGridLayoutManager -> {
                val lastPositions = layoutManager.findLastVisibleItemPositions(IntArray(layoutManager.spanCount))
                lastPositions.max()!!
            }
            else -> layoutManager.itemCount - 1
        }
        return position
    }


    fun initData(){

        doAsync {
            val data = getNews(0,20)
            newsList.clear()
            newsList.addAll(data)
            uiThread {
                adapter.notifyDataSetChanged()
            }
        }

//            Log.d(TAG, response.body()!!.string())

    }


    fun getNews(start: Int, end: Int) : ArrayList<News> {
        val newsList = ArrayList<News>()

        if (start >= 100|| end >= 100){
            return newsList
        }

        val url = "http://c.m.163.com/nc/article/headline/T1348647853363/$start-$end.html"

        val okHttpClient = OkHttpClient()

        val request = Request.Builder()
                .url(url)
                .removeHeader("User-Agent").addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0")
                .get()
                .build()

        val call = okHttpClient.newCall(request)

        val response = call.execute()

        if (response.isSuccessful) {
            val json = response.body()!!.string()

            val jsonObject = JSONObject(json)

            val jsonArray = jsonObject.getJSONArray("T1348647853363")

            Log.d(TAG,"jsonArraySize :${jsonArray.length()}")
            for (i in 0 until jsonArray.length()) {
                val jo = jsonArray.getJSONObject(i)
                val news = News()
                news.title = jo.getString("title")
                news.imgsrc = jo.getString("imgsrc")
                news.digest = jo.getString("digest")
                news.source = jo.getString("source")
                if (jo.has("url")) {
                    news.url = jo.getString("url")
                }
                newsList.add(news)
            }

        } else {
            runOnUiThread {
                toast("错误信息：${response.code()}")
            }
        }


        Log.d(TAG,"获得新闻条数：${newsList.size}")
        return newsList

    }

    val TAG = MainActivity::class.java.simpleName

}
