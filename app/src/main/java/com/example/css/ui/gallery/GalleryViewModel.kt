package com.example.css.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.css.model.Factura
import com.example.css.model.MyFactura

class GalleryViewModel : ViewModel() {

    private var facturaLiveData: MutableLiveData<Factura> = MutableLiveData()

    fun getFactura():LiveData<Factura> {
        facturaLiveData.value  = MyFactura
        return facturaLiveData
    }


}