package com.gorbuvla.stackreader.di.components

import android.app.Application
import android.content.Context
import com.gorbuvla.stackreader.di.modules.AppModule
import com.gorbuvla.stackreader.di.modules.NetModule
import com.gorbuvla.stackreader.service.StackAPI
import com.squareup.moshi.Moshi
import dagger.Component
import javax.inject.Singleton

/**
 * Created by vlad on 21/08/2017.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface AppComponent {
    fun stackApi() : StackAPI
    fun inject(a: Application)
    fun moshi() : Moshi
    fun context() : Context


}