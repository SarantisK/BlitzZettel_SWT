package com.example.blitzzettel
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

//Hier werden die neuen Zettel erstellt mittels AlertDialog
class NewBlitzNoteDialogFragment : DialogFragment() {

    // Initialisierung des SharedViewModels
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Einrichten des AlertDialog-Builders
        val builder = AlertDialog.Builder(requireContext())

        // Aufblasen des benutzerdefinierten Layouts für den Dialog
        val view = requireActivity().layoutInflater.inflate(R.layout.fragmen_dialog, null)

        // Benutzerdefinierte Ansicht zum Dialog hinzufügen
        builder.setView(view)

        // Dialog initialisieren
        initialzieDialog(view)


        return builder.create() // Erstellen und Rückgabe des Dialogs
    }

    private fun initialzieDialog(view: View) {

        // UI-Komponenten im Dialog als Variablen gespeichert
        val blitzTitel = view.findViewById<EditText>(R.id.blitz_titel)
        val blitzContent = view.findViewById<EditText>(R.id.blitz_content)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        // Setzen des OnClickListener für den Hinzufügen-Button
        addButton.setOnClickListener {
            // Extrahieren des Textes aus den EditText-Feldern
            val newTitel = blitzTitel.text.toString()
            val newContent = blitzContent.text.toString()

            // Überprüfen, ob Titel nicht leer ist
            if (newTitel.isNotBlank()) {
                // Funktion zum Erstellen eines Zettels mittels der API
                sendNoteData(newTitel, newContent)
                // Zurücksetzen der Eingabefelder nach dem Senden
                blitzTitel.text.clear()
                blitzContent.text.clear()
            } else {
                //Anzeigen einer Toast-Nachricht, wenn Titel leer ist
                Toast.makeText(requireContext(), "Titel ist erforderlich", Toast.LENGTH_SHORT).show()
            }
        }

        // Setzen des OnClickListener für den Abbrechen-Button
        cancelButton.setOnClickListener {
            // Navigieren zurück zum HomeFragment
            findNavController().navigate(R.id.action_HomeFragment_self)
            dismiss() // Dialog schließen
        }
    }
    private fun sendNoteData(title: String, content: String) {
        // Initialisieren der API mit Token und Server-IP
        val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)
        // Ausführen des API-Aufrufs innerhalb eines Coroutine-Scopes
        lifecycleScope.launch {
            // Senden der Daten und Empfangen des Feedbacks von der API
            val feedback = api.postZettelErstellen(title, content)
            // Anzeigen des Feedbacks in einer Toast-Nachricht
            Toast.makeText(requireContext(), feedback, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        // Eine Konstante, die als eindeutiger Bezeichner (TAG)
        // für das NewBlitzNoteDialogFragment dient
        const val TAG = "NewBlitzNoteDialog"
    }
}
