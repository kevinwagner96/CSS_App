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

    /*
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private  val _array = MutableLiveData<ArrayList<String>>().apply {
        value =   arrayListOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")
    }



    val text: LiveData<String> = _text

    var prices: LiveData<ArrayList<String>> = _array

    fun setData(list :ArrayList<String>){
        val data =MutableLiveData<ArrayList<String>>().apply {
            value = list
        }
        prices = data
        _array.
    }
*/



}