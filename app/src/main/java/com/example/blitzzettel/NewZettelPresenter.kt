package com.example.blitzzettel
import android.text.Editable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewZettelPresenter (private val view: NewZettelView) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun addNewNote(title: Editable, content: Editable, viewModel: SharedViewModel) {

        if (title.isNotBlank()) {
            coroutineScope.launch {
                val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)
                val feedback = api.postZettelErstellen(title.toString(), content.toString())
                view.showFeedback(feedback)
            }

        } else {
            view.showFeedback("Titel ist erforderlich")
        }
    }

}
