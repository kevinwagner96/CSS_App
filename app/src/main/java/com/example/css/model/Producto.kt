package com.example.css.model

import android.content.res.Resources
import com.example.css.R
import java.math.BigDecimal
import java.math.RoundingMode

open class Producto {
    var code: String = ""
    var descripcion:String = ""
    var unidad:String= "U"
    var precio_contado:Double = 0.0
    var precio_lista:Double = 0.0
    var unidad_almacenamiento:String = ""
    var ratio_conversion: Double = 0.0
    var departamento: String = ""
    var marca: String = ""



    fun precioContadoPor(n:Double): String {
        return BigDecimal(precio_contado*n).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun precioContado():String{
        return BigDecimal(precio_contado).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

    fun precioListaPor(n:Int): Double {
        return precio_contado*n
    }

    fun updateAtributes(){
        val marcas = arrayListOf<String>(
                "ALBERDI",
                "CORTINES",
                "CERAMICAS LOURDES",
                "Cañuelas",
                "lourdes",
                "SAN PIETRO",
                "SIMOLEX",
                "Tendenza"
            )
        val departamentos =  arrayListOf<String>("ESMALTADOS GENESIS-BALDOS", "Ceramica", "CERAMICA REVESTIMIENTO", "Pisos", "PORCELANATO", "Porcelánico" )

        if(this.ratio_conversion!=0.0) {
            return
        }
        else if(this.marca in marcas|| this.departamento in departamentos){
            this.unidad = "m²  "
            this.ratio_conversion = getRatioConversion()
            this.unidad_almacenamiento = "CAJ "
        }

    }

    private fun getRatioConversion(): Double {
        var m2: Double = 0.0
        var metros = getMetros()
        var number : String = ""

        if(metros != ""){
            for(i in metros.indices){
                if(metros[i].isDigit())
                    number = number+metros[i]
                if(metros[i]==',' || metros[i]=='.')
                    number = number+"."
            }

            m2 = number.toDoubleOrNull()!!
        }

        return m2
    }

    private fun getMetros(): String {
        for( i in this.descripcion.indices){
            var sub = this.descripcion.substring(i,i+2)
            if(isMCuadrado(sub)){
                var metros = this.descripcion.substring(i-5,i)
                return metros
            }
        }
        return  ""
    }

    private fun isMCuadrado(text : String):Boolean{
        if (text == "m²")
            return true
        if (text == "M²")
            return true
        if (text == "M2")
            return true
        if(text == "Â²")
            return true


        return false
    }



}

