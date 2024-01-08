package com.example.blitzzettel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePresenter(private val view: HomeView) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun onViewCreated(viewModel: SharedViewModel) {
        // Verwendung des SharedViewModels zur Verwaltung des Zustands und der Daten zwischen den Fragments

        // API-Instanz wird erstellt
        val api = Api(viewModel.BearerToken.toString(), viewModel.ServerIP)
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
                    // Parsen der Daten als Liste von Zettel-Objekten
                    val zettelList = parseZettelListApi(responseData.toString())
                    view.showZettels(zettelList)
                }
            }
        }
    }
}


