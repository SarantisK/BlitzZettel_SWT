package com.example.blitzzettel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch


class SecondPresenter (private val view: SecondView) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    fun onViewCreated(zettlid: String?, viewModel: SharedViewModel) {

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



