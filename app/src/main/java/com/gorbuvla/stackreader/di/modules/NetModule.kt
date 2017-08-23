package com.gorbuvla.stackreader.di.modules

import com.gorbuvla.stackreader.service.StackAPI
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Created by vlad on 21/08/2017.
 */
@Module
class NetModule {

    private val apiVersion = 2.2

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://api.stackexchange.com/$apiVersion/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideStackApi(retrofit: Retrofit): StackAPI {
        return retrofit.create(StackAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMoshi() : Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }
}