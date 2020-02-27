package com.example.css.helpers

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.io.File

object Compartir {
    fun bitmap(context:Context, bit: Bitmap?){
        val cw = ContextWrapper(context)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="image/jpg"
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, "profile.jpg")
        Toast.makeText(context,mypath.absolutePath,Toast.LENGTH_LONG).show()

        shareIntent.putExtra(Intent.EXTRA_STREAM, bit)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(context,Intent.createChooser(shareIntent,"Presupuesto"),null)

    }
}