package com.example.css.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.css.model.Producto
import com.example.css.model.WordsSearch

class HomeViewModel : ViewModel() {

    private var productosLiveData: MutableLiveData<List<Producto>> = MutableLiveData()
    private  var productosList: List<Producto> = ArrayList()

    private var suggestionLiveData: MutableLiveData<List<WordsSearch>> = MutableLiveData()
    private  var suggestionList: List<WordsSearch> = ArrayList()

    fun getProductosList():LiveData<List<Producto>> {
        return productosLiveData
    }

    fun getSuggestionList():LiveData<List<WordsSearch>> {
        return suggestionLiveData
    }

    fun setSuggestionList(array:List<WordsSearch>){
        suggestionList = array

        suggestionLiveData.value = suggestionList
    }

    fun setProductList(array:List<Producto>){
        productosList = array
        for(p in productosList.listIterator()){
            p.updateAtributes()
        }
        productosLiveData.value = productosList
    }

    fun getProducto(i:Int):Producto {
        return productosList.get(i)
    }





}