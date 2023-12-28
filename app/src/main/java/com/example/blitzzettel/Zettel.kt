package com.example.blitzzettel


/**
 * Zettel ist eine Datenklasse, die einen Zettel repräsentiert.
 * Sie hält die ID und den Titel des Zettels.
 */
data class Zettel(val id: String, val title: String) //val content: String

// Funktion zum Verarbeiten der empfangenen Daten und wandelt sie in eine Liste von Zettel-Objekten um
fun parseZettelListApi(responseData: String): List<Zettel> {
    val zettelList = mutableListOf<Zettel>() // EIne Liste aus der Klasse Zettel

    // Splitte die Zeilen in die Daten auf in einer Liste
    val lines = responseData.split("\n")

    // Iteriere über die Zeilen und erstelle Zettel-Objekte
    lines.forEach { line ->
        val parts = line.split(" ", limit = 2) //Spaltet den responsestring der API in Titel & ID auf
        if (parts.size == 2) {
            val id = parts[0]
            val title = parts[1]
            val zettel = Zettel(id, title)
            zettelList.add(zettel)
        }
    }

    return zettelList
}


