package com.example.blitzzettel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.blitzzettel.databinding.FragmentFirstBinding

// API Libraries
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        // die Daten werden angezeigt
        fetchDataAndDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Funktion weist der View Daten aus API zu
    private fun fetchDataAndDisplay() {
        val response = fetchDataFromAPI()
        binding.textviewFirst.text = response
    }

    // Funktion für API Aufruf
    private fun fetchDataFromAPI(): String {
        val url = URL("http://127.0.0.1:23123/z") //?q=tags%3A%23blitz&enc=plain
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val response = StringBuffer()

                while (inputStream.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                inputStream.close()

                connection.disconnect()

                response.toString()
            } else {
                "HTTP GET request failed: $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error fetching data"
        } finally {
            connection.disconnect()
        }
    }
}
