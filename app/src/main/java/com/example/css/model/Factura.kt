package com.example.css.model

import java.math.BigDecimal
import java.math.RoundingMode

open class Factura {

    val items:ArrayList<Item> = ArrayList<Item>()
    private var total:Double = 0.0

    fun addItem(item: Item){
        items.add(item)
    }

    fun removeLastItem(){
        items.removeAt(items.size-1)
    }

    fun removeAll(){
        items.clear()
        calcularTotal()
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

    fun removeItem(position:Int){
        items.removeAt(position);
    }

}

object MyFactura : Factura()