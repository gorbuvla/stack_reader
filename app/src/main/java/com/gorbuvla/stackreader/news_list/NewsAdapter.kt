package com.gorbuvla.stackreader.news_list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorbuvla.stackreader.R
import com.gorbuvla.stackreader.common.fromHTML
import com.gorbuvla.stackreader.domain.StackNewsItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_list_item.view.*

/**
 * Created by vlad on 20/08/2017.
 */
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(), AdapterModel<StackNewsItem> {

    private var dataList = mutableListOf<StackNewsItem>()


    override fun getItemCount() = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NewsViewHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(R.layout.layout_list_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun onDataArrived(data: List<StackNewsItem>) {
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    //clear existing data and add most recent
    override fun onMostRecentArrived(data: List<StackNewsItem>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getAll() = dataList

    override fun getFirst() = dataList.firstOrNull()

    override fun getLast() = dataList.lastOrNull()


    fun getElementAt(i: Int) : StackNewsItem {
        return dataList[i]
    }

    //view holder class for recyclerview
    inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(newsItem: StackNewsItem) {

            itemView.apply {
                Picasso.with(context).load(newsItem.owner.profile_image).noFade().fit().into(list_item_avatar)
                list_item_username.text = newsItem.owner.display_name
                list_item_qtitle.text = "Q: ${newsItem.title.fromHTML()}"
                list_item_answered.text = if (newsItem.answer_count > 0) { "Answers: ${newsItem.answer_count}" } else { "" }
            }
        }
    }

}