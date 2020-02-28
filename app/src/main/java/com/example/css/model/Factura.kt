package com.example.css.model

import java.math.BigDecimal
import java.math.RoundingMode

object Factura {

    val items:ArrayList<Item> = ArrayList<Item>()
    private var total:Double = 0.0

    fun addItem(item:Item){
        items.add(item)
    }

    private fun calcularTotal(){
        total =0.0
        items.forEach {
            total += it.total
        }
    }

    fun getTotal():String{
        calcularTotal()
        return BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN).toString()
    }

}