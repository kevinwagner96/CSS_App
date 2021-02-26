package com.example.css.ui.tools

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.css.R
import java.net.URL
import java.net.URLConnection

var text = "nada"

class ToolsFragment : Fragment() {

    private lateinit var toolsViewModel: ToolsViewModel


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProviders.of(this).get(ToolsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        var server1: TextView = root.findViewById(R.id.textViewServer1)
        var server2: TextView = root.findViewById(R.id.textViewServer2)
        var server3: TextView = root.findViewById(R.id.textViewServer3)

        toolsViewModel.text.observe(this, Observer {
            textView.text = it
        })

        server1.text = getString(R.string.server1)
        server2.text = getString(R.string.server2)
        server3.text = getString(R.string.server3)

        /*
        toolsViewModel.server1.observe(this, Observer {
            server1.text = it
        })
        toolsViewModel.server2.observe(this, Observer {
            server2.text = it
        })
        toolsViewModel.server3.observe(this, Observer {
            server3.text = it
        })

         */



        class ServerStatus :
            AsyncTask<Void?, Void?, Void?>() {

            var server1_status = false
            var server2_status = false
            var server3_status = false


            override fun doInBackground(vararg params: Void?): Void? {
                server1_status = isHostAvailable(getString(R.string.server1), 80, 3000)
                server2_status = isHostAvailable(getString(R.string.server2), 80, 3000)
                server3_status = isHostAvailable(getString(R.string.server3), 80, 3000)
                return null
            }

            override fun onPostExecute(result: Void?) {
                if(server1_status)
                    server1.setTextColor(Color.GREEN)
                else
                    server1.setTextColor(Color.RED)

                if(server2_status)
                    server2.setTextColor(Color.GREEN)
                else
                    server2.setTextColor(Color.RED)

                if(server3_status)
                    server3.setTextColor(Color.GREEN)
                else
                    server3.setTextColor(Color.RED)

            }

            /**
             * Check if host is reachable.
             * @param host The host to check for availability. Can either be a machine name, such as "google.com",
             * or a textual representation of its IP address, such as "8.8.8.8".
             * @param port The port number.
             * @param timeout The timeout in milliseconds.
             * @return True if the host is reachable. False otherwise.
             */

            fun isHostAvailable(host: String?, port: Int, timeout: Int): Boolean {
                return try {
                    val myUrl = URL(host)
                    val connection: URLConnection = myUrl.openConnection()
                    connection.setConnectTimeout(timeout)
                    connection.connect()
                    true
                } catch (e: Exception) {
                    // Handle your exceptions
                    false
                }
            }

        }

        ServerStatus().execute()


        return root
    }


}