package com.example.blitzzettel

import android.content.Context
import android.content.SharedPreferences
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
class SettingsFragment : Fragment(), SettingsView {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    // SharedPreferences für das Speichern des ersten Login-Zustands
    private lateinit var sharedPreferences: SharedPreferences
    // Manager für verschlüsselte Einstellungen zur sicheren Speicherung von Anmeldeinformationen
    private lateinit var encryptedPrefsManager: EncryptedPrefsManager
    private lateinit var presenter: SettingsPresenter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        encryptedPrefsManager = EncryptedPrefsManager(requireContext())
        presenter = SettingsPresenter(this, EncryptedPrefsManager(requireContext()), sharedViewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()

        binding.aktualisierenButton.setOnClickListener {
            // Daten aus den Textfeldern erheben
            val neuerNutzername = binding.editTexNutzername.text
            val neuesPasswort = binding.editTextPasswort.text
            val neuesPasswortWdhl = binding.editTextPasswortwiederholen.text
            val neueServerId = binding.editTextServerid.text

            //Update anahnd der angegebenen Daten in Feldern
            presenter.performUpdate(neuerNutzername, neuesPasswort, neuesPasswortWdhl, neueServerId)
        }

        binding.resetButton.setOnClickListener {
            presenter.resetAutoLogin()
        }
    }
    override fun setCredentials(nutzername: String, passwort: String, serverid: String) {
        binding.editTexNutzername.setText(nutzername)
        binding.editTextPasswort.setText(passwort)
        binding.editTextPasswortwiederholen.setText(passwort)
        binding.editTextServerid.setText(serverid)
    }
    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToHome() {
        findNavController().navigate(R.id.action_SettingsFragment_to_HomeFragment)
    }

}


interface SettingsView {
    fun setCredentials(nutzername: String, passwort: String, serverid: String )
    fun showMessage(message: String)
    fun navigateToHome()
}