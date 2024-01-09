package com.example.blitzzettel


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blitzzettel.databinding.FragmentHomeBinding
import android.widget.Toast



/**
 * HomeFragment stellt die Hauptansicht der Anwendung dar. Es zeigt eine Liste von Zetteln.
 *  Von diesem Fragment aus kann man zu anderen Fragments navigieren,
 *  wo man neue Zettel hinzuzufügen oder vorhandene betrachten kann.
 */

class HomeFragment : Fragment(), HomeView {

    // Binding-Objekt für das Fragment, wird zur Verwaltung der Views verwendet
    private var _binding: FragmentHomeBinding? = null
    // Sicherer Zugriff auf das Binding, das null sein könnte
    private val binding get() = _binding!!

    // Presenter und Adapter für die Logik und Anzeige der Daten
    private lateinit var presenter: HomePresenter
    private lateinit var adapter: ZettelAdapter

    // onCreateView wird aufgerufen, um das Layout des Fragments zu "inflaten"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflater verwendet das Binding-Objekt, um das Layout aufzubauen
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Initialisierung des Presenters
        presenter = HomePresenter(this )
        // Die Wurzelansicht des Fragments wird zurückgegeben
        return binding.root
    }

    // onViewCreated wird aufgerufen, nachdem das Layout instanziiert wurde
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Einrichtung der RecyclerView, die die Zettel anzeigt
        setupRecyclerView()

        // Verwendung des SharedViewModels zur Verwaltung des Zustands und der Daten zwischen den Fragments
        val viewModel: SharedViewModel by activityViewModels()

        // Presenter bekommt das ViewModel übergeben
        presenter.onViewCreated(viewModel)

        // Listener für den Button, um den Dialog zum Hinzufügen neuer Zettel anzuzeigen
        binding.button.setOnClickListener {
            // Erstellen einer neuen Instanz des NewBlitzNoteDialogFragment
            NewBlitzNoteDialogFragment().show(
                // Verwenden des Child Fragment Managers, um das Dialogfragment anzuzeigen
                childFragmentManager,
                // Verwenden des TAGs aus dem NewBlitzNoteDialogFragment für die Fragment-Transaktion
                NewBlitzNoteDialogFragment.TAG
            )
        }

    }

    // Hilfsmethode zur Einrichtung der RecyclerView
    private fun setupRecyclerView() {
        adapter = ZettelAdapter(emptyList()) { zettel ->
            // Bei Klick auf einem Zettel wird zum SecondFragment navigiert und die Zettel-ID übergeben
            // Bundle mit Zettel-ID und Zettel-Titel
            val bundle = bundleOf(
                "zettelId" to zettel.id,
                "zettelTitel" to zettel.title
            )
            findNavController().navigate(R.id.action_HomeFragment_to_SecondFragment, bundle)
        }
        binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myRecyclerView.adapter = adapter

    }

    // Methode, um die Zettel in der RecyclerView anzuzeigen
    override fun showZettels(zettels: List<Zettel>) {
        adapter.zettelList = zettels
        adapter.notifyDataSetChanged()
    }

    // Methode zur Anzeige von Fehlermeldungen als Toast
    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // onDestroyView wird aufgerufen, um Ressourcen freizugeben
    override fun onDestroyView() {
        super.onDestroyView()
        // Binding-Ressourcen werden auf null gesetzt, um Speicherlecks zu vermeiden
        _binding = null
    }
}

// Interface, das die Methoden für das HomeView definiert
interface HomeView {
    fun showZettels(zettels: List<Zettel>) // Anzeige der Zettel
    fun showError(message: String) // Anzeige von Fehlermeldungen
}
