package com.example.css.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import java.lang.Exception

object Compartir {
    fun bitmap(context:Context, path:String){
        try {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "image/*"

            Toast.makeText(context, path, Toast.LENGTH_LONG).show()

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(context, Intent.createChooser(shareIntent, "Presupuesto"), null)

        }catch (e :Exception){
            e.printStackTrace()
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }

    }
}