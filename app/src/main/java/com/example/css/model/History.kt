package com.example.css.model

object MyHistory:History()

open class History {
    val list:ArrayList<InfoProducto> = ArrayList()

    fun touch(producto: Producto){
        list.forEach{
            if(it.producto.code==producto.code) {
                it.touch()
                return
            }
        }

        list.add(InfoProducto(producto,1))
    }

    fun getTopProductos() = list.sortedWith(compareBy {it.getTouches()})

}

class InfoProducto {
    val producto:Producto
    private var touches:Int = 0

    constructor (producto: Producto,touches:Int){
        this.producto=producto
        this.touches=touches
    }

    fun touch(){
        touches++
    }

    fun getTouches():Int{
        return touches
    }
}