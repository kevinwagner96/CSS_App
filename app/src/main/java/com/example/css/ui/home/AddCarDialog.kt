package com.example.css.ui.home


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.css.R


class AddCarDialog : AppCompatDialogFragment() {



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.layout_add_to_car,null)
        val scroll = view.findViewById<SeekBar>(R.id.seekBar)
        val cant = view.findViewById<EditText>(R.id.editTextCantidad)
        /*
        scroll?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                cant.setText(i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
*/
        builder.setView(view)
            .setTitle("Add")
            .setNeutralButton("Cancel"){_,_ ->
                Toast.makeText(this.context,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("Agregar"){dialog, which ->
                Toast.makeText(this.context,"Agergado", Toast.LENGTH_SHORT).show()
            }

        return builder.create()
    }


}