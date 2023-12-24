package com.example.blitzzettel

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
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    //private var token:String? = ""

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val saveCred= EncryptedPrefsManager(requireContext())
        binding.anmeldeButton.setOnClickListener{

            // Hole alle Daten aus den Textfeldern

            val nutzername = binding.editTexNutzername.text.toString()
            val passwort = binding.editTextPasswort.text.toString()
            val serverId = binding.editTextServerid.text.toString()
            val api = Api("",serverId)

            if (nutzername.isBlank() || passwort.isBlank() || serverId.isBlank()) {
                Toast.makeText(requireContext(), "Alle Felder ausfüllen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // API-Aufruf zur Authentifizierung
                val token = withContext(Dispatchers.IO) {
                    api.postGenerateToken(nutzername, passwort)
                }

                if (token != "400") {
                    val sharedViewModel: SharedViewModel by activityViewModels()
                    sharedViewModel.ServerIP = serverId
                    sharedViewModel.BearerToken = token // Class Viewmodel den Token übergeben
                    saveCred.saveCredentials(nutzername, passwort, serverId)


                    findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
                } else {
                    //Funktioniert noch nicht
                    Toast.makeText(requireContext(), "Fehlgeschlagen", Toast.LENGTH_SHORT).show()
                }
            }

        }




        /*if (isFirstTime()) {
            updateFirstTimeStatus()
            binding.anmeldeButton.setOnClickListener {
                val nutzername = binding.editTextTextNutzername.text.toString()
                val passwort = binding.editTextTextPassword.text.toString()
                val serverId = binding.editTextServerid.text.toString()
                // API Abfrage (PostGenerateToken)
                cred.saveCredentials(nutzername, passwort, serverId)





                findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
            }
            // Nach Aktionen beim ersten Start, navigiere zu einem anderen Fragment

        } else {
            cred.clearCredentials()
            navigateToHomeIfCredentialsExist()
        }*/


/*
        if (EncryptedPrefsManager.hasCredentials(requireContext())) {
            val credentials = cred.getCredentials()
            binding.editTextTextNutzername.setText(credentials.first)
            binding.editTextTextPassword.setText(credentials.second)
            binding.editTextServerid.setText(credentials.third)

        }

        binding.anmeldeButton.setOnClickListener {
            val nutzername = binding.editTextTextNutzername.text.toString()
            val passwort = binding.editTextTextPassword.text.toString()
            val serverId = binding.editTextServerid.text.toString()
            cred.saveCredentials(nutzername, passwort, serverId)

            findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
        }*/

    }


    private fun navigateToHomeIfCredentialsExist() {


        if (EncryptedPrefsManager.hasCredentials(requireContext())) {
            // Navigiere zum HomeFragment, wenn Anmeldeinformationen vorhanden sind
            findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
        }
    }
    private fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean("firstTime", true)
    }

    private fun updateFirstTimeStatus() {
        // Setze den Zustand auf false, da die App jetzt gestartet wurde
        sharedPreferences.edit().putBoolean("firstTime", false).apply()
    }
}
