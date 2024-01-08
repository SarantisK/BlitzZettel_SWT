package com.example.blitzzettel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

import java.util.Base64


//Alle API-Aufrufe werden in dieser API-Klasse gespeichert

class Api(val p_token:String ="" , val ServerIP:String = "10.0.2.2:23123") {

    constructor() : this("", "")

    //Gibt als Test einfach mal alle Zettel
    suspend fun testingAPI(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:23123/z") // Statt Localhost muss 10.0.2.2 Angegeben werden, im Emulator wird das als Localhost angesehen.
            .build()

        val response = client.newCall(request).execute()


        val temp = response.body?.string()
        return temp
    }
    //Funktion, um einen Authentifizerungstoken anhand vom Usernamen
    //und Passwort zu generieren.
    fun postGenerateToken(username:String, pw:String):String? {
        try {
            val base64_string = Base64.getEncoder()
                .encodeToString(String.format("%s:%s", username, pw).toByteArray()) //Sorgt für Base64 Verschlüsselung
            val client = OkHttpClient()
            val mediaType = "text/plain".toMediaType()
            val body = "".toRequestBody(mediaType)
            val request = Request.Builder()
                .url(String.format("http://%s/a",ServerIP))
                .post(body)
                .addHeader("Authorization", String.format("Basic %s", base64_string))
                .build()
            val response = client.newCall(request).execute()

            if (response.code == 200) {
                val token_full =
                    response.body?.string() // Kompletter Token mit "" & Klammern & Dauer wie lange er anhält
                val token_short = token_full?.replace("(", "")
                    ?.replace("\"", "")?.replace(")", "")
                    ?.replace("600", "")
                // Token_short kürz den String auf den richtigen aufbau
                return token_short
            }
            return when (response.code) {
                400 -> "400" //"Form data invalid"
                401 -> "401" //"Benutzer-ID oder Passwort falsch"
                403 -> "403" //Authentifizierung nicht aktiv"
                else -> "Unbekannter Fehler"
            }
        } catch (e: Exception) {
            return "Netzwerkfehler"
        }
    }

        //Funktion, um Authentifizerungstoken zu erneuern
        suspend fun renewToken() {
            val client = OkHttpClient()
            val mediaType = "text/plain".toMediaType()
            val body = "".toRequestBody(mediaType)
            val request = Request.Builder()
                .url(String.format("http://%s/a"))
                .addHeader("Authorization", p_token)
                .put(body)
                .build()
            val response = client.newCall(request).execute()

        }

    //Funktion, um alle Zettel mit #blitz über die API aufzurufen
        suspend fun getAllBlitzTagsApi(): String? {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(String.format("http://%s/%s",ServerIP,"z?q=tags%3A%23blitz"))// Statt Localhost muss 10.0.2.2 Angegeben werden, im Emulator wird das als Localhost angesehen.
                .addHeader("Authorization", p_token)
                .build()

            val response = client.newCall(request).execute()
            if (response.code == 200) {
                return response.body?.string()
            }
            if (response.code == 204) {
                return "Keine Zettel vorhanden"
            }
            if (response.code == 400) {
                return "Error 400, überprüfe ihre Verbindung"
            }
            return "test"
        } catch (e: Exception) {
            return "Netzwerkfehler"
        }
    }
    //Funktion, um spezifischen Blitzzettel anhand der Zettel-ID über die API aufzurufen
        suspend fun getSpecificBlitzZettel(p_zID: String, ): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(
                    String.format("http://%s/z/%s",ServerIP,p_zID)) // Statt Localhost muss 10.0.2.2 Angegeben werden, im Emulator wird das als Localhost angesehen.
                .addHeader("Authorization", p_token)
                .build()


            val response = client.newCall(request).execute()
            if (response.code == 200) {
                ///Wenn Request funktioniert und Zettel Inhalt hat
                val temp = response.body?.string()

                return temp
            }
            if (response.code == 204) {
                /// Wenn Request funktioniert, aber kein Inhalt hat
                return ""
            }
            if (response.code == 400) {
                ///Falls keine Verbindung möglich, Zettel sollte immer vorhanden sein
                ///da wir die Werte aus getAllBlitzTagsAPI nehmen
                return "Error 400, überprüfe ihre Verbindung"
            }
            return "test"

        }

    //Funktion, um erstellte Blitzzettel in der App mittels API an den Zettelstore zu senden
        suspend fun postZettelErstellen(p_titel: String, p_content: String, ): String {
            val client = OkHttpClient()   //
            val mediaType = "text/plain".toMediaType()
            val body = String.format(
                "title: %s\r\nrole: zettel\r\ntags: #blitz\r\nsyntax: zmk \r\nvisibility: login\r\n\r\n%s",
                p_titel, //inhalt des Bodys
                p_content // Wird parsed in zmk Style
            )
            val request = Request.Builder()
                .url(String.format("http://%s/z",ServerIP)) // Aufbau der URl
                .post(body.toRequestBody(mediaType))
                .addHeader("Content-Type", "text/plain") //Wird aber gesendet als Plain
                .addHeader("Authorization", p_token) // Bearer Token für Auth.
                .build()

            return withContext(Dispatchers.IO) {
                val response = client.newCall(request).execute()

                // Überprüfen des Statuscodes und Rückgabe entsprechender Strings
                when (response.code) {
                    201 -> "Zettel erfolgreich erstellt"
                    400 -> "Ungültige Anfrage"//Syntax-Error
                    // Weitere Statuscodes hier behandeln
                    else -> "Netzwerkfehler"
                }
            }
        }
    }



