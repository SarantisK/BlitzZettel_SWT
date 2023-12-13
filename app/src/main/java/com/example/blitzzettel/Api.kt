package com.example.blitzzettel
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

    suspend fun postGenerateToken()
    {

    }
    suspend fun renewToken(p_token:String)
    {

    }

    suspend fun getAllBlitzTagsApi(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:23123/z?q=tags%3A%23blitz.") // Statt Localhost muss 10.0.2.2 Angegeben werden, im Emulator wird das als Localhost angesehen.
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
    suspend fun getSpecificBlitzZettel(p_zID:String):String
    {



        return ""
    }


    suspend fun postZettelErstellen(p_titel:String,p_content:String)
    {
        val client = OkHttpClient()
        val mediaType = "text/plain".toMediaType()
        val body = String.format("title: %s\r\nrole: zettel\r\ntags: #blitz\r\nsyntax: zmk \r\nvisibility: login\r\n\r\n%s",p_titel,p_content) // Body wird in zettelmarkup erstellt
        val request = Request.Builder()
            .url("http://10.0.2.2:23123/z") //bugfix hier, war localhost soll 10.0.2.2
            .post(body.toRequestBody(mediaType))
            .addHeader("Content-Type", "text/plain")
            .build()
        val response = client.newCall(request).execute()
    }
}

