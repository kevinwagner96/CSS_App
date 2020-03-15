package com.example.css


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

    @GET("producto/")
    fun getAllPosts(): Call<List<Producto>>

    @GET("producto")
    fun searchByDescripcion(@Query("buscado", encoded=true) desc: String): Call<BusquedaResponse>

    @GET("producto")
    fun getById(@Query("code",encoded=true) code: String): Call<ProductoResponse>

    @GET("producto")
    fun getByBarcode(@Query("barcode",encoded = true) barcode: String): Call<ProductoResponse>



}