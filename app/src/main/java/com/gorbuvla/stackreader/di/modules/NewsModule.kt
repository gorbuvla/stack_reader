package com.gorbuvla.stackreader.di.modules

import android.content.Context
import com.gorbuvla.stackreader.di.scopes.ActivityScope
import com.gorbuvla.stackreader.news_list.NewsAdapter
import com.gorbuvla.stackreader.service.StorageService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

/**
 * Created by vlad on 21/08/2017.
 */
@Module
class NewsModule {


    @Provides
    @ActivityScope
    fun provideAdapter(): NewsAdapter {
        return NewsAdapter()
    }

    @Provides
    @ActivityScope
    fun provideStorageService(context: Context, moshi: Moshi) : StorageService {
        return StorageService(context, moshi)
    }

}
