package com.example.blitzzettel
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment


class NewBlitzNoteDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        // Inflate the custom layout for the dialog
        val view = requireActivity().layoutInflater.inflate(R.layout.fragmen_dialog, null)

        // Find views and perform actions
        val blitzTitel= view.findViewById<EditText>(R.id.blitz_titel)
        val blitzContent= view.findViewById<EditText>(R.id.blitz_content)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)

        // Define actions for the buttons
        addButton.setOnClickListener {
            // Perform action when Add button is clicked
            // For example, get text from EditText and handle it
            val newTitel = blitzTitel.text.toString()
            val newContent= blitzContent.text.toString()

            // Perform your action here
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






