package com.example.css.helpers

import android.util.Log
import com.example.css.ApiService
import com.example.css.model.Item
import com.example.css.service
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class ApiServer {
    private var active_URL:String = ""

    fun setURL(url: String){
        active_URL = url
        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient())
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("APISERVER","Server cambio por "+url)
        service = retrofit.create<ApiService>(ApiService::class.java)


    }

    fun getURL():String{
        return active_URL
    }

    final fun okHttpClient(): OkHttpClient {
        return OkHttpClient . Builder ()
            .connectTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build();

    }
}

object MyApiServer : ApiServer()