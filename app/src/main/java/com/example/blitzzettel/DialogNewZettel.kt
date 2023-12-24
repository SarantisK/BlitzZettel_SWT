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


class NewBlitzNoteDialogFragment : DialogFragment() {



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val viewModel:SharedViewModel by activityViewModels()
        val api = Api(viewModel.BearerToken.toString(),viewModel.ServerIP)
        // Inflate the custom layout for the dialog
        val view = requireActivity().layoutInflater.inflate(R.layout.fragmen_dialog, null)

        // Find views and perform actions
        val blitzTitel= view.findViewById<EditText>(R.id.blitz_titel)
        val blitzContent= view.findViewById<EditText>(R.id.blitz_content)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val feedbackTextView = view.findViewById<TextView>(R.id.feedback)

        // Define actions for the buttons
        addButton.setOnClickListener {
            // Perform action when Add button is clicked
            // For example, get text from EditText and handle it
            val newTitel = blitzTitel.text.toString()
            val newContent= blitzContent.text.toString()

            if (newTitel.isBlank()) {
                Toast.makeText(requireContext(), "Titel hinzufügen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val feedback = api.postZettelErstellen(newTitel, newContent).toString()
                // Anzeigen des Feedbacks im TextView
                Toast.makeText(requireContext(), feedback, Toast.LENGTH_SHORT).show()
            }

            blitzTitel.text.clear()
            blitzContent.text.clear()

        }

        cancelButton.setOnClickListener {
            // Perform action when Cancel button is clicked
            findNavController().navigate(R.id.action_HomeFragment_self) // Navigation auf Home Fenster für refresh der Zettel
            dismiss() // Dismiss the dialog
        }

        // Set the custom view to the dialog
        builder.setView(view)

        return builder.create()
    }

    companion object {
        const val TAG = "NewBlitzNoteDialog"
    }
}
