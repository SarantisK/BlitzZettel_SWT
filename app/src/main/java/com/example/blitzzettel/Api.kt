package com.example.blitzzettel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Api() {

    // Funktion weist der View Daten aus API zu
    suspend fun fetchDataAndDisplay(): String {
        val response = fetchDataFromAPI()
        return response
    }

    // Funktion für API Aufruf
    // superseded by API via OkHttp Package
    suspend fun fetchDataFromAPI(): String {
        val url = URL("https://10.0.2.2/z") //?q=tags%3A%23blitz&enc=plain
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val response = StringBuffer()

                while (inputStream.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                inputStream.close()

                connection.disconnect()

                response.toString()
            } else {
                "HTTP GET request failed: $responseCode"
            }
        } catch (e: Exception) {
            e.toString()
        } finally {
            connection.disconnect()
        }

    }

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



    suspend fun postGenerateToken(username:String, hashpw:String)
    {
        val client = OkHttpClient()
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)
        val request = Request.Builder()
            .url(String.format("http://127.0.0.1:23123/a?username=%s&password=%s",username,hashpw))
            .post(body)
            .build()
        val response = client.newCall(request).execute()

    }

    suspend fun renewToken(p_token:String)
    {
        val client = OkHttpClient()
        val mediaType = "text/plain".toMediaType()
        val body = "".toRequestBody(mediaType)
        val request = Request.Builder()
            .url(String.format("http://127.0.0.1:23123/a?Authorization=%s",p_token))
            .put(body)
            .build()
        val response = client.newCall(request).execute()

    }


    suspend fun getAllBlitzTagsApi(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:23123/z?q=tags%3A%23blitz") // Statt Localhost muss 10.0.2.2 Angegeben werden, im Emulator wird das als Localhost angesehen.
            .build()

        val response = client.newCall(request).execute()
        if (response.code == 200) {
            val temp = response.body?.string()

            return temp
        }
        if (response.code == 204) {
            return "Keine Zettel vorhanden"
        }
        if (response.code == 400) {
            return "Error 400, überprüfe ihre Verbindung"
        }
        return "test"
    }

    suspend fun getSpecificBlitzZettel(p_zID:String):String?
    {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(String.format("http://10.0.2.2:23123/z/%s",p_zID)) // Statt Localhost muss 10.0.2.2 Angegeben werden, im Emulator wird das als Localhost angesehen.
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



    suspend fun postZettelErstellen(p_titel: String, p_content: String): String {
        val client = OkHttpClient()
        val mediaType = "text/plain".toMediaType()
        val body = String.format(
            "title: %s\r\nrole: zettel\r\ntags: #blitz\r\nsyntax: zmk \r\nvisibility: login\r\n\r\n%s"
            , p_titel, p_content
        )

        val request = Request.Builder()
            .url("http://10.0.2.2:23123/z")
            .post(body.toRequestBody(mediaType))
            .addHeader("Content-Type", "text/plain")
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            // Überprüfen des Statuscodes und Rückgabe entsprechender Strings
            when (response.code) {
                201-> "Zettel erfolgreich erstellt"
                400 -> "Ungültige Anfrage"//Syntax-Error
                // Weitere Statuscodes hier behandeln
                else -> ""
            }
        }
    }

}


