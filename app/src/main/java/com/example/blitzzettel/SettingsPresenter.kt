package com.example.blitzzettel

import android.text.Editable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Konstruktor mit Fragment & Perm. + Temp. Speicherklassen
class SettingsPresenter
    (
    private val view: SettingsView, // Bekommt die Instanz der SettingsFragment Klasse
    private val encryptedPrefsManager: EncryptedPrefsManager,
    private val sharedViewModel: SharedViewModel
){

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    fun onViewCreated() {
        var (nutzername, passwort, serverid) = encryptedPrefsManager.getCredentials()
        view.setCredentials(nutzername.toString(), passwort.toString(), serverid.toString())
    }
    fun performUpdate(
        neuerNutzername: Editable?,
        neuesPasswort: Editable?,
        neuesPasswortWdhl: Editable?,
        neueServerId: Editable?
    ) {

        val neuNutzername = neuerNutzername.toString()
        val neuPasswort = neuesPasswort.toString()
        val neuPasswortWdhl = neuesPasswortWdhl.toString()
        val neuServerId = neueServerId.toString()

        if (neuNutzername.isBlank() || neuPasswort.isBlank() || neuPasswortWdhl.isBlank()
            || neuServerId.isBlank()
        ) {
            view.showMessage(message = "Alle Felder müssen ausgefüllt sein")
            return
        }
        if (neuPasswort != neuPasswortWdhl) {
            view.showMessage(message = "Passwörter stimmen nicht überein")
            return
        }
        changeCred(neuNutzername, neuPasswort, neuServerId) //Neuer Token etc...
    }

    //Autologin Reset
    fun resetAutoLogin() {
        encryptedPrefsManager.clearLoginData()
        // Logik zum Zurücksetzen der automatischen Anmeldung
        view.showMessage(message = "Autologin zurückgesetzt")
    }

    //Holt sich mit den neuen Daten ein Neues Token
    fun changeCred(neuNutzername: String, neuPasswort: String, neuServerId: String){
        val api = Api("", neuServerId)
        coroutineScope.launch {
            val token = withContext(Dispatchers.IO) {
                api.postGenerateToken(neuNutzername, neuPasswort)
            }
            withContext(Dispatchers.Main) {
                processResponse(token, neuNutzername, neuPasswort, neuServerId)
            }
        }
    }


    //Rückmeldung bearbeiten
    private fun processResponse(token: String?,neuerNutzername: String,
                                neuesPasswort: String, neueServerId: String)
    {
        when (token) {
            "Netzwerkfehler", "400", "401", "403", "Unbekannter Fehler" -> {
                view.showMessage(message = token)
            }

            else -> {
                // Erfolgreich angemeldet
                encryptedPrefsManager.saveCredentials(neuerNutzername, neuesPasswort, neueServerId)
                view.showMessage(message = "Anmeldeinformationen aktualisiert")

                //Füge IP & Token in Temporären Speicher
                sharedViewModel.ServerIP = neueServerId
                sharedViewModel.BearerToken = token

                // Auf HomeFragment weiterleiten
                view.navigateToHome()
            }
        }
    }
}