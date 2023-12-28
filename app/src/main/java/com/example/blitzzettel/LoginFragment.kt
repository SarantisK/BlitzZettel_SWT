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
 * LoginFragment ist zuständig für die Benutzerauthentifizierung. Es fordert vom Benutzer,
 * Anmeldeinformationen einzugeben, validiert diese und kommuniziert mit der Zetelstore API, um einen
 * Authentifizierungstoken zu erhalten. Bei erfolgreicher Authentifizierung navigiert es zur
 * Hauptansicht (HomeFragment) der App und speichert die Anmeldeinformationen für zukünftige Sitzungen.
 */
class LoginFragment : Fragment() {

    // Binding-Variable für den Zugriff auf die Views
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // SharedPreferences für das Speichern des ersten Login-Zustands
    private lateinit var sharedPreferences: SharedPreferences
    // Manager für verschlüsselte Einstellungen zur sicheren Speicherung von Anmeldeinformationen
    private lateinit var encryptedPrefsManager: EncryptedPrefsManager

    // Layout des Fragments inflaten
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Das Binding wird initialisiert
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Initialisieren von SharedPreferences und EncryptedPrefsManager
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        encryptedPrefsManager = EncryptedPrefsManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Überprüfen, ob es der erste Start der App ist und aktualisiert den Zustand.
        if (isFirstTime()) {
            updateFirstTimeStatus()
        } else {
            // Automatische Anmeldung, falls nicht erster Start vorliegt
            autoLogin()
        }
        // Ein Listener, um auf den Anmeldeversuch zu reagieren.
        setupLoginListener()
    }

    // Konfiguration des Klick-Listeners für den Anmeldebutton.
    private fun setupLoginListener() {
        binding.anmeldeButton.setOnClickListener {
            // Extrahieren von Nutzername, Passwort und Server-ID aus den Eingabefeldern
            val nutzername = binding.editTexNutzername.text.toString()
            val passwort = binding.editTextPasswort.text.toString()
            val serverId = binding.editTextServerid.text.toString()

            // Überprüft die Eingaben und führt bei Gültigkeit die Anmeldung durch
            if (validateInputs(nutzername, passwort, serverId)) {
                performLogin(nutzername, passwort, serverId)
            }
        }
    }

    // Durchführen einer automatischen Anmeldung mit gespeicherten Credentials
    private fun autoLogin() {
        // Zugriff auf gespeicherten Anmeldeinformationen
        val (nutzername, passwort, serverId) = encryptedPrefsManager.getCredentials()

        // Führt die Anmeldung durch
        performLogin(nutzername ?: "", passwort ?: "", serverId ?: "")

        // Die gespeicherten Anmeldeinformationen in die Eingabefelder setzen
        binding.editTexNutzername.setText(nutzername)
        binding.editTextPasswort.setText(passwort)
        binding.editTextServerid.setText(serverId)
    }

    // Durchführung der Anmeldung mit gegebenen Anmeldeinformationen
    private fun performLogin(nutzername: String, passwort: String, serverId: String) {
        // API-Instanz erstellen
        val api = Api("", serverId)

        // Authentifizierung in einem Hintergrund-Thread durchführen
        lifecycleScope.launch {

            val token = withContext(Dispatchers.IO) {
                // API-Aufruf zur Authentifizierung
                api.postGenerateToken(nutzername, passwort)
            }
            // Verarbeitet die Antwort der Authentifizierung
            withContext(Dispatchers.Main) {
                processLoginResponse(token, nutzername, passwort, serverId)
            }
        }
    }

    // Verarbeitet die Antwort der API nach einem Anmeldeversuch
    private fun processLoginResponse(token: String?, nutzername: String, passwort: String, serverId: String) {
        // Handhabt die unterschiedlichen Antwortmöglichkeiten
        when (token) {

            // Fehlernachrichten für den Benutzer anzeigen
            "Netzwerkfehler" -> Toast.makeText(requireContext(), "Netzwerkfehler: Verfügbarkeit" +
                    " des Zettelstores prüfen", Toast.LENGTH_SHORT).show()

            "400" -> Toast.makeText(requireContext(),"Formulardaten ungültig", Toast.LENGTH_SHORT)
                .show()

            "401" -> Toast.makeText(requireContext(),"Benutzer-ID oder Passwort falsch", Toast.LENGTH_SHORT)
                .show()

            "403" -> Toast.makeText(requireContext(),"Authentifizierung nicht aktiv: " +
                    "Im Zettelstore einrichten", Toast.LENGTH_SHORT)
                .show()

            "Unbekannter Fehler" -> Toast.makeText(requireContext(),"Unbekannter Fehler", Toast.LENGTH_SHORT)
                .show()

            else -> {
                // Erfolgreiche Authentifizierung
                Toast.makeText(requireContext(), "Erfolgreich angemeldet", Toast.LENGTH_SHORT)
                    .show()
                // Auf HomeFragment weiterleiten
                findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)

                // Speichern von Token, ServerId, Nutzername und Passwort
                val sharedViewModel: SharedViewModel by activityViewModels()
                sharedViewModel.ServerIP = serverId
                sharedViewModel.BearerToken = token
                encryptedPrefsManager.saveCredentials(nutzername, passwort, serverId)

                // Erfolgreiche Anmeldung dem Benutzer anzeigen
                Toast.makeText(requireContext(), "Erfolgreich Angemeldet", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    // Überprüft, ob die Eingabefelder ausgefüllt sind
    private fun validateInputs(nutzername: String, passwort: String, serverId: String): Boolean {
        if (nutzername.isBlank() || passwort.isBlank() || serverId.isBlank()) {
            Toast.makeText(requireContext(), "Alle Felder ausfüllen", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Überprüft, ob es der erste Start der App ist
    private fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean("firstTime", true)
    }

    // Aktualisert den Zustand nach dem ersten Start der App
    private fun updateFirstTimeStatus() {
        sharedPreferences.edit().putBoolean("firstTime", false).apply()
    }

    // Wird aufgerufen, wenn die View zerstört wird
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

