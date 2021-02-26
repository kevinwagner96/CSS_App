package com.example. css.ui.home

import android.app.SearchManager
import android.content.Intent
import android.database.MatrixCursor
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SearchView.OnQueryTextListener
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.css.ApiService
import com.example.css.R
import com.example.css.helpers.MyApiServer
import com.example.css.model.*
import com.example.css.service
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    var changeUnity:Double = 1.0
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
        //var actionBar: android.app.ActionBar? = activity?.actionBar
        val switch: Switch = root.findViewById(R.id.switch1)

        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            run {
                IntentIntegrator.forSupportFragment(this).initiateScan();

            }
        }


        //actionBar?.setCustomView(searchView)
        //actionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        //searchView.setQuery("test",true)
        //searchView.focusable = View.FOCUSABLE
        searchView.isIconified = false
        searchView.requestFocusFromTouch()



        MyApiServer.setURL(getString(R.string.server1))
        ServerStatus().execute(  )

        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient())
            .baseUrl(MyApiServer.getURL())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create<ApiService>(ApiService::class.java)


        Handler().postDelayed({ automaticSearch("")}, 3000)


        searchView.setOnQueryTextListener( object : OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("automaticSearch", "Buscado =  "+newText)
                val suggestion = homeViewModel.getSuggestionList()
                val c = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                var i=0
                suggestion.value?.forEach {
                   if( it.text.startsWith(newText.toUpperCase()) && i<100)
                       c.addRow(arrayListOf(i++,it.text))
                }


                val from = arrayOf(BaseColumns._ID,SearchManager.SUGGEST_COLUMN_TEXT_1)
                val cursorAdapter = SimpleCursorAdapter(context, R.layout.item_search, c, arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1), intArrayOf(R.id.item_label), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

                searchView.suggestionsAdapter = cursorAdapter
                cursorAdapter.notifyDataSetChanged()

                return true
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

        homeViewModel.getSuggestionList().observe(this, Observer {
            Log.d("automaticSearch", "Observable")
            var list : Array<String> = Array<String>(it.size){""}
            var ids: IntArray = IntArray(it.size)
            var i = 0

            val c = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
            it?.forEach{
                c.addRow(arrayOf(i++,it.text))
               // Log.d("automaticSearch", "ADD "+it.text)
            }


            val from = arrayOf(BaseColumns._ID,SearchManager.SUGGEST_COLUMN_TEXT_1)
            val cursorAdapter = SimpleCursorAdapter(context, R.layout.item_search, c, arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1), intArrayOf(R.id.item_label), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

            searchView.suggestionsAdapter = cursorAdapter
            cursorAdapter.notifyDataSetChanged()


        })

        listView.setOnItemClickListener {parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            showAddCarDialog(homeViewModel.getProducto(position))

        }



        return root
    }


    private fun showErrorDialog(){

        ServerStatus().execute(  )

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
        producto.updateAtributes()

        val dialog : android.app.AlertDialog
        val builder = android.app.AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.layout_add_to_car,null)
        val scroll = view.findViewById<SeekBar>(R.id.seekBar)
        val cant = view.findViewById<EditText>(R.id.editTextCantidad)
        val desc = view.findViewById<TextView>(R.id.textViewDescripcion)
        val total = view.findViewById<TextView>(R.id.textViewTotal)
        val calcular = view.findViewById<ImageButton>(R.id.btn_calculadora)
        val radioButton1 = view.findViewById<RadioButton>(R.id.radioButton)
        val radioButton2 = view.findViewById<RadioButton>(R.id.radioButton2)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        radioButton1.visibility=View.INVISIBLE
        radioButton2.visibility=View.INVISIBLE

        if(producto.unidad!="m²  " || producto.ratio_conversion==0.0){
            calcular.visibility = View.INVISIBLE
            calcular.layoutParams.width = 0
        }

        if(producto.unidad!="m²  " && producto.unidad_almacenamiento!="" && producto.ratio_conversion!=0.0){
            radioButton1.text = producto.unidad
            radioButton2.text = producto.unidad_almacenamiento
            radioButton1.isChecked = true
            radioButton1.visibility = View.VISIBLE
            radioButton2.visibility = View.VISIBLE
        }else{
            radioGroup.layoutParams.height = 0
        }

        radioButton1.setOnClickListener{
            changeUnity = 1.0
            val cantidad = cant.text.toString().toDouble()
            val decimal = BigDecimal(cantidad * producto.precio_contado*changeUnity).setScale(2, RoundingMode.HALF_EVEN)
            total.setText("Total= $"+decimal.toString())
        }

        radioButton2.setOnClickListener{
            val cantidad = cant.text.toString().toDouble()
            changeUnity = producto.ratio_conversion
            val decimal = BigDecimal(cantidad * producto.precio_contado*changeUnity).setScale(2, RoundingMode.HALF_EVEN)
            total.setText("Total= $"+decimal.toString())

        }

        desc.setText(producto.descripcion)
        cant.setText("1")
        val decimal = BigDecimal(producto.precio_contado).setScale(2, RoundingMode.HALF_EVEN)
        total.setText("Total= $"+decimal.toString())





        scroll.progress = 1
        scroll.max = 20
        scroll.incrementProgressBy(1)
        scroll.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                cant.setText(i.toString())
                val decimal = BigDecimal(i*producto.precio_contado*changeUnity).setScale(2, RoundingMode.HALF_EVEN)
                total.setText("Total= $"+decimal.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        builder.setView(view)
            .setTitle("Agrega un producto")
            .setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
                dialog.dismiss()
            }
            .setPositiveButton("Agregar"){dialog, which ->
                if(("0"+cant.text.toString()).toDouble()>0.0){
                    if(radioButton2.isChecked){
                        producto.unidad = producto.unidad_almacenamiento
                        producto.precio_contado = producto.precio_contado*producto.ratio_conversion
                    }

                    MyFactura.addItem(Item(producto, ("0" + cant.text.toString()).toDouble()))
                    Toast.makeText(this.context, "Agergado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.context, "Ingrese una cantidad", Toast.LENGTH_SHORT).show()
                    showAddCarDialog(producto)
                }

            }


        cant.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(!p0.isNullOrBlank()){
                    val cantidad = ("0"+p0.toString()).toDouble()
                    val decimal = BigDecimal(cantidad * producto.precio_contado*changeUnity).setScale(2, RoundingMode.HALF_EVEN)
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
                Log.d("searchByDescripcion", "kevin")
                Log.d("searchByDescripcion" , posts.toString())
                if(posts?.StatusCode ==200 && !posts.Value.isEmpty())
                    homeViewModel.setProductList(posts?.Value!!)
                else if(posts?.StatusCode ==200){
                    Toast.makeText(context,"No hay resultados",Toast.LENGTH_SHORT).show()
                }
                else
                    showErrorDialog()
            }
            override fun onFailure(call: Call<BusquedaResponse>?, t: Throwable?) {
                Log.d("searchByDescripcion", t?.message)
                showErrorDialog()
                t?.printStackTrace()
            }

        })
    }

    fun automaticSearch(desc:String){
        //Recibimos todos los posts
        service.automaticSearch(desc).enqueue(object: Callback<AutomaticSearchResponse> {
            override fun onResponse(call: Call<AutomaticSearchResponse>?, response: Response<AutomaticSearchResponse>?) {
                var posts = response?.body()
                Log.d("automaticSearch", "kevin")
                Log.d("automaticSearch" , posts.toString())
                if(posts?.StatusCode ==200 && !posts.Value.isEmpty())
                    homeViewModel.setSuggestionList(posts?.Value)

                else if(posts?.StatusCode ==200){
                    Toast.makeText(context,"No hay resultados",Toast.LENGTH_SHORT).show()
                }
                else
                    showErrorDialog()
            }
            override fun onFailure(call: Call<AutomaticSearchResponse>?, t: Throwable?) {
                Log.d("automaticSearch", t?.message)
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
            .connectTimeout(8, TimeUnit.SECONDS)
            .writeTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build();

    }

    class ServerStatus :
        AsyncTask<Void?,Void?, Void?>() {

        var server1_name = "http://192.168.0.7:3000/api/"
        var server2_name = "http://corralon.ddns.net/api/"
        var server3_name = "http://192.168.0.115:3000/api/"

        var server1_status = false
        var server2_status = false
        var server3_status = false



        override fun onPostExecute(result: Void?) {
            Log.d("APISERVER", "Post Execute")

            if (server1_status)
                MyApiServer.setURL(server1_name)
            else if (server2_status)
                MyApiServer.setURL(server2_name)
            else if (server3_status)
                MyApiServer.setURL(server3_name)
            else
                MyApiServer.setURL(server1_name)

            super.onPostExecute(result)

        }

        /**
         * Check if host is reachable.
         * @param host The host to check for availability. Can either be a machine name, such as "google.com",
         * or a textual representation of its IP address, such as "8.8.8.8".
         * @param port The port number.
         * @param timeout The timeout in milliseconds.
         * @return True if the host is reachable. False otherwise.
         */

        fun isHostAvailable(host: String, port: Int, timeout: Int): Boolean {
            return try {
                val myUrl = URL(host)
                val connection: URLConnection = myUrl.openConnection()
                connection.setConnectTimeout(timeout)
                connection.connect()
                Log.d("APISERVER", "SERVER : "+host + " -> TRUE")
                true
            } catch (e: Exception) {
                Log.d("APISERVER", "SERVER : "+host + " -> FALSE")
                false
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            server1_status = isHostAvailable(server1_name, 80, 1000)
            server2_status = isHostAvailable(server2_name, 80, 1000)
            server3_status = isHostAvailable(server3_name, 80, 1000)
            return null
        }
    }
}

