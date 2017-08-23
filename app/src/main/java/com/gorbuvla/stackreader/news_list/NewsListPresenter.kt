package com.gorbuvla.stackreader.news_list

import android.util.Log
import com.gorbuvla.stackreader.domain.StackNewsItem
import com.gorbuvla.stackreader.domain.StackResponse
import com.gorbuvla.stackreader.service.StackAPI
import com.gorbuvla.stackreader.service.StorageService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by vlad on 20/08/2017.
 */
class NewsListPresenter
    @Inject constructor(
            private val api: StackAPI,
            private val storageService: StorageService
    ) : RequiredPresenterOps<RequiredViewCallback, StackNewsItem> {

    private var view: RequiredViewCallback? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()


    //execute news request
    private fun execute(o: Observable<Response<StackResponse>>, f: (list: List<StackNewsItem>) -> Unit) {
        view?.showLoading(true)
        compositeDisposable.add(
                o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { response ->
                            view?.showLoading(false)

                            if (response.isSuccessful) {
                                response.body()?.items?.let { list -> f(list) }
                            } else {
                                view?.displaySnackBarMsg("Error while loading. Try later", MessageType.ERROR)
                            }
                        },
                        { e ->
                            view?.showLoading(false)
                            view?.displaySnackBarMsg("Error while loading. Try later", MessageType.ERROR)
                        }))
    }


    override fun loadMostRecent(count: Int, adapter: AdapterModel<StackNewsItem>) =
            execute(api.getQuestions(pagesize = count, page = 1), { list: List<StackNewsItem> -> adapter.onMostRecentArrived(list)})


    override fun postload(adapter: AdapterModel<StackNewsItem>) {
        val b = if (adapter.getItemCount() > 0) adapter.getLast()?.creation_date.toString() else ""
        execute(api.getQuestions(toTimestamp = b), {list: List<StackNewsItem> -> adapter.onDataArrived(list)})
    }


    override fun loadPreviousSession(adapter: AdapterModel<StackNewsItem>) {
        compositeDisposable.add(
                storageService.readFromFile()
                .doOnError({ e -> Log.i("NewsListPresenter", "${e.message}"); loadMostRecent(20, adapter = adapter)  })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            (data, pos) -> adapter.onMostRecentArrived(data)
                                view?.scrollToPosition(pos)
                                view?.displaySnackBarMsg("Old data? Swipe down to refresh", MessageType.NOTIFY)
                        }
                ))
    }

    override fun saveCurrentSession(scrollPos: Int, adapter: AdapterModel<StackNewsItem>) {

        val (currFirst, currLast) = adapter.let { Pair(it.getFirst()?.question_id ?: 0, it.getLast()?.question_id ?: 0) }

        if (storageService.prevSessionFirstQID == currFirst && storageService.prevSessionLastQID == currLast) {
            storageService.prevScrollPosition = scrollPos
            return
        }

        compositeDisposable.add(
                storageService.writeToFile(scrollPos, adapter.getAll())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                        .doOnError { t -> Log.e("PRESENTER", "${t.message}") }
                        .subscribe())
    }

    override fun onAttach(v: RequiredViewCallback) {
        view = v
    }

    override fun onDetach() {
        compositeDisposable.dispose()
        view = null
    }
}