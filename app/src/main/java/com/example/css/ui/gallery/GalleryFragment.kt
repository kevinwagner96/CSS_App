package com.example.css.ui.gallery


import android.annotation.SuppressLint
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
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.css.R
import com.example.css.helpers.Compartir
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


        galleryViewModel.getFactura().observe(this, Observer { factura ->

            totalTextView.text = "Total = $"+factura.getTotal()

            val list : ArrayList<String> = ArrayList()
            factura.items.forEach{
                list.add(it.producto.descripcion)
                Log.i("KEVIN-Agrega",it.producto.descripcion)
            }

            val arrayAdapter = MyListAdapter(root.context, R.layout.row, factura.items)

            listaFactura.adapter = arrayAdapter
        })

        compartirButton.setOnClickListener {
            val bitmap = listViewToBitmap(facturaList)
            if(bitmap!=null) {
                val capturePath = saveImageToStorage(bitmap)
                Compartir.bitmap(this.requireContext(), capturePath)
            }else{
                Toast.makeText(context,"Lista vacia",Toast.LENGTH_SHORT).show()
            }
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

    private fun getPermission(){
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),100)

        }
    }

    private fun saveImageToStorage(bitmap: Bitmap):String{

        getPermission()

        val externalStorageStats = Environment.getExternalStorageState()
        if(externalStorageStats == Environment.MEDIA_MOUNTED){
            val storageDirectory = context?.getExternalFilesDir(null)?.absolutePath
            val file = File(storageDirectory,R.string.image_name.toString())
            try{
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
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
}