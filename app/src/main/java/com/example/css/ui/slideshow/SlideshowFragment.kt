package com.example.css.ui.slideshow

import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.css.R
import com.example.css.model.MyProducto
import org.w3c.dom.Text
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.sqrt

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var ALTURA:Double = 1.0
    private var ANCHO:Double = 1.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProviders.of(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val altura:SeekBar = root.findViewById(R.id.seekBar_alto)
        val ancho:SeekBar = root.findViewById(R.id.seekBar_ancho)
        val metrosCuadrados:EditText = root.findViewById(R.id.editText)
        val metrosAncho:EditText = root.findViewById(R.id.editText2)
        val metrosAlto:EditText = root.findViewById(R.id.editText3)
        val frame : FrameLayout = root.findViewById(R.id.frame)
        val frameInterior : FrameLayout = root.findViewById(R.id.frameInterior)

        val descripcion: TextView = root.findViewById(R.id.txtDesc)
        val metrosPorCaja : TextView = root.findViewById(R.id.txtM2)
        val precioPorCaja : TextView = root.findViewById(R.id.txtPrecioXCaja)
        val resumen  : TextView = root.findViewById(R.id.resumen)
        val total : TextView = root.findViewById(R.id.total)



        slideshowViewModel.getProducto().observe(this, Observer {
            descripcion.text = it.descripcion
            metrosPorCaja.text = "$"+it.precioContado()+" x M2"
            precioPorCaja.text = "$"+it.precioContadoPor(it.ratioConversion)+" x CAJA"

        })

        altura.progress = 2
        altura.max = 20
        altura.incrementProgressBy(1)
        ancho.progress = 2
        ancho.max = 20
        ancho.incrementProgressBy(1)
        metrosAlto.setText("1")
        metrosAncho.setText("1")
        metrosCuadrados.setText("1")



        altura.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                metrosCuadrados.clearFocus()
                ALTURA = (i.toDouble()/2).toDouble()
                metrosAlto.setText(ALTURA.toString())
                val decimal = BigDecimal(ALTURA*ANCHO).setScale(2, RoundingMode.HALF_EVEN)
                metrosCuadrados.setText(decimal.toString())
                updateFrame(frame,frameInterior)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        ancho.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                metrosCuadrados.clearFocus()
                ANCHO = (i.toDouble()/2).toDouble()
                metrosAncho.setText(ANCHO.toString())
                val decimal = BigDecimal(ALTURA*ANCHO).setScale(2, RoundingMode.HALF_EVEN)
                metrosCuadrados.setText(decimal.toString())
                updateFrame(frame,frameInterior)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        metrosCuadrados.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (metrosCuadrados.isFocused) {
                    if (!p0.isNullOrBlank()) {
                        var cantidad = ("0" + p0.toString()).toDouble()
                        ANCHO = sqrt(cantidad)
                        ALTURA = sqrt(cantidad)
                        metrosAncho.setText(
                            BigDecimal(ANCHO).setScale(2, RoundingMode.HALF_EVEN).toString()
                        )
                        metrosAlto.setText(
                            BigDecimal(ALTURA).setScale(2, RoundingMode.HALF_EVEN).toString()
                        )
                        updateFrame(frame, frameInterior)
                    }

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        )

        metrosAlto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                    if (!p0.isNullOrBlank()) {
                        var alto = ("0" + p0.toString()).toDouble()
                        ALTURA = alto
                        val decimal = BigDecimal(ALTURA*ANCHO).setScale(2, RoundingMode.HALF_EVEN)
                        metrosCuadrados.setText(decimal.toString())

                        updateFrame(frame, frameInterior)
                    }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        )


      // val input :InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        metrosAncho.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                    if (!p0.isNullOrBlank()) {
                        var ancho = ("0" + p0.toString()).toDouble()
                        ANCHO = ancho
                        val decimal = BigDecimal(ALTURA*ANCHO).setScale(2, RoundingMode.HALF_EVEN)
                        metrosCuadrados.setText(decimal.toString())
                        updateFrame(frame, frameInterior)
                    }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        )


        return root
    }

    fun updateFrame(frame:FrameLayout,frameInterior:FrameLayout){
        val params: FrameLayout.LayoutParams
        if(ANCHO >= ALTURA)
            params = FrameLayout.LayoutParams(frame.width,((frame.height)*(ALTURA/ANCHO)).toInt(),Gravity.CENTER)
        else
            params = FrameLayout.LayoutParams((frame.width*(ANCHO/ALTURA)).toInt(),frame.height,Gravity.CENTER)

        frameInterior.layoutParams=params
    }

}