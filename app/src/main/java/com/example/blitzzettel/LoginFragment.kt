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
     * LoginFragment ist zuständig für die Benutzerauthentifizierung. Es fordert vom Benutzer,
     * Anmeldeinformationen einzugeben, validiert diese und kommuniziert mit der Zetelstore API, um einen
     * Authentifizierungstoken zu erhalten. Bei erfolgreicher Authentifizierung navigiert es zur
     * Hauptansicht (HomeFragment) der App und speichert die Anmeldeinformationen für zukünftige Sitzungen.
     */
    class LoginFragment : Fragment(), LoginView {

        // Binding-Variable für den Zugriff auf die Views
        private var _binding: FragmentLoginBinding? = null
        // Binding = non null variable, die auf Binding-Objekt zugreift
        private val binding get() = _binding!!
        // Presenter-Objekt für Login wird initialisiert
        private lateinit var presenter: LoginPresenter



        private val sharedViewModel: SharedViewModel by activityViewModels()

        // Die onCreateView-Methode wird überschrieben, um das Layout für das Fragment zu erstellen/inflaten
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Die Binding wird initialisiert
            _binding = FragmentLoginBinding.inflate(inflater, container, false)
            // Der LoginPresenter wird initialisiert und erhält eine Referenz auf das aktuelle Fragment, einen EncryptedPrefsManager und das sharedViewModel
            presenter = LoginPresenter(this, EncryptedPrefsManager(requireContext()), sharedViewModel)

            // Root View des Fragments wird zurückgegeben
            return binding.root
        }

        // onViewCreated-Methode wird überschrieben und erhält View und gespeicherten Zustand
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            //Aufruf der übergeordneten Klasse
            super.onViewCreated(view, savedInstanceState)
            // onViewCreated wird aufgerufen
            presenter.onViewCreated()

            binding.anmeldeButton.setOnClickListener {
                // Extrahieren von Nutzername, Passwort und Server-ID aus den Eingabefeldern
                val nutzername = binding.editTexNutzername.text.toString()
                val passwort = binding.editTextPasswort.text.toString()
                val serverId = binding.editTextServerid.text.toString()
                presenter.performLogin(nutzername, passwort, serverId)
            }
        }

        override fun autoFillCredentials(nutzername: String, serverId: String,passwort : String) {
            binding.editTexNutzername.setText(nutzername)
            binding.editTextPasswort.setText(passwort) // BUG???
            binding.editTextServerid.setText(serverId)
        }
        override fun showErrorMessage(message: String) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        override fun navigateToHome() {
            findNavController().navigate(R.id.action_loginFragment_to_HomeFragment)
        }

        // Wird aufgerufen, wenn die View zerstört wird
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

    interface LoginView {
        fun showErrorMessage(message: String)
        fun navigateToHome()
        fun autoFillCredentials(nutzername: String, serverId: String,passwort: String)
    }



