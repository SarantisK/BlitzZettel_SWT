package com.example.blitzzettel
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class NewBlitzNoteDialogFragment : DialogFragment() {

    private val api = Api()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

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

            lifecycleScope.launch {
                val feedback = api.postZettelErstellen(newTitel, newContent)
                // Anzeigen des Feedbacks im TextView
                feedbackTextView.text = feedback
            }
            blitzTitel.text.clear()
            blitzContent.text.clear()

        }

        cancelButton.setOnClickListener {
            // Perform action when Cancel button is clicked
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
