package com.example.css.ui.home

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.css.ApiService
import com.example.css.R
import com.example.css.model.*
import com.example.css.ui.gallery.GalleryViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_gallery.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel


    val TAG_LOGS = "kikopalomares"

    lateinit var service: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val listView: ListView = root.findViewById(R.id.priceList)
        val searchView: SearchView = root.findViewById(R.id.searchView)
        var actionBar: android.app.ActionBar? = activity?.actionBar
        val switch: Switch = root.findViewById(R.id.switch1)

        actionBar?.setCustomView(searchView)
        actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        //searchView.setQuery("test",true)
        //searchView.focusable = View.FOCUSABLE
        searchView.isIconified = false
        searchView.requestFocusFromTouch()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(R.string.api_url.toString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create<ApiService>(ApiService::class.java)


        searchView.setOnQueryTextListener( object : OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i(TAG_LOGS, query )
                if(!switch.isChecked)
                    searchByDescripcion(query);
                else {
                    if(query.isDigitsOnly())
                        getProductoById(query.toInt());
                    else
                        Toast.makeText(root.context,"Solo pueden ser numeros :(",Toast.LENGTH_SHORT).show()
                }

                return false
            }
        }
        )



        homeViewModel.getProductosList().observe(this, Observer {
            var list : ArrayList<String> = ArrayList()
            it?.forEach{
                list.add(it.descripcion)
            }

            val arrayAdapter = ArrayAdapter(root.context,
                    android.R.layout.simple_list_item_1, list)
                    listView.adapter = arrayAdapter
        })

        listView.setOnItemClickListener {parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            showFilterDialog(homeViewModel.getProducto(position))

        }



        return root
    }



    fun showFilterDialog(producto: Producto){
        val builder = android.app.AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.layout_add_to_car,null)
        val scroll = view.findViewById<SeekBar>(R.id.seekBar)
        val cant = view.findViewById<EditText>(R.id.editTextCantidad)
        val desc = view.findViewById<TextView>(R.id.textViewDescripcion)
        val total = view.findViewById<TextView>(R.id.textViewTotal)

        desc.setText(producto.descripcion)
        cant.setText("1")
        val decimal = BigDecimal(producto.precioContado).setScale(2, RoundingMode.HALF_EVEN)
        total.setText("Total= $"+decimal.toString())

        scroll.progress = 1
        scroll.max = 20
        scroll.incrementProgressBy(1)
        scroll.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                cant.setText(i.toString())
                val decimal = BigDecimal(i*producto.precioContado).setScale(2, RoundingMode.HALF_EVEN)
                total.setText("Total= $"+decimal.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        builder.setView(view)
            .setTitle("Agrega un producto")
            .setNeutralButton("Cancel"){_,_ ->
                //
            }
            .setPositiveButton("Agregar"){dialog, which ->
                if(("0"+cant.text.toString()).toDouble()>0.0){
                    Factura.addItem(Item(producto, ("0" + cant.text.toString()).toDouble()))
                    Toast.makeText(this.context, "Agergado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.context, "Ingrese una cantidad", Toast.LENGTH_SHORT).show()
                    showFilterDialog(producto)
                }

            }

        cant.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(!p0.isNullOrBlank()){
                    var cantidad = ("0"+p0.toString()).toDouble()
                    val decimal = BigDecimal(cantidad * producto.precioContado).setScale(2, RoundingMode.HALF_EVEN)
                    total.setText("Total= $"+decimal.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
        )


        builder.create().show()
    }

    fun searchByDescripcion(desc:String){
        //Recibimos todos los posts
        service.searchByDescripcion(desc).enqueue(object: Callback<BusquedaResponse> {
            override fun onResponse(call: Call<BusquedaResponse>?, response: Response<BusquedaResponse>?) {
                var posts = response?.body()
                Log.i(TAG_LOGS, Gson().toJson(posts))
                /*
                var list : ArrayList<String> = ArrayList()
                posts?.Value?.forEach{
                    list.add(it.descripcion)
                }
                */
                homeViewModel.setProductList(posts?.Value!!)

            }
            override fun onFailure(call: Call<BusquedaResponse>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun getProductoById(code:Int){
        //Recibimos los datos del post con ID = 1

        var post: ProductoResponse? = null
        service.getPostById(code).enqueue(object: Callback<ProductoResponse>{
            override fun onResponse(call: Call<ProductoResponse>?, response: Response<ProductoResponse>?) {
                post = response?.body()
                if(post?.Value!=null)
                    showFilterDialog(post?.Value!!)
                else
                    Toast.makeText(context,"El producto no existe :(",Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<ProductoResponse>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }
/*
    fun editPost(){
        var post: Producto? = Producto(1, 1, "Hello k", "body")
        //Editamos los datos por POST
        service.editPostById(1, post).enqueue(object: Callback<Producto>{
            override fun onResponse(call: Call<Post>?, response: Response<Post>?) {
                post = response?.body()
                Log.i(TAG_LOGS, Gson().toJson(post))
            }
            override fun onFailure(call: Call<Post>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }
    */

}