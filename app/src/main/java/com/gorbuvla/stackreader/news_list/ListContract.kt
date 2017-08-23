package com.gorbuvla.stackreader.news_list

import com.gorbuvla.stackreader.di.components.NewsComponent
import com.gorbuvla.stackreader.domain.StackNewsItem

/**
 * Created by vlad on 20/08/2017.
 */

enum class MessageType {
    ERROR, NOTIFY, NEW_DATA
}

//required view operations visible to presenter
interface RequiredViewCallback {
    fun onItemClicked(pos: Int)
    fun showLoading(b: Boolean)
    fun displaySnackBarMsg(msg: String, type: MessageType)
    fun scrollToPosition(pos: Int)
}

//required interface to be implemented by parent activity
interface RequiredParentViewCallback {
    val newsComponent: NewsComponent
    fun onItemSelected(item: StackNewsItem)
}

interface RequiredPresenterOps<V, T> {
    fun onAttach(v: V)
    fun onDetach()

    fun postload(adapter: AdapterModel<T>)
    fun loadMostRecent(count: Int, adapter: AdapterModel<T>)

    fun loadPreviousSession(adapter: AdapterModel<T>)
    fun saveCurrentSession(scrollPos: Int, adapter: AdapterModel<T>)

}

//adapter interface visible to presenter
interface AdapterModel<T> {
    fun onDataArrived(data: List<T>)
    fun onMostRecentArrived(data: List<T>)
    fun getItemCount() : Int
    fun getFirst() : T?
    fun getLast() : T?
    fun getAll() : List<T>
}
