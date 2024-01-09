package com.example.blitzzettel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
*
* // Der HomePresenter ist verantwortlich für die Verarbeitung der Logik des HomeFragments.
 */

class HomePresenter(private val view: HomeView) {
    // Erstellt einen CoroutineScope, der auf dem Main-Thread ausgeführt wird,
    // um UI-Aktualisierungen zu ermöglichen.
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // onViewCreated wird aufgerufen, wenn das View erstellt wurde.
    fun onViewCreated(viewModel: SharedViewModel) {


        // API-Instanz wird mit dem Bearer Token und der Server-IP aus dem ViewModel erstellt
        val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)

        // Führe die API-Anfrage im IO-Dispatcher aus, um den Main-Thread nicht zu blockieren
        coroutineScope.launch {

            val responseData = withContext(Dispatchers.IO) {
                api.getAllBlitzTagsApi()

            }
            // Werte den API-Aufruf aus und zeige entsprechende Toast-Nachrichten an
            when (responseData) {

                null -> view.showError(message = "Fehler bei der API-Anfrage")
                "Keine Zettel vorhanden" -> view.showError(message = "Keine Zettel vorhanden")
                "Error 400, überprüfe ihre Verbindung" -> view.showError(message = "Fehler 400, überprüfe deine Verbindung")
                "test" -> view.showError(message = "Testfehler")
                "Netzwerkfehler" -> view.showError(message = "Netzwerkfehler: Verfügbarkeit des Zettelstores prüfen")

                else -> {
                    // Parsen der Daten als Liste von Zettel-Objekten und Anzeige in View
                    val zettelList = parseZettelListApi(responseData.toString())
                    view.showZettels(zettelList)
                }
            }
        }
    }
}


