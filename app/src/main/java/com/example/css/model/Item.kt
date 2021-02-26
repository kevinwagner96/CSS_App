package com.example.css.model

import java.math.BigDecimal
import java.math.RoundingMode

class Item {
    val producto:Producto
    val cantidad:Double
    val total:Double

    constructor(producto: Producto , cant:Double){
        this.producto = producto
        cantidad = cant
        total = producto.precio_contado * cant
    }

    fun getTotal():String{
        return BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun getName():String{
        return producto.descripcion
    }

    fun getUnidad():String{
        if(producto.unidad.isNullOrBlank())
            return "U"
        else
            return  producto.unidad
    }
}
