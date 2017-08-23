package com.gorbuvla.stackreader.service

import com.gorbuvla.stackreader.domain.StackResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by vlad on 20/08/2017.
 */

interface StackAPI {

    @GET("/questions")
    fun getQuestions(@Query("filter") filter: String = "withbody",
                     @Query("order") order: String = "desc",
                     @Query("sort") sort: String = "creation",
                     @Query("site") site: String = "cooking",
                     @Query("fromdate") fromTimestamp: String = "",
                     @Query("todate") toTimestamp: String = "",
                     @Query("pagesize") pagesize: Int = 20,
                     @Query("page") page: Int = 1
    ) : Observable<Response<StackResponse>>
}