package com.example.blitzzettel

import android.content.Context
import android.content.SharedPreferences
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
    fun saveCredentials(username: String, password: String, server: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("server", server)
        editor.apply()
    }

    // Ruft die gespeicherten Anmeldeinformationen ab
    fun getCredentials(): Triple<String?, String?, String?> {
        val username = sharedPreferences.getString("username", null)
        val password = sharedPreferences.getString("password", null)
        val server = sharedPreferences.getString("server", null)
        return Triple(username, password, server)
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
            return sharedPreferences.contains("username") &&
                    sharedPreferences.contains("password") &&
                    sharedPreferences.contains("server")
        }
    }
}
