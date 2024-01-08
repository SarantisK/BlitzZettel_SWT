package com.example.blitzzettel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.blitzzettel.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), SecondView {

    private var _binding: FragmentSecondBinding? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var presenter: SecondPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Erstellt Binding Klasse, um
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        presenter = SecondPresenter(this)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Zettel-ID, die von HomeFragment übergeben wurde
        val zettelId = arguments?.getString("zettelId")

        // Zettel-Titel, die von HomeFragment übergeben wurde
        val zettelTitel = arguments?.getString("zettelTitel")
        binding.textviewTitel.text = zettelTitel

        val viewModel:SharedViewModel by activityViewModels()

        presenter.onViewCreated(zettelId, viewModel)

        // Auf Button "buttonSecond" geklickt, dann zum HomeFragment navigieren
        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_HomeFragment)
        }

    }
    override fun showZettelContent(data: String) {
        binding.textviewSecond.text = data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface SecondView {
    fun showZettelContent(data: String)
}
