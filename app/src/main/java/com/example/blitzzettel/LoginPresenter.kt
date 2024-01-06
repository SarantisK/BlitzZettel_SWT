package com.example.blitzzettel

import android.text.Editable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Initialisiert und verwaltet den Anmeldevorgang in Verbindung mit der Benutzeroberfläche und Authentifizierungsdetails.
class LoginPresenter(private val view: LoginView, private val encryptedPrefsManager: EncryptedPrefsManager, private val sharedViewModel: SharedViewModel) {
    // Erstellt einen CoroutineScope, der für die Ausführung von Aufgaben im Haupt-Dispatcher verwendet wird.
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Wird aufgerufen, wenn die zugehörige Ansicht erstellt wird. Prüft und füllt Anmeldeinformationen automatisch aus.
    fun onViewCreated() {
        val (nutzername, passwort, serverId) = encryptedPrefsManager.getCredentials()

        if (!nutzername.isNullOrEmpty() && !passwort.isNullOrEmpty() && !serverId.isNullOrEmpty()) {
            view.autoFillCredentials(nutzername, serverId)
        }
    }

    // Führt den Anmeldevorgang durch, überprüft die Gültigkeit der Eingaben und authentifiziert sich asynchron im Hintergrund.
    fun performLogin(nutzername: Editable?, passwort: Editable?, serverId: Editable?) {
        val nutzername = nutzername.toString()
        val passwort = passwort.toString()
        val serverId = serverId.toString()

        if (nutzername.isBlank() || passwort.isBlank() || serverId.isBlank()) {
            view.showErrorMessage("Alle Felder ausfüllen")
        }

        val api = Api("", serverId)

        coroutineScope.launch {
            val token = withContext(Dispatchers.IO) {
                // Führt einen API-Aufruf zur Authentifizierung durch.
                api.postGenerateToken(nutzername, passwort)
            }

            withContext(Dispatchers.Main) {
                // Verarbeitet die Antwort des Authentifizierungsvorgangs.
                processLoginResponse(token, nutzername, passwort, serverId)
            }
        }
    }

    // Verarbeitet die verschiedenen Antwortmöglichkeiten nach der Authentifizierung und führt entsprechende Aktionen aus.
    private fun processLoginResponse(token: String?, nutzername: String, passwort: String, serverId: String) {
        when (token) {
            "Netzwerkfehler" -> view.showErrorMessage("Netzwerkfehler: Verfügbarkeit des Zettelstores prüfen")
            "400" -> view.showErrorMessage("Formulardaten ungültig")
            "401" -> view.showErrorMessage("Benutzer-ID oder Passwort falsch")
            "403" -> view.showErrorMessage("Authentifizierung nicht aktiv")
            "Unbekannter Fehler" -> view.showErrorMessage("Unbekannter Fehler")
            else -> {
                // Erfolgreiche Authentifizierung
                view.showErrorMessage("Erfolgreich angemeldet")
                view.navigateToHome()

                // Speichert Token, ServerId, Nutzername und Passwort.
                sharedViewModel.ServerIP = serverId
                sharedViewModel.BearerToken = token
                encryptedPrefsManager.saveCredentials(nutzername, passwort, serverId)
                // Zeigt eine Erfolgsmeldung an.
                view.showErrorMessage("Erfolgreich Angemeldet")
            }
        }
    }
}