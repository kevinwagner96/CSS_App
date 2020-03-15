package com.example. css.ui.home

import android.app.Dialog
import android.content.Intent
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.css.ApiService
import com.example.css.MainActivity
import com.example.css.R
import com.example.css.model.*
import com.example.css.ui.gallery.GalleryViewModel
import com.example.css.ui.slideshow.SlideshowFragment
import com.example.css.ui.slideshow.SlideshowViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_gallery.*
import retrofit2.Call
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_add_to_car.*
import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
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

        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            run {
                IntentIntegrator.forSupportFragment(this).initiateScan();

            }
        }

        actionBar?.setCustomView(searchView)
        actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        //searchView.setQuery("test",true)
        //searchView.focusable = View.FOCUSABLE
        searchView.isIconified = false
        searchView.requestFocusFromTouch()


        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient())
            .baseUrl("http://192.168.0.6:45455/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create<ApiService>(ApiService::class.java)


        searchView.setOnQueryTextListener( object : OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

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
            showAddCarDialog(homeViewModel.getProducto(position))

        }



        return root
    }


    private fun showErrorDialog(){
        val builder = android.app.AlertDialog.Builder(activity)


            builder.setTitle("Error de conexion")
            .setNeutralButton("Cancel"){_,_ -> }
                .setMessage("Lo sentimos :(")

        builder.create().show()
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent) {
        var result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){
            if(result.contents != null){
                getProductoByBarcode(result.contents);
            } else {
                Toast.makeText(this.context, "Error de lectura", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showAddCarDialog(producto: Producto){
        val dialog : android.app.AlertDialog
        val builder = android.app.AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.layout_add_to_car,null)
        val scroll = view.findViewById<SeekBar>(R.id.seekBar)
        val cant = view.findViewById<EditText>(R.id.editTextCantidad)
        val desc = view.findViewById<TextView>(R.id.textViewDescripcion)
        val total = view.findViewById<TextView>(R.id.textViewTotal)
        val calcular = view.findViewById<ImageButton>(R.id.btn_calculadora)


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
                    MyFactura.addItem(Item(producto, ("0" + cant.text.toString()).toDouble()))
                    Toast.makeText(this.context, "Agergado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.context, "Ingrese una cantidad", Toast.LENGTH_SHORT).show()
                    showAddCarDialog(producto)
                }

            }
            .setNegativeButton("Edit"){dialog, which ->
                MyProducto.set(producto)
                val frag : FragmentManager = activity!!.supportFragmentManager
                frag.beginTransaction().add(R.layout.fragment_slideshow,SlideshowFragment()).commit()
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
        dialog = builder.create()

        calcular.setOnClickListener{
            MyProducto.set(producto)
            nav_host_fragment.findNavController().navigate(R.id.nav_slideshow)
            dialog.cancel()
        }

        dialog.show()
    }

    fun searchByDescripcion(desc:String){
        //Recibimos todos los posts
        service.searchByDescripcion(desc).enqueue(object: Callback<BusquedaResponse> {
            override fun onResponse(call: Call<BusquedaResponse>?, response: Response<BusquedaResponse>?) {
                var posts = response?.body()
                if(posts?.StatusCode ==200 && !posts.Value.isEmpty())
                    homeViewModel.setProductList(posts?.Value!!)
                else if(posts?.StatusCode ==200){
                    Toast.makeText(context,"No hay resultados",Toast.LENGTH_SHORT).show()
                }
                else
                    showErrorDialog()
            }
            override fun onFailure(call: Call<BusquedaResponse>?, t: Throwable?) {
                showErrorDialog()
                t?.printStackTrace()
            }

        })
    }

    fun getProductoByBarcode(barcode:String){
        var post: ProductoResponse? = null
        service.getByBarcode(barcode).enqueue(object: Callback<ProductoResponse>{
            override fun onResponse(call: Call<ProductoResponse>?, response: Response<ProductoResponse>?) {
                post = response?.body()
                if(post?.StatusCode ==200 && post!!.Value!= null)
                    showAddCarDialog(post?.Value!!)
                else if (post?.StatusCode ==200)
                    Toast.makeText(context,"El producto no existe :(",Toast.LENGTH_SHORT).show()
                else
                    showErrorDialog()
            }
            override fun onFailure(call: Call<ProductoResponse>?, t: Throwable?) {
                showErrorDialog()
                t?.printStackTrace()
            }
        })
    }

    fun getProductoById(code:Int){
        //Recibimos los datos del post con ID = 1

        var post: ProductoResponse? = null
        service.getById(code.toString()).enqueue(object: Callback<ProductoResponse>{
            override fun onResponse(call: Call<ProductoResponse>?, response: Response<ProductoResponse>?) {
                post = response?.body()
                if(post?.StatusCode ==200 && post!!.Value!= null)
                    showAddCarDialog(post?.Value!!)
                else if (post?.StatusCode ==200)
                    Toast.makeText(context,"El producto no existe :(",Toast.LENGTH_SHORT).show()
                else
                    showErrorDialog()
            }
            override fun onFailure(call: Call<ProductoResponse>?, t: Throwable?) {
                showErrorDialog()
                t?.printStackTrace()
            }
        })
    }

    final fun okHttpClient():OkHttpClient {
       return OkHttpClient . Builder ()
            .connectTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build();

    }
}

