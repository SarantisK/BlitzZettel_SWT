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
        private val binding get() = _binding!!

        private lateinit var presenter: LoginPresenter



        private val sharedViewModel: SharedViewModel by activityViewModels()

        // Layout des Fragments inflaten
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Das Binding wird initialisiert
            _binding = FragmentLoginBinding.inflate(inflater, container, false)

            presenter = LoginPresenter(this, EncryptedPrefsManager(requireContext()), sharedViewModel)


            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            presenter.onViewCreated()

            binding.anmeldeButton.setOnClickListener {
                // Extrahieren von Nutzername, Passwort und Server-ID aus den Eingabefeldern
                val nutzername = binding.editTexNutzername.text
                val passwort = binding.editTextPasswort.text
                val serverId = binding.editTextServerid.text
                presenter.performLogin(nutzername, passwort, serverId)
            }
        }

        override fun autoFillCredentials(nutzername: String, serverId: String) {
            binding.editTexNutzername.setText(nutzername)
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
        fun autoFillCredentials(nutzername: String, serverId: String)
    }



