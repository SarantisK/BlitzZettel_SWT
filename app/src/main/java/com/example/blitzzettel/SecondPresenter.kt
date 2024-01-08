package com.example.blitzzettel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch


// Definition der Klasse SecondPresenter. Diese Klasse ist Teil des MVP-Architekturmusters und fungiert als Presenter.
// Der Presenter ist verantwortlich für die Geschäftslogik und die Kommunikation zwischen der View (Benutzeroberfläche) und dem Model (Daten).
class SecondPresenter (private val view: SecondView) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Definition der Funktion 'onViewCreated'
    fun onViewCreated(zettlid: String?, viewModel: SharedViewModel) {

        // Überprüfung, ob die Variable 'zettlid' nicht null ist.
        // Erstellung einer Instanz der Api-Klasse.
        // Der BearerToken w für die Authentifizierung bei API-Anfragen
        if (zettlid != null) {
            val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)

            // Mit Coroutines die Daten asynchron laden und anzeigen
            coroutineScope.launch {

                // API-Aufruf, um einen spezifischen Blitz-Zettel abzurufen
                val responseData = withContext(Dispatchers.IO) {
                    api.getSpecificBlitzZettel(zettlid)
                }
                // Setzt den Text der TextView auf die API-Antwort
                view.showZettelContent(responseData.toString())
            }
        }
    }
}



