package com.example.blitzzettel

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.example.blitzzettel.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity(), MainView {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Verknüpfung der Binding-Klasse mit dem Layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar als Action Bar setzen
        setSupportActionBar(binding.toolbar)

        //Konfiguration der App-Leiste
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        appBarConfiguration = AppBarConfiguration(setOf())
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter = MainPresenter(this, EncryptedPrefsManager(this))
    }
    //Hinzufügen von Elementen zur Action Bar, falls vorhanden
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return presenter.onOptionsItemSelected(item)
    }

    // Diese Funktion wird aufgerufen, um zur Einstellungsansicht zu navigieren
    override fun navigateToSettings() {
        // Das NavHostFragment wird aus dem Fragment-Manager abgerufen, der das Haupt-Navigationsfragment enthält
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        // Der NavController wird aus dem NavHostFragment abgerufen
        val navController = navHostFragment.navController

        // Mit dem NavController wird die Navigation zur Einstellungsansicht ausgelöst
        navController.navigate(R.id.action_HomeFragment_to_settingsFragment)
    }

    // Diese Funktion zeigt eine Toast-Nachricht mit dem übergebenen Text an
    override fun showMessage(message: String) {
        // Ein Toast-Objekt wird erstellt und angezeigt, um die Nachricht kurzzeitig anzuzeigen
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }




    //Vergleicht eingegebenes Passwort mit gespeichertem Passwort.
    //Wenn gleich wird man auf die Einstellungen weitergeleitet, wenn nicht
    //erscheint eine Toast-Message.
    override fun showPasswordDialogForSettings(passwort: String?) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_password, null)
        builder.setView(dialogLayout)
        builder.setTitle("Passwort eingeben um zu den Einstellungen zu gelangen.")

        builder.setPositiveButton("Bestätigen") { _, _ ->
            val enteredPassword = dialogLayout.findViewById<EditText>(R.id.EditTextPassword).text.toString()
            presenter.validatePassword(enteredPassword, passwort)
        }

        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.cancel()
        }

        builder.setCancelable(false)
        builder.show()
    }
}

// Dieses Interface definiert die Methoden, die von der MainActivity implementiert werden müssen, um die Benutzeroberfläche zu aktualisieren

interface MainView {
    // Diese Methode wird aufgerufen, um zur Einstellungsansicht zu navigieren
    fun navigateToSettings()

    // Diese Methode wird aufgerufen, um einen Dialog zur Eingabe des Passworts für die Einstellungen anzuzeigen
    // Das optionale 'passwort' Argument ermöglicht das Vorhandensein eines vorherigen Passworts
    fun showPasswordDialogForSettings(password: String?)

    // Diese Methode wird aufgerufen, um eine allgemeine Nachricht anzuzeigen, z. B. mit Toast
    fun showMessage(message: String)
}

