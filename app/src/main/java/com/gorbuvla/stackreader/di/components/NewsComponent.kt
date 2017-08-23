package com.gorbuvla.stackreader.di.components


import com.gorbuvla.stackreader.di.modules.NewsModule
import com.gorbuvla.stackreader.di.scopes.ActivityScope
import com.gorbuvla.stackreader.news_list.NewsListFragment
import com.gorbuvla.stackreader.news_list.NewsListPresenter
import com.gorbuvla.stackreader.service.StorageService
import dagger.Component

/**
 * Created by vlad on 21/08/2017.
 */
@ActivityScope
@Component(modules = arrayOf(NewsModule::class), dependencies = arrayOf(AppComponent::class))
interface NewsComponent {
    fun inject(view: NewsListFragment)
    fun presenter() : NewsListPresenter
    fun storageService() : StorageService
}