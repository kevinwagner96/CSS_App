package com.example.css.ui.gallery

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.css.R
import com.example.css.helpers.Compartir
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    internal var imagePath: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_total_factura)
        val listaFactura:ListView = root.findViewById(R.id.facturaList)
        val button:Button = root.findViewById(R.id.button)

        galleryViewModel.getFactura().observe(this, Observer {

            textView.text = "Total = $"+it.getTotal()

            var list : ArrayList<String> = ArrayList()
            it.items?.forEach{
                list.add(it.producto.descripcion)
                Log.i("KEVIN-Agrega",it.producto.descripcion)
            }

            val arrayAdapter = MyListAdapter(root.context, R.layout.row, it.items)

            listaFactura.adapter = arrayAdapter
        })

        button.setOnClickListener {
           val path = getWholeListViewItemsToBitmap(facturaList)?.let { it1 -> saveImageToStorage(it1) }
            Compartir.bitmap(this.requireContext(), path.toString())
        }

        return root
    }

    fun getWholeListViewItemsToBitmap(listView: ListView): Bitmap? {
        val listview: ListView = listView
        val adapter: ListAdapter = listview.adapter
        val itemscount: Int = adapter.getCount()
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
        val bigcanvas = Canvas(bigbitmap)
        val paint = Paint()
        var iHeight:Float = 0F
        bigcanvas.drawColor(Color.WHITE);
        for (i in bmps.indices) {
            var bmp: Bitmap = bmps[i]

            bigcanvas.drawBitmap(bmp, 0F, iHeight, null)
            iHeight += bmp!!.height
            bmp.recycle()
        }
        return bigbitmap
    }

    fun getPermission(){
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),100)

        }
    }

    fun saveImageToStorage(bitmap: Bitmap):String{
        getPermission()

        var externalStorageStats= Environment.getExternalStorageState()
        if(externalStorageStats.equals(Environment.MEDIA_MOUNTED)){
            val storageDirectory = Environment.getExternalStorageDirectory().toString()
            val file = File(storageDirectory,"test_image.jpg")
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
            Toast.makeText(context,"No se puede acceder a almacenamiento",Toast.LENGTH_SHORT).show()
        }

        return ""
    }
}