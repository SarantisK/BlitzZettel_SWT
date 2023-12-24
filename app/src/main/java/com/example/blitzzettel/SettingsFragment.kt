package com.example.blitzzettel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blitzzettel.databinding.FragmentSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveCred = EncryptedPrefsManager(requireContext())
        val (nutzername, passwort, serverid) = saveCred.getCredentials()

        binding.editTexNutzername.setText(nutzername)
        binding.editTextPasswort.setText(passwort)
        binding.editTextPasswortwiederholen.setText(passwort)
        binding.editTextServerid.setText(serverid)

        binding.aktualisierenButton.setOnClickListener {
            val neuerNutzername = binding.editTexNutzername.text.toString()
            val neuesPasswort = binding.editTextPasswort.text.toString()
            val neuesPasswortWdhl = binding.editTextPasswortwiederholen.text.toString()
            val neueServerId = binding.editTextServerid.text.toString()

            if (neuerNutzername.isBlank() || neuesPasswort.isBlank() || neueServerId.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Alle Felder müssen ausgefüllt sein",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (neuesPasswort != neuesPasswortWdhl) {
                Toast.makeText(
                    requireContext(),
                    "Passwörter stimmen nicht überein",
                    Toast.LENGTH_SHORT
                ).show()
                neuesPasswortWdhl
                return@setOnClickListener
            }

            val api = Api("", neueServerId)

            lifecycleScope.launch {
                // API-Aufruf zur Authentifizierung
                val token = withContext(Dispatchers.IO) {
                    api.postGenerateToken(neuerNutzername, neuesPasswort)
                }

                if (token != "400") {
                    val sharedViewModel: SharedViewModel by activityViewModels()
                    sharedViewModel.ServerIP = neueServerId
                    sharedViewModel.BearerToken = token // Class Viewmodel den Token übergeben
                    saveCred.saveCredentials(neuerNutzername, neuesPasswort, neueServerId)
                    Toast.makeText(requireContext(),
                        "Anmeldeinformationen aktualisiert",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(requireContext(), "Fehlgeschlagen", Toast.LENGTH_SHORT).show()
                }

                findNavController().navigate(R.id.action_SettingsFragment_to_HomeFragment)
            }
        }
    }
}
