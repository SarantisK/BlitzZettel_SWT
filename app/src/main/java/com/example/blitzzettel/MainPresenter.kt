package com.example.blitzzettel
import android.view.MenuItem

// Dieser Presenter ist verantwortlich für die Logik der MainActivity und verbindet die Benutzeroberfläche mit den Daten

class MainPresenter(private val view: MainView, private val encryptedPrefsManager: EncryptedPrefsManager) {

    // Diese Methode wird aufgerufen, wenn ein Optionen-Menüelement ausgewählt wurde
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Wenn das Einstellungs-Menüelement ausgewählt wurde
            R.id.settings -> {
                // Die gespeicherten Anmeldedaten (Username, Passwort, etc.) werden aus dem verschlüsselten PrefsManager abgerufen
                val (_, savedPassword, _) = encryptedPrefsManager.getCredentials()

                // Die Methode der MainView wird aufgerufen, um einen Dialog zur Passworteingabe für die Einstellungen anzuzeigen
                view.showPasswordDialogForSettings(savedPassword)
                true
            }
            // Wenn ein anderes Menüelement ausgewählt wurde
            else -> false
        }
    }

    // Diese Methode wird aufgerufen, um das eingegebene Passwort zu überprüfen
    fun validatePassword(enteredPassword: String, savedPassword: String?) {
        // Wenn das eingegebene Passwort mit dem gespeicherten Passwort übereinstimmt
        if (enteredPassword == savedPassword) {
            // Die Methode der MainView wird aufgerufen, um zur Einstellungsansicht zu navigieren
            view.navigateToSettings()
        } else {
            // Andernfalls wird eine Nachricht angezeigt, dass das Passwort falsch ist
            view.showMessage("Falsches Passwort")
        }
    }
}

