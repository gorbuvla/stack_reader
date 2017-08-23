package com.gorbuvla.stackreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.gorbuvla.stackreader.di.components.DaggerNewsComponent
import com.gorbuvla.stackreader.di.components.NewsComponent
import com.gorbuvla.stackreader.di.modules.NewsModule
import com.gorbuvla.stackreader.domain.StackNewsItem
import com.gorbuvla.stackreader.news_detail.DetailFragment
import com.gorbuvla.stackreader.news_list.NewsListFragment
import com.gorbuvla.stackreader.news_list.RequiredParentViewCallback

class MainActivity : AppCompatActivity(), RequiredParentViewCallback {

    companion object {
        @JvmStatic
        private val DETAIL_TAG = "DETAIL_TAG"
        @JvmStatic
        private val NEWS_TAG = "NEWS_TAG"
        @JvmStatic
        private val DETAIL_KEY = "DETAIL_KEY"
        @JvmStatic
        private val NEWS_KEY = "NEWS_KEY"
        @JvmStatic
        private val LANDSCAPE = 2
    }

    private val colors = arrayOf(R.color.m_brown, R.color.m_purple, R.color.m_indigo, R.color.m_teal)


    override lateinit var newsComponent: NewsComponent
        private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        newsComponent = DaggerNewsComponent.builder()
                .appComponent(StackReaderApp.appComponent)
                .newsModule(NewsModule())
                .build()


        if (savedInstanceState != null) {

            val fd = supportFragmentManager.getFragment(savedInstanceState, DETAIL_KEY)

            //restore DetailFragment if present
            fd?.let {

                val tr = supportFragmentManager.beginTransaction()

                if (resources.getBoolean(R.bool.isTablet)) {
                    //recreate fragment due to container id change
                    val refd = DetailFragment()
                    refd.arguments = fd.arguments

                    if (resources.configuration.orientation == LANDSCAPE) {
                        //put detail into separate container
                        supportFragmentManager.popBackStackImmediate()
                        tr.replace(R.id.detail_container, refd, DETAIL_TAG)
                    } else {
                        //put detail into same container as list fragment
                        tr.add(R.id.container, refd, DETAIL_TAG)
                                .addToBackStack(DETAIL_TAG)
                    }.commit()

                } else {
                    tr.add(R.id.container, fd, DETAIL_TAG).addToBackStack(DETAIL_TAG).commit()
                }
            }

        } else {

            val lf = NewsListFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, lf, NEWS_TAG).commit()

            val perm = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (perm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val fn = supportFragmentManager.findFragmentByTag(NEWS_TAG)
        val fd = supportFragmentManager.findFragmentByTag(DETAIL_TAG)

        fn?.let { supportFragmentManager.putFragment(outState, NEWS_KEY, fn) }
        fd?.let { supportFragmentManager.putFragment(outState, DETAIL_KEY, fd) }
    }

    override fun onItemSelected(item: StackNewsItem) {
        val b = Bundle()
        b.putParcelable("NEWS_ITEM", item)
        b.putInt("COLOR", colors[(Math.random() * 4).toInt()])

        val f = DetailFragment()
        f.arguments = b

        //val i = supportFragmentManager.fragments.map { fr -> if (fr == null) 0 else 1 }.reduce({x, y -> x+y})


        val tr = supportFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        if (resources.getBoolean(R.bool.isTablet) && resources.configuration.orientation == LANDSCAPE) {
            tr.replace(R.id.detail_container, f, DETAIL_TAG).commit()
        } else {
            tr.add(R.id.container, f, DETAIL_TAG)
                    .addToBackStack(DETAIL_TAG)
                    .commit()
        }
    }
}
