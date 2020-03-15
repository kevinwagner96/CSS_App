package com.example.css.model

import org.jetbrains.annotations.NotNull
import java.math.BigDecimal
import java.math.RoundingMode

open class Producto {
    var code: String = ""
    var descripcion:String = ""
    var unidad:String= "U"
    var precioContado:Double = 0.0
    var precioLista:Double = 0.0
    var punidadAlmacenamiento:String = ""
    var ratioConversion: Double = 0.0
    var departamento: String = ""
    var marca: String = ""

    fun precioContadoPor(n:Double): String {
        return BigDecimal(precioContado*n).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun precioContado():String{
        return BigDecimal(precioContado).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun precioListaPor(n:Int): Double {
        return precioContado*n
    }
}
