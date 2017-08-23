package com.gorbuvla.stackreader

import android.app.Application
import com.gorbuvla.stackreader.di.components.AppComponent
import com.gorbuvla.stackreader.di.components.DaggerAppComponent
import com.gorbuvla.stackreader.di.modules.AppModule
import com.gorbuvla.stackreader.di.modules.NetModule

/**
 * Created by vlad on 20/08/2017.
 */
class StackReaderApp : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule())
                .build()
    }
}