package com.example.css.ui.gallery

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.example.css.R
import com.example.css.helpers.Compartir
import java.io.File
import java.io.FileOutputStream
import java.util.*
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

            Compartir.bitmap(this.requireContext(),getWholeListViewItemsToBitmap(listaFactura))
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
/*
    private fun saveToInternalStorage(bitmapImage: Bitmap): String? {


        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        //val storageDir = getExternalFileDir(Environment.DIRECTORY_PICTURES)
        // Create imageDir

        val mypath = File(directory, "" +
                "profile.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Toast.makeText(context,mypath.absolutePath, Toast.LENGTH_LONG).show()

        return "hola"
    }
*/
    private fun saveImage(finalBitmap: Bitmap) {

        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/capture_photo")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val OutletFname = "Image-$n.jpg"
        val file = File(myDir, OutletFname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            imagePath = file.absolutePath
            out.flush()
            out.close()


        } catch (e: Exception) {
            e.printStackTrace()

        }

    }
}