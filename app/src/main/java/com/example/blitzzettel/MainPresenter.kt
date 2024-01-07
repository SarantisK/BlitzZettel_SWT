package com.example.blitzzettel
import android.view.MenuItem

class MainPresenter (private val view: MainView, private val encryptedPrefsManager: EncryptedPrefsManager) {

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val (_, savedPassword, _) = encryptedPrefsManager.getCredentials()
                view.showPasswordDialogForSettings(savedPassword)
                true
            }
            else -> false
        }
    }

    fun validatePassword(enteredPassword: String, savedPassword: String?) {
        if (enteredPassword == savedPassword) {
            view.navigateToSettings()
        } else {
            view.showMessage("Falsches Passwort")
        }
    }


}
