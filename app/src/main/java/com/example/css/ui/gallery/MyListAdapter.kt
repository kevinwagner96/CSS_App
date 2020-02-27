package com.example.css.ui.gallery

import com.example.css.R
import com.example.css.model.Item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MyListAdapter(var mCtx:Context , var resource:Int,var items:List<Item>) :ArrayAdapter<Item>( mCtx , resource , items ){


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater :LayoutInflater = LayoutInflater.from(mCtx)

        val view : View = layoutInflater.inflate(resource , null )
        var textView : TextView = view.findViewById(R.id.titleTv)
        var textView1 : TextView = view.findViewById(R.id.descTv)
        var textCode : TextView = view.findViewById(R.id.codeText)


        var item : Item = items[position]

        textView.text = item.producto.descripcion

        textCode.text = item.producto.code

        if(item.cantidad == 1.0)
            textView1.text = item.cantidad.toString()+item.getUnidad()+" = $"+item.getTotal()
        else
            textView1.text = item.cantidad.toString()+item.getUnidad()+" x $"+item.producto.precioContado()+" = $"+item.getTotal()


        return view
    }

}