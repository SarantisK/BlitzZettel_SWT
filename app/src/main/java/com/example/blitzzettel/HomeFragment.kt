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

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: HomePresenter
    private lateinit var adapter: ZettelAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        presenter = HomePresenter(this )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // Verwendung des SharedViewModels zur Verwaltung des Zustands und der Daten zwischen den Fragments
        val viewModel: SharedViewModel by activityViewModels()
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

    private fun setupRecyclerView() {
        adapter = ZettelAdapter(emptyList()) { zettel ->
            // Bei Klick auf einem Zettel wird zum SecondFragment navigiert und die Zettel-ID übergeben
            val bundle = bundleOf("zettelId" to zettel.id)
            findNavController().navigate(R.id.action_HomeFragment_to_SecondFragment, bundle)
        }
        binding.myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myRecyclerView.adapter = adapter

    }

    override fun showZettels(zettels: List<Zettel>) {
        adapter.zettelList = zettels
        adapter.notifyDataSetChanged()
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface HomeView {
    fun showZettels(zettels: List<Zettel>)
    fun showError(message: String)
}
