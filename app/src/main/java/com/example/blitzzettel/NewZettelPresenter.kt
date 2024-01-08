package com.example.blitzzettel
import android.text.Editable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Präsentationslogik für das Erstellen einer neuen Notiz (Zettel).
class NewZettelPresenter(private val view: NewZettelView) {
    // Eigenschaften und Abhängigkeiten
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun addNewNote(title: Editable, content: Editable, viewModel: SharedViewModel, clearInputFields: () -> Unit) {
        // Überprüft, ob der title nicht leer ist
        if (title.isNotBlank()) {
            //Coroutine für asynchrone Operationen
            coroutineScope.launch {
                // Api-Instanz mit Token und ServerIP aus dem viewModel
                val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)
                // Ruft die Methode postZettelErstellen auf der Api-Instanz auf, um einen neuen Zettel zu erstellen
                val feedback = api.postZettelErstellen(title.toString(), content.toString())
                // Zeigt Feedback in der Ansicht an
                view.showFeedback(feedback)
                clearInputFields()
            }
        } else {
            // Zeigt eine Meldung in der Ansicht an, dass ein Titel erforderlich ist, wenn der title leer ist
            view.showFeedback("Titel ist erforderlich")
        }
    }
}

