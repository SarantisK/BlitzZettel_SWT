package com.example.blitzzettel


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blitzzettel.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

     // Holt sich die Daten aus dem Viewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel:SharedViewModel by activityViewModels()

        val api = Api(viewModel.BearerToken.toString(),viewModel.ServerIP)

        // Initialisieren des Adapters mit einer leeren Liste
        val adapter = ZettelAdapter(emptyList()) { zettel ->
            // Bei Klick auf einem Zettel wird zum SecondFragment navigiert
            // und die Zettel-ID übergeben
            val bundle = bundleOf("zettelId" to zettel.id)
            findNavController().navigate(R.id.action_HomeFragment_to_SecondFragment, bundle)
        }

        // RecyclerView initialisieren
        binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myRecyclerView.adapter = adapter

        // Daten laden und Adapter aktualisieren mit API-Daten
        lifecycleScope.launch {
            // API-Aufruf für alle Blitzzettel
            val responseData = withContext(Dispatchers.IO) {
                api.getAllBlitzTagsApi()
            }
            // Parsen der Daten als Liste von Zettel-Objekten
            val zettelList = parseZettelListApi(responseData.toString())

            // Aktualisieren des Adapters auf dem Hauptthread mit Zettel-Liste
            withContext(Dispatchers.Main) {
                adapter.zettelList = zettelList
                adapter.notifyDataSetChanged()
            }
        }


        binding.button.setOnClickListener {
            NewBlitzNoteDialogFragment().show(
                childFragmentManager, NewBlitzNoteDialogFragment.TAG
            )
        }
        // Aufruf des Dialogs beim Klick auf einen Button oder einer anderen Aktion

    }
}

