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
 * HomeFragment stellt die Hauptansicht der Anwendung dar. Es zeigt eine Liste von Zetteln an und
 * ermöglicht dem Benutzer, neue Zettel hinzuzufügen oder vorhandene zu betrachten.
 */
class HomeFragment : Fragment() {

    // Binding-Variable für den Zugriff auf die Views
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Layout des Fragments inflaten
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verwendung des SharedViewModels zur Verwaltung des Zustands und der Daten zwischen den Fragments
        val viewModel:SharedViewModel by activityViewModels()

        // API-Instanz wird erstellt
        val api = Api(viewModel.BearerToken.toString(),viewModel.ServerIP)

        // Initialisieren des Adapters mit einer leeren Liste
        val adapter = ZettelAdapter(emptyList()) { zettel ->
            // Bei Klick auf einem Zettel wird zum SecondFragment navigiert und die Zettel-ID übergeben
            val bundle = bundleOf("zettelId" to zettel.id)
            findNavController().navigate(R.id.action_HomeFragment_to_SecondFragment, bundle)
        }

        // Initialisiert den Layoutmanager und Adapter der RrcyclerView
        binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myRecyclerView.adapter = adapter

        // Daten laden und Adapter aktualisieren mit API-Daten
        lifecycleScope.launch {
            // API-Aufruf für alle Blitzzettel im Hintergrund-Thread
            val responseData = withContext(Dispatchers.IO) {
                api.getAllBlitzTagsApi()
            }
            // Parsen der Daten als Liste von Zettel-Objekten
            val zettelList = parseZettelListApi(responseData.toString())

            // Aktualisieren des Adapters im Haupt-Thread mit Zettel-Liste
            withContext(Dispatchers.Main) {
                adapter.zettelList = zettelList
                adapter.notifyDataSetChanged()
            }
        }

        // Listener für den Button, um den Dialog zum Hinzufügen neuer Zettel anzuzeigen
        binding.button.setOnClickListener {
            NewBlitzNoteDialogFragment().show(
                childFragmentManager, NewBlitzNoteDialogFragment.TAG
            )
        }
    }
}

