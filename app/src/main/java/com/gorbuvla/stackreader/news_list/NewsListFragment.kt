package com.gorbuvla.stackreader.news_list


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorbuvla.stackreader.R
import com.gorbuvla.stackreader.common.BasicClickListener
import com.gorbuvla.stackreader.common.DividerDecoration
import com.gorbuvla.stackreader.common.RecyclerListener
import com.gorbuvla.stackreader.common.ScrollListener
import com.gorbuvla.stackreader.domain.StackNewsItem
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*
import javax.inject.Inject

/**
 * Created by vlad on 20/08/2017.
 */
class NewsListFragment : Fragment(), RequiredViewCallback {

    companion object {
        @JvmStatic
        private val GOT_IT_LABEL = "GOT IT"
        @JvmStatic
        private val RETRY_LABEL = "RETRY"
        @JvmStatic
        private val SCROLL_LABEL = "SCROLL UP"
        @JvmStatic
        private val PREV_POS = "PREV_POS"
        @JvmStatic
        private val PREV_SATE = "PREV_STATE"
    }

    @Inject
    lateinit var presenter: NewsListPresenter

    @Inject
    lateinit var adapter: NewsAdapter


    private lateinit var recyclerClickListener: RecyclerListener
    private lateinit var callback: RequiredParentViewCallback


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = try {
            context as RequiredParentViewCallback
        } catch (e: ClassCastException) {
            throw ClassCastException("Parent activity must implement required contract")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_list, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val list = adapter.getAll()
        if (list.size > 0) {
            outState.putParcelableArrayList(PREV_SATE, list as ArrayList<out Parcelable>)
            val prevPos = (recycler_view.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            outState.putInt(PREV_POS, prevPos)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        callback.newsComponent.inject(this)

        presenter.onAttach(this)

        swipe_layout.setOnRefreshListener { presenter.loadMostRecent(20, adapter = adapter)  }

        val sl = ScrollListener {presenter.postload(adapter)}

        recycler_view.addItemDecoration(DividerDecoration(context, LinearLayoutManager.VERTICAL))
        recycler_view.itemAnimator = DefaultItemAnimator()
        recycler_view.adapter = adapter

        recycler_view.apply {
            setHasFixedSize(true)
            clearOnScrollListeners()
            layoutManager = LinearLayoutManager(context)
            recyclerClickListener = RecyclerListener(context, this, object : BasicClickListener {
                override fun onClick(v: View, pos: Int) {
                    onItemClicked(pos)
                }
                override fun onLongCLick(v: View, pos: Int) {
                }
            })
            addOnItemTouchListener(recyclerClickListener)
            addOnScrollListener(sl)
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(PREV_SATE) && savedInstanceState.containsKey(PREV_POS)) {
            adapter.onMostRecentArrived(savedInstanceState.getParcelableArrayList<StackNewsItem>(PREV_SATE))
            recycler_view.scrollToPosition(savedInstanceState.getInt(PREV_POS))
        } else {
            presenter.loadPreviousSession(adapter)
        }
    }


    override fun scrollToPosition(pos: Int) = recycler_view.scrollToPosition(pos)


    override fun displaySnackBarMsg(msg: String, type: MessageType) {
        val (actionLabel, func, color) = when(type) {
            MessageType.ERROR -> Triple(RETRY_LABEL, { _: View -> presenter.postload(adapter) }, Color.RED)
            MessageType.NEW_DATA -> Triple(SCROLL_LABEL, { _: View -> recycler_view.scrollToPosition(0) }, Color.GREEN)
            MessageType.NOTIFY -> Triple(GOT_IT_LABEL, { _: View -> /* do nothing */ }, Color.YELLOW)
        }
        Snackbar.make(swipe_layout, msg, Snackbar.LENGTH_LONG)
                .setAction(actionLabel, func).setActionTextColor(color).show()
    }

    override fun onItemClicked(pos: Int) = callback.onItemSelected(adapter.getElementAt(pos))


    override fun showLoading(b: Boolean) {
        swipe_layout.isRefreshing = b
    }


    override fun onStop() {
        super.onStop()
        if (!activity.isChangingConfigurations) {
            val firstVisible = (recycler_view.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            presenter.saveCurrentSession(firstVisible, adapter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetach()
        recycler_view.clearOnScrollListeners()
        recycler_view.removeOnItemTouchListener(recyclerClickListener)
    }
}