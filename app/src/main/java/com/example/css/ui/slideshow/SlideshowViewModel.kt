package com.example.css.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.css.model.Factura
import com.example.css.model.MyFactura
import com.example.css.model.MyProducto
import com.example.css.model.Producto

class SlideshowViewModel : ViewModel() {

    private var productoLiveData: MutableLiveData<Producto> = MutableLiveData()

    fun getProducto():LiveData<Producto> {
        productoLiveData.value  = MyProducto.producto
        return productoLiveData
    }

}