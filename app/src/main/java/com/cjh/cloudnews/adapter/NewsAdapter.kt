package com.cjh.cloudnews.adapter

import android.nfc.Tag
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cjh.cloudnews.NewsActivity
import com.cjh.cloudnews.R
import com.cjh.cloudnews.pojo.News
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*

/**
 *
 * @author ChangJiahong
 * @date 2019/4/26
 */
class NewsAdapter(val data: ArrayList<News>,val recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val HEADER_VIEW = 0
        const val FOOTER_VIEW = 1
        const val NORMAL_VIEW = 2
        /**
         * list 布局
         */
        const val LIST_ITEM = 3
        /**
         * 网格布局
         */
        const val STAGGER_ITEM = 4


    }

    /**
     * 页脚
     */
    var mIsFooterEnable = false

    /**
     * 标头
     */
    var mIsHeaderEnable = false

    var isLoading = false



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {


        val v = LayoutInflater.from(p0.context).inflate(R.layout.news_list_item,p0,false)
        var holder: RecyclerView.ViewHolder
        holder = when(p1){
            FOOTER_VIEW ->{
                val fv = LayoutInflater.from(p0.context).inflate(R.layout.load_more,p0,false)
                FooterHolder(fv)
            }
            HEADER_VIEW ->{
                val hv = LayoutInflater.from(p0.context).inflate(R.layout.news_header_item,p0,false)
                Holder(hv)
            }
            LIST_ITEM ->{
                Holder(v)
            }
            STAGGER_ITEM ->{
                val sv = LayoutInflater.from(p0.context).inflate(R.layout.news_grid_item,p0,false)
                Holder(sv)
            }
            else ->{
                Holder(v)
            }
        }



        return holder
    }

    override fun getItemCount(): Int {
        var count = data.size
        return count
    }


    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {

        when(getItemViewType(position = p1)){
            LIST_ITEM, NORMAL_VIEW, STAGGER_ITEM, HEADER_VIEW->{
                var holder = p0 as Holder
                val item = data[p1]

                p0.titleV.text = item.title
                p0.digestV.text = item.digest
                p0.sourceV.text = item.source
                p0.v.setOnClickListener {
                    if (p1 >0) {
                        recyclerView.context.startActivity<NewsActivity>("news" to item)
                    }
                }
                Glide.with(p0.v.context).load(item.imgsrc).diskCacheStrategy(DiskCacheStrategy.NONE).into(p0.iconV)
            }

            FOOTER_VIEW ->{

            }
        }


    }


    override fun getItemViewType(position: Int): Int {

        val footerPosition = itemCount - 1
        val headerPosition = 0

        when(position){
            footerPosition ->{
                if (mIsFooterEnable){
                    return FOOTER_VIEW
                }
            }
            headerPosition ->{
                if (mIsHeaderEnable){
                    return HEADER_VIEW
                }
            }
            else ->{
                val lm = recyclerView.layoutManager
                return when (lm) {
                    is LinearLayoutManager -> LIST_ITEM
                    is StaggeredGridLayoutManager -> STAGGER_ITEM
                    is GridLayoutManager -> STAGGER_ITEM
                    else -> NORMAL_VIEW
                }
            }
        }

        return NORMAL_VIEW
    }

    class FooterHolder(val v: View) : RecyclerView.ViewHolder(v){
        val msg = v.find<TextView>(R.id.msg)
    }

    class HeaderHolder(val v: View) : RecyclerView.ViewHolder(v){
        val msg = v.find<TextView>(R.id.msg)
    }

    class Holder(val v: View) : RecyclerView.ViewHolder(v){

        val titleV = v.find<TextView>(R.id.title)
        val iconV = v.find<ImageView>(R.id.icon)
        val digestV = v.find<TextView>(R.id.digest)
        val sourceV = v.find<TextView>(R.id.source)
    }

    private var loadeVPosition = -1

    fun loadMore(loadData : ()->Unit){
        if (!isLoading) {


            isLoading = true
            data.add(News())
            loadeVPosition = data.size-1
            mIsFooterEnable = true
            notifyDataSetChanged()

            loadData()


        }
    }

    fun loaded(){
        if (isLoading) {
            Log.d("MAIN", "删除前datasize  ${data.size}")
            data.removeAt(loadeVPosition)
            Log.d("MAIN", "datasize  ${data.size}")

            mIsFooterEnable = false
            notifyDataSetChanged()

            isLoading = false
        }
    }

//    data class News(val title: String)
}