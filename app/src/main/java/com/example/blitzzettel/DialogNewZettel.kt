package com.example.blitzzettel
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

//Hier werden die neuen Zettel erstellt mittels AlertDialog
class NewBlitzNoteDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Einrichten des AlertDialog-Builders
        val builder = AlertDialog.Builder(requireContext())

        // Initialisierung des SharedViewModels und der API-Klasse
        val viewModel: SharedViewModel by activityViewModels()
        val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)

        // Aufblasen des benutzerdefinierten Layouts für den Dialog
        val view = requireActivity().layoutInflater.inflate(R.layout.fragmen_dialog, null)

        // Suchen von Ansichten und Ausführen von Aktionen
        val blitzTitel = view.findViewById<EditText>(R.id.blitz_titel)
        val blitzContent = view.findViewById<EditText>(R.id.blitz_content)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val feedbackTextView = view.findViewById<TextView>(R.id.feedback)

        // Definieren von Aktionen für die Schaltflächen
        addButton.setOnClickListener {
            // Aktion beim Klicken der Hinzufügen-Schaltfläche ausführen
            // Beispielsweise Text aus EditText abrufen und verarbeiten
            val newTitel = blitzTitel.text.toString()
            val newContent = blitzContent.text.toString()

            // Überprüfen, ob der Titel leer ist
            if (newTitel.isBlank()) {
                Toast.makeText(requireContext(), "Titel hinzufügen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aufruf der API, um eine neue Blitznotiz zu erstellen
            lifecycleScope.launch {
                val feedback = api.postZettelErstellen(newTitel, newContent).toString()
                // Feedback in einem Toast anzeigen
                Toast.makeText(requireContext(), feedback, Toast.LENGTH_SHORT).show()
            }

            // Textfelder leeren
            blitzTitel.text.clear()
            blitzContent.text.clear()
        }

        cancelButton.setOnClickListener {
            // Aktion beim Klicken der Abbrechen-Schaltfläche ausführen
            findNavController().navigate(R.id.action_HomeFragment_self) // Navigation zur Startseite zur Aktualisierung der Notizen
            dismiss() // Dialog schließen
        }

        // Benutzerdefinierte Ansicht zum Dialog hinzufügen
        builder.setView(view)

        return builder.create() // Erstellen und Rückgabe des Dialogs
    }

    companion object {
        const val TAG = "NewBlitzNoteDialog"
    }
}

