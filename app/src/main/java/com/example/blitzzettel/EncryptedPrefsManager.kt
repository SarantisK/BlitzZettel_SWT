package com.example.blitzzettel

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


class EncryptedPrefsManager(context: Context) {
    private val prefsName = "my_prefs" // Name für verschlüsselte SharedPreferences
    //private val keyAlias = "my_key_alias"

    // Masterkey-Alias für die Verschlüsselung
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    // Verschlüsselte SharedPreferences-Instanz
    private val sharedPreferences: SharedPreferences

    init {
        // Erstellung der EncryptedSharedPreferences-Instanz
        sharedPreferences = EncryptedSharedPreferences.create(
            prefsName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Speichert die Anmeldeinformationen (Benutzername, Passwort, Server)
    fun saveCredentials(nutzername: String, passwort: String, serverID: String) {
        val editor = sharedPreferences.edit()
        editor.putString("nutzername", nutzername)
        editor.putString("passwort", passwort)
        editor.putString("serverID", serverID)
        editor.apply()
    }


    // Ruft die gespeicherten Anmeldeinformationen ab
    fun getCredentials(): Triple<String?, String?, String?> {
        val nutzername = sharedPreferences.getString("nutzername", null)
        val passwort = sharedPreferences.getString("passwort", null)
        val serverID = sharedPreferences.getString("serverID", null)
        return Triple(nutzername, passwort, serverID)
    }

    fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("nutzername")
        editor.remove("passwort")
        editor.remove("serverID")
        editor.apply()
    }


    companion object {
        private val prefsName = "my_prefs" // Name für verschlüsselte SharedPreferences

        // Überprüft, ob Anmeldeinformationen vorhanden sind
        fun hasCredentials(context: Context): Boolean {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPreferences = EncryptedSharedPreferences.create(
                prefsName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return sharedPreferences.contains("nutzername") &&
                    sharedPreferences.contains("passwort") &&
                    sharedPreferences.contains("serverID")
        }
    }
}

