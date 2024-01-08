package com.example.blitzzettel

import android.text.Editable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Der Presenter ist für die Logik und die Kommunikation zwischen View (Fragment) und Daten (z.B., SharedPreferences) verantwortlich.
class SettingsPresenter(
    private val view: SettingsView, // Die Instanz der SettingsFragment Klasse
    private val encryptedPrefsManager: EncryptedPrefsManager, // Klasse zur sicheren Speicherung von Einstellungen
    private val sharedViewModel: SharedViewModel // ViewModel für die gemeinsame Datenhaltung zwischen Fragmenten
) {

    // CoroutineScope für die Verwendung von Coroutines auf dem Haupt-Thread
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Wird aufgerufen, wenn die View (Fragment) erstellt wird
    fun onViewCreated() {
        // Laden der gespeicherten Anmeldeinformationen und Aktualisieren der Ansicht
        var (nutzername, passwort, serverid) = encryptedPrefsManager.getCredentials()
        view.setCredentials(nutzername.toString(), passwort.toString(), serverid.toString())
    }

    // Methode für die Aktualisierung von Anmeldeinformationen
    fun performUpdate(
        neuerNutzername: Editable?,
        neuesPasswort: Editable?,
        neuesPasswortWdhl: Editable?,
        neueServerId: Editable?
    ) {
        // Konvertierung der Editable-Objekte zu String
        val neuNutzername = neuerNutzername.toString()
        val neuPasswort = neuesPasswort.toString()
        val neuPasswortWdhl = neuesPasswortWdhl.toString()
        val neuServerId = neueServerId.toString()

        // Überprüfung auf leere Felder
        if (neuNutzername.isBlank() || neuPasswort.isBlank() || neuPasswortWdhl.isBlank()
            || neuServerId.isBlank()
        ) {
            view.showMessage(message = "Alle Felder müssen ausgefüllt sein")
            return
        }

        // Überprüfung, ob die eingegebenen Passwörter übereinstimmen
        if (neuPasswort != neuPasswortWdhl) {
            view.showMessage(message = "Passwörter stimmen nicht überein")
            return
        }

        // Änderung der Anmeldeinformationen (z.B., Neuer Token)
        changeCred(neuNutzername, neuPasswort, neuServerId)
    }

    // Zurücksetzen der automatischen Anmeldung (Autologin)
    fun resetAutoLogin() {
        encryptedPrefsManager.clearLoginData()
        // Logik zum Zurücksetzen der automatischen Anmeldung
        view.showMessage(message = "Autologin zurückgesetzt")
    }

    // Aktualisierung der Anmeldeinformationen durch Abrufen eines neuen Tokens
    fun changeCred(neuNutzername: String, neuPasswort: String, neuServerId: String) {
        val api = Api("", neuServerId)
        coroutineScope.launch {
            val token = withContext(Dispatchers.IO) {
                api.postGenerateToken(neuNutzername, neuPasswort)
            }
            withContext(Dispatchers.Main) {
                // Verarbeiten der API-Antwort
                processResponse(token, neuNutzername, neuPasswort, neuServerId)
            }
        }
    }

    // Verarbeitung der API-Antwort
    private fun processResponse(token: String?, neuerNutzername: String,
                                neuesPasswort: String, neueServerId: String) {
        when (token) {
            "Netzwerkfehler" -> view.showMessage(message = "Netzwerkfehler: Verfügbarkeit" +
                                                            " des Zettelstores prüfen")

            "400" -> view.showMessage(message = "Formulardaten ungültig")

            "401" -> view.showMessage(message = "Benutzer-ID oder Passwort falsch")

            "403" -> view.showMessage(message = "Authentifizierung nicht aktiv: ")

            "Unbekannter Fehler" -> view.showMessage(message = "Unbekannter Fehler")


            else -> {
                // Erfolgreiche Anmeldung
                encryptedPrefsManager.saveCredentials(neuerNutzername, neuesPasswort, neueServerId)
                view.showMessage(message = "Anmeldeinformationen aktualisiert")

                // Füge IP & Token in den temporären Speicher (SharedViewModel)
                sharedViewModel.ServerIP = neueServerId
                sharedViewModel.BearerToken = token

                // Weiterleitung zum HomeFragment
                view.navigateToHome()
            }
        }
    }
}