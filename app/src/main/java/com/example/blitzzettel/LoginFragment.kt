package com.example.blitzzettel

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.blitzzettel.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val cred = EncryptedPrefsManager(requireContext())

        if (isFirstTime()) {
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
            navigateToHomeIfCredentialsExist()
        }


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
