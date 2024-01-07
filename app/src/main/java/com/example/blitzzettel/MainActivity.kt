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

    override fun navigateToSettings() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.action_HomeFragment_to_settingsFragment)
    }

    override fun showMessage(message: String) {
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

interface MainView {
    fun navigateToSettings()
    fun showPasswordDialogForSettings(passwort: String?)
    fun showMessage(message: String)
}
