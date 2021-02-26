package com.example.css.helpers

import android.util.Log
import com.example.css.model.Factura
import com.example.css.model.MyFactura
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File

object JsonIO {
    fun writeJSONtoFile(s:String) {

        //Create a Object of Gson
        var gson = Gson()
        //Convert the Json object to JsonString
        var jsonString:String = gson.toJson(MyFactura)
        //Initialize the File Writer and write into file
        val file= File(s)
        file.writeText(jsonString)
    }

    fun readJSONfromFile(f:String) {
        if(MyFactura.items.size > 0){
            return;
        }
        //Creating a new Gson object to read data
        var gson = Gson()
        //Read the PostJSON.json file
        val bufferedReader: BufferedReader = File(f).bufferedReader()
        // Read the text from buffferReader and store in String variable
        val inputString = bufferedReader.use { it.readText() }

        //Convert the Json File to Gson Object
        val factura = gson.fromJson(inputString, Factura::class.java)
        //Initialize the String Builder
        factura.items.forEach{
            MyFactura.addItem(it)
        }
    }
}