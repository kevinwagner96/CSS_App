package com.example.css.ui.gallery


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.css.R
import com.example.css.helpers.Compartir
import com.example.css.model.Item
import com.example.css.model.MyFactura
import com.example.css.model.Producto
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.zip.Inflater
import kotlin.collections.ArrayList


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val totalTextView: TextView = root.findViewById(R.id.text_total_factura)
        val listaFactura:ListView = root.findViewById(R.id.facturaList)
        val compartirButton:FloatingActionButton = root.findViewById(R.id.share_fab)

        val imm  =  context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(root.windowToken, 0)

        listaFactura.setOnItemClickListener { parent, view, position, id ->
            val arrayAdapter = MyListAdapter(this.requireContext(), R.layout.row, MyFactura.items)
            val element =  arrayAdapter.getItem(position) as Item


            val builder = AlertDialog.Builder(this.activity)
            builder.setTitle("Borrar")
                .setMessage(element.getName())
                .setCancelable(false)
                .setPositiveButton("Si") { dialog, id ->
                    MyFactura.removeItem(position)
                    updateFactura()
                }
                .setNegativeButton("No") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()


        }

        galleryViewModel.getFactura().observe(this, Observer { factura ->
            updateFactura()
        })

        compartirButton.setOnClickListener {
            var total = Producto()
            total.descripcion = ""
            total.unidad = "TOTAL: "
            total.precio_contado = MyFactura.getTotal().toDouble()
            MyFactura.addItem(Item(total,1.0))

            val bitmap = listViewToBitmap(facturaList)
            if(bitmap!=null) {
                val capturePath = saveImageToStorage(bitmap)
                Compartir.bitmap(this.requireContext(), capturePath)
            }else{
                Toast.makeText(context,"Lista vacia",Toast.LENGTH_SHORT).show()
            }

            MyFactura.removeLastItem()
        }


        return root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden){
            val share = this.activity?.findViewById<FloatingActionButton>(R.id.share_fab)
            share?.show()
        }



        super.onHiddenChanged(hidden)
    }

    private fun listViewToBitmap(listview: ListView): Bitmap? {
        if(listview.isEmpty())
            return null

        val adapter: ListAdapter = listview.adapter
        val itemscount: Int = adapter.count
        var allitemsheight = 0
        val bmps: MutableList<Bitmap> = ArrayList()
        for (i in 0 until itemscount) {
            val childView: View = adapter.getView(i, null, listview)
            childView.measure(
                MeasureSpec.makeMeasureSpec(listview.width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            childView.layout(0, 0, childView.measuredWidth, childView.measuredHeight)
            childView.isDrawingCacheEnabled = true
            childView.buildDrawingCache()
            bmps.add(childView.drawingCache)
            allitemsheight += childView.measuredHeight
        }
        val bigbitmap = Bitmap.createBitmap(
            listview.measuredWidth,
            allitemsheight,
            Bitmap.Config.ARGB_8888
        )
        val bigcanvas: Canvas = Canvas(bigbitmap)
        var iHeight = 0F
        bigcanvas.drawColor(Color.WHITE)
        for (i in bmps.indices) {
            val bmp: Bitmap = bmps[i]

            bigcanvas.drawBitmap(bmp, 0F, iHeight, null)
            iHeight += bmp.height
            bmp.recycle()
        }
        return bigbitmap
    }

     fun updateFactura(){

        val totalTextView = this.activity?.findViewById<TextView>(R.id.text_total_factura)
        val listaFactura = this.activity?.findViewById<ListView>(R.id.facturaList)

        totalTextView?.text = "Total = $"+MyFactura.getTotal()

        val arrayAdapter = MyListAdapter(this.requireContext(), R.layout.row, MyFactura.items)

        listaFactura?.adapter = arrayAdapter
    }

    private fun getPermission(){
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),100)

        }
    }

    private fun saveImageToStorage(bitmap: Bitmap):String{

        getPermission()

        val externalStorageStats = Environment.getExternalStorageState()
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if(externalStorageStats == Environment.MEDIA_MOUNTED){
            val file = File(storageDirectory,"presupuesto.PNG")
            try{
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                stream.flush()
                stream.close()
                Toast.makeText(context,"Path:"+ Uri.parse(file.absolutePath),Toast.LENGTH_SHORT).show()
            }catch (e : Exception){
                e.printStackTrace()
            }
            return file.absolutePath

        }else{
            Toast.makeText(context,R.string.toast_storage_problem,Toast.LENGTH_SHORT).show()
        }

        return ""
    }

    class BorrarProducto : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Eliminar del carro")
                    .setPositiveButton("SI",
                        DialogInterface.OnClickListener { dialog, id ->
                            // FIRE ZE MISSILES!
                        })
                    .setNegativeButton("NO",
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}