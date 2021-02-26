package com.example.css


import com.example.css.model.AutomaticSearchResponse
import com.example.css.model.BusquedaResponse
import com.example.css.model.Producto
import com.example.css.model.ProductoResponse
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Project project
 * Created by Francisco Palomares Barrios on 07/06/2018.
 */
interface ApiService {

    @GET("product/")
    fun getAllPosts(): Call<List<Producto>>

    @GET("product")
    fun searchByDescripcion(@Query("text", encoded=true) desc: String): Call<BusquedaResponse>

    @GET("search")
    fun automaticSearch(@Query("text", encoded=true) desc: String): Call<AutomaticSearchResponse>

    @GET("product/{id}")
    fun getById(@Path("id",encoded=true) code: String): Call<ProductoResponse>

    @GET("barcode/")
    fun getByBarcode(@Query("text",encoded = true) barcode: String): Call<ProductoResponse>



}

lateinit var service: ApiService