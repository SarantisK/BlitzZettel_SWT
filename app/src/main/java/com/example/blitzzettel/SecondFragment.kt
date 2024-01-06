package com.example.blitzzettel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blitzzettel.databinding.FragmentSecondBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Erstellt Binding Klasse, um
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Zettel-ID, die von HomeFragment übergeben wurde
        val zettelId = arguments?.getString("zettelId")
        val viewModel:SharedViewModel by activityViewModels()
        val api = Api(viewModel.BearerToken.toString(),viewModel.ServerIP)

        // Auf Button "buttonSecond" geklickt, dann zum HomeFragment navigieren
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_HomeFragment)
        }


        // Mit Coroutines die Daten asynchron laden und anzeigen
        lifecycleScope.launch {


            // API-Aufruf, um einen spezifischen Blitz-Zettel abzurufen
            val responseData = withContext(Dispatchers.IO) {
                api.getSpecificBlitzZettel(zettelId.toString())
            }
            // Setzt den Text der TextView auf die API-Antwort
            binding.textviewSecond.text = responseData.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}