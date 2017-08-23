package com.gorbuvla.stackreader.service

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.gorbuvla.stackreader.common.toStringDate
import com.gorbuvla.stackreader.domain.StackNewsItem
import com.gorbuvla.stackreader.domain.StackResponse
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import okio.Okio
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InterruptedIOException

/**
 * Created by vlad on 22/08/2017.
 */
class StorageService(private val c: Context, moshi: Moshi) {

    companion object {
        @JvmStatic
        private val PREF_FILE = "stackreader_pref"
        @JvmStatic
        private val CACHE_FILE = "app_cache.json"
        @JvmStatic
        private val PREV_LIST_POS = "LAST_LIST_POS_TAG"
        @JvmStatic
        private val PREV_SAVE_TIMESTAMP = "LAST_SAVE_TIMESTAMP"
        @JvmStatic
        private val PREV_FIRST_QID = "PREV_FQID"
        @JvmStatic
        private val PREV_LAST_QID = "PREV_LQID"
        @JvmStatic
        private val CACHE_WRITE_PERIOD_MILLIS = 10000
    }

    private var sp: SharedPreferences = c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    private val adapter = moshi.adapter(StackResponse::class.java)


    var prevScrollPosition: Int
        get() = sp.getInt(PREV_LIST_POS, 0)
        set(value) = sp.edit().putInt(PREV_LIST_POS, value).apply()

    var prevSessionTimestamp: Long
        get() = sp.getLong(PREV_SAVE_TIMESTAMP, 0)
        set(value) = sp.edit().putLong(PREV_SAVE_TIMESTAMP, value).apply()

    var prevSessionFirstQID: Int
        get() = sp.getInt(PREV_FIRST_QID, 0)
        set(value) = sp.edit().putInt(PREV_FIRST_QID, value).apply()

    var prevSessionLastQID: Int
        get() = sp.getInt(PREV_LAST_QID, 0)
        set(value) = sp.edit().putInt(PREV_LAST_QID, value).apply()


    fun writeToFile(scrollPos: Int, data: List<StackNewsItem>) : Observable<File> {
        return Observable.create(ObservableOnSubscribe<File> {
            subscriber ->

            try {

                val timestamp = System.currentTimeMillis()
                val lastCacheTime = prevSessionTimestamp

                if (timestamp - lastCacheTime < CACHE_WRITE_PERIOD_MILLIS) {
                    subscriber.onError(CacheTimingPeriodException("Unable to perform operation before time limit"))
                } else if (scrollPos !in 0..data.size) {
                    subscriber.onError(IllegalArgumentException("Scroll position is out of data size range"))
                } else {

                    val perm = ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    val file = File(Environment.getExternalStorageDirectory(), CACHE_FILE)

                    if (perm != PackageManager.PERMISSION_GRANTED) {
                        subscriber.onError(AccessDeniedException(file, reason = "Access denied, external storage unavailable"))
                    } else {

                        val sink = Okio.buffer(Okio.sink(file))


                        val jsonString = adapter.toJson(StackResponse(data))
                        val stream: InputStream = jsonString.byteInputStream()
                        sink.writeAll(Okio.source(stream))
                        sink.close()

                        prevSessionTimestamp = timestamp
                        prevScrollPosition = scrollPos
                        prevSessionFirstQID = data.first().question_id
                        prevSessionLastQID = data.last().question_id

                        subscriber.onNext(file)
                        subscriber.onComplete()
                    }
                }

            } catch (e: InterruptedIOException) {
                if (!subscriber.isDisposed) {
                    subscriber.onError(e)
                }
            }
        })
    }

    fun readFromFile() : Observable<Pair<List<StackNewsItem>, Int>> {
        return Observable.create( {
            subscriber ->

            val perm = ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val file = File(Environment.getExternalStorageDirectory(), CACHE_FILE)

            Log.i("STORAGE", "prev session stored: ${prevSessionTimestamp.toStringDate()}")
            if (perm != PackageManager.PERMISSION_GRANTED) {
                subscriber.onError(AccessDeniedException(file, reason = "Access denied, external storage unavailable"))
            } else if (!file.exists()) {
                subscriber.onError(FileNotFoundException("Cache file not present"))
            } else {

                Okio.buffer(Okio.source(file)).use {

                    val response = adapter.fromJson(it)
                    it.close()

                    if (response == null) {
                        subscriber.onError(IllegalStateException("Illegal state, cache file present, unsuccessful read"))
                    } else {
                        val list = response.items
                        val pos = prevScrollPosition

                        if (pos in 0..list.size) {
                            subscriber.onNext(Pair(list, pos))
                            subscriber.onComplete()
                        } else {
                            subscriber.onError(IllegalStateException("Scroll position exceeds list size"))
                        }
                    }
                }
            }
        })
    }
}


class CacheTimingPeriodException(override var message: String): Exception()