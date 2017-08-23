package com.gorbuvla.stackreader.di.modules

import android.app.Application
import android.content.Context
import com.gorbuvla.stackreader.StackReaderApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by vlad on 21/08/2017.
 */
@Module
class AppModule(val application: StackReaderApp) {

    @Provides
    @Singleton
    fun provideContext() : Context = application

    @Provides
    @Singleton
    fun provideApplication() : Application = application
}