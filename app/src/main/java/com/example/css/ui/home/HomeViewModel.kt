package com.example.css.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.css.model.Producto

class HomeViewModel : ViewModel() {

    private var productosLiveData: MutableLiveData<List<Producto>> = MutableLiveData()
    private  var productosList: List<Producto> = ArrayList()

    fun getProductosList():LiveData<List<Producto>> {
        return productosLiveData
    }


    fun setProductList(array:List<Producto>){
        productosList = array
        productosLiveData.value = productosList
    }

    fun getProducto(i:Int):Producto {
        return productosList.get(i)
    }





}