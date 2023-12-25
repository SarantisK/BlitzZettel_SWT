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
import com.example.blitzzettel.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var encryptedPrefsManager: EncryptedPrefsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        encryptedPrefsManager = EncryptedPrefsManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstTime()) {
            updateFirstTimeStatus()
            setupLoginListener()
        } else {
            autoLogin()
        }
    }

    private fun setupLoginListener() {
        binding.anmeldeButton.setOnClickListener {
            val nutzername = binding.editTexNutzername.text.toString()
            val passwort = binding.editTextPasswort.text.toString()
            val serverId = binding.editTextServerid.text.toString()

            if (validateInputs(nutzername, passwort, serverId)) {
                performLogin(nutzername, passwort, serverId)
            }
        }
    }

    private fun autoLogin() {
        val (nutzername, passwort, serverId) = encryptedPrefsManager.getCredentials()
        performLogin(nutzername ?: "", passwort ?: "", serverId ?: "")
        binding.editTexNutzername.setText(nutzername)
        binding.editTextPasswort.setText(passwort)
        binding.editTextServerid.setText(serverId)
    }


    private fun performLogin(nutzername: String, passwort: String, serverId: String) {
        val api = Api("", serverId)
        lifecycleScope.launch {
            val token = withContext(Dispatchers.IO) {
                api.postGenerateToken(nutzername, passwort)
            }

            if (token != "400" && token != "403") {
                findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
                val sharedViewModel: SharedViewModel by activityViewModels()
                sharedViewModel.ServerIP = serverId
                sharedViewModel.BearerToken = token
                encryptedPrefsManager.saveCredentials(nutzername, passwort, serverId)
                Toast.makeText(requireContext(), "Erfolgreich Angemeldet", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "Fehlgeschlagen", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateInputs(nutzername: String, passwort: String, serverId: String): Boolean {
        if (nutzername.isBlank() || passwort.isBlank() || serverId.isBlank()) {
            Toast.makeText(requireContext(), "Alle Felder ausfüllen", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean("firstTime", true)
    }

    private fun updateFirstTimeStatus() {
        sharedPreferences.edit().putBoolean("firstTime", false).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

