package com.example.css


import com.example.css.model.BusquedaResponse
import com.example.css.model.Producto
import com.example.css.model.ProductoResponse
import retrofit2.Call
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

    @GET("producto/{id}")
    fun getPostById(@Path("id") id: Int): Call<ProductoResponse>

    @POST("producto/{id}")
    fun editPostById(@Path("id") id: Int, @Body post: Producto?): Call<Producto>
}