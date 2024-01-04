package com.example.blitzzettel

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


/**
 * EncryptedPrefsManager verwaltet die Speicherung und das Abrufen von Anmeldeinformationen
 * in einer verschlüsselten Form. Die Klasse nutzt EncryptedSharedPreferences, um Benutzernamen,
 * Passwörter und Server-IDs zu speichern.
 */

class EncryptedPrefsManager(context: Context) {
    private val prefsName = "my_prefs" // Name für verschlüsselte SharedPreferences

    // Masterkey-Alias für die Verschlüsselung
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // Verschlüsselte SharedPreferences-Instanz
    private val sharedPreferences: SharedPreferences

    init {
        // Im Konstruktor der Klasse wird die verschlüsselte SharedPreferences-Instanz erstellt.
        // Hier werden die Verschlüsselungseinstellungen definiert.
        sharedPreferences = EncryptedSharedPreferences.create(
            prefsName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Methode zum Speichern der Anmeldeinformationen (Benutzername, Passwort, Server-ID)
    // in den SharedPreferences
    fun saveCredentials(nutzername: String, passwort: String, serverID: String) {
        val editor = sharedPreferences.edit()
        editor.putString("nutzername", nutzername)
        editor.putString("passwort", passwort)
        editor.putString("serverID", serverID)
        editor.apply() // Speichert die Änderungen
    }

    fun clearLoginData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
        // or editor.commit() if you want to wait for the write to be complete
    }

    // Ruft die gespeicherten Anmeldeinformationen ab
    fun getCredentials(): Triple<String?, String?, String?> {
        val nutzername = sharedPreferences.getString("nutzername", null)
        val passwort = sharedPreferences.getString("passwort", null)
        val serverID = sharedPreferences.getString("serverID", null)
        return Triple(nutzername, passwort, serverID) // Gibt die Informationen als Triple zurück
    }
}

