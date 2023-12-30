package com.example.blitzzettel

import androidx.lifecycle.ViewModel

//Bearer-Token und IP werden in der Klasse gespeichert und vererbt

class SharedViewModel:ViewModel() {

    var BearerToken : String? = null
    var ServerIP : String = ""
}