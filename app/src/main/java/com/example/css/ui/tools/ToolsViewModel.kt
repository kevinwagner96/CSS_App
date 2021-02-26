package com.example.css.ui.tools

import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.css.R
import java.net.InetAddress
import androidx.fragment.app.Fragment

class ToolsViewModel : ViewModel() {




    private val _text = MutableLiveData<String>().apply {
        value = "Servidores disponibles"
    }
    private val _server1 = MutableLiveData<String>().apply {
        value = "Servidor 1"
    }
    private val _server2 = MutableLiveData<String>().apply {
        value = "Servidor 2"
    }
    private val _server3 = MutableLiveData<String>().apply {
        value = "Servidor 3"
    }
    val text: LiveData<String> = _text
    val server1: LiveData<String> = _server1
    val server2: LiveData<String> = _server2
    val server3: LiveData<String> = _server3
}