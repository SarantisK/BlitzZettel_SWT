package com.example.blitzzettel


// Datenklasse zur Darstellung der Zettel
data class Zettel(val id: String, val title: String) //val content: String

// Funktion zum Verarbeiten der empfangenen Daten
fun parseZettelListApi(responseData: String): List<Zettel> {
    val zettelList = mutableListOf<Zettel>()

    // Splitte die Zeilen in die Daten auf
    val lines = responseData.split("\n")

    // Iteriere über die Zeilen und erstelle Zettel-Objekte
    lines.forEach { line ->
        val parts = line.split(" ", limit = 2)
        if (parts.size == 2) {
            val id = parts[0]
            val title = parts[1]
            val zettel = Zettel(id, title)
            zettelList.add(zettel)
        }
    }

    return zettelList
}

// Funktion zum Verarbeiten der empfangenen Daten
fun parseZettelDataSendblitz(){

}

