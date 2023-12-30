package com.example.blitzzettel

import android.app.AlertDialog
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.example.blitzzettel.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

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

        //binding.addButton.setOnClickListener { view ->
        //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //    .setAction("Action", null).show()
        // }
    }
    //Hinzufügen von Elementen zur Action Bar, falls vorhanden
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    //Beim Anklicken des Einstellungsicons wird das gespeicherte Passwort
    //aufgerufen und in der Funktion showPasswordDialogForSettings als Parameter
    //gespeichert
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val (_, savedPassword, _) = encryptedPrefsManager.getCredentials()
                showPasswordDialogForSettings(savedPassword)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private val encryptedPrefsManager by lazy { EncryptedPrefsManager(this) }


    //Vergleicht eingegebenes Passwort mit gespeichertem Passwort.
    //Wenn gleich wird man auf die Einstellungen weitergeleitet, wenn nicht
    //erscheint eine Toast-Message.
    fun showPasswordDialogForSettings(passwort: String?) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_password, null)
        builder.setView(dialogLayout)
        builder.setTitle("Passwort eingeben um zu den Einstellungen zu gelangen.")

        builder.setPositiveButton("Bestätigen") { _, _ ->
            val enteredPassword = dialogLayout.findViewById<EditText>(R.id.EditTextPassword).text.toString()
            if (enteredPassword == passwort) {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.action_HomeFragment_to_settingsFragment)
            } else {
                Toast.makeText(this, "Falsches Passwort", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.cancel()
        }

        builder.setCancelable(false)
        builder.show()
    }
}

