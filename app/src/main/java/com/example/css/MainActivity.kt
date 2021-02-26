package com.example.css

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import com.example.css.helpers.JsonIO
import com.example.css.model.MyFactura
import com.example.css.ui.gallery.GalleryFragment
import com.example.css.ui.gallery.MyListAdapter
import com.example.css.ui.home.HomeFragment
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(item.itemId == R.id.action_delete)
            MyFactura.removeAll()

        updateFactura()

        return super.onOptionsItemSelected(item)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            var result: IntentResult? =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if (result != null) {
                val textBuscado: SearchView = findViewById(R.id.searchView)
                if (result.contents != null) {
                    textBuscado.queryHint = result.contents

                } else {
                    textBuscado.queryHint = "Fallo Scan"
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }catch (e:Exception){
            return
        }
    }

    override fun onStop() {
        val fileName = cacheDir.absolutePath+"/PostJson.json"
        JsonIO.writeJSONtoFile(fileName)
        super.onStop()
    }

    override fun onStart() {
        val fileName = cacheDir.absolutePath+"/PostJson.json"
        try {
            JsonIO.readJSONfromFile(fileName)
        }catch (e:Exception){
            e.printStackTrace()
        }
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun updateFactura(){

        val totalTextView = this.findViewById<TextView>(R.id.text_total_factura)
        val listaFactura = this.findViewById<ListView>(R.id.facturaList)

        totalTextView?.text = "Total = $"+MyFactura.getTotal()

        val arrayAdapter = MyListAdapter(this, R.layout.row, MyFactura.items)

        listaFactura?.adapter = arrayAdapter
    }
}
