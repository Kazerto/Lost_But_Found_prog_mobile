package com.example.myapplicationlbf

import android.content.Intent
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.lang.System.exit
import kotlin.system.exitProcess

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    protected fun setupToolbar(title: String) {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Active le bouton à gauche
            setHomeAsUpIndicator(R.drawable.ic_menu) // Définit une icône spécifique pour le menu burger
            setTitle(title) // Définit le titre de l'interface
        }
    }



    protected fun setupDrawer(drawerLayout: DrawerLayout, navigationView: NavigationView) {
        this.drawerLayout = drawerLayout
        this.navigationView = navigationView

        // Configurer le NavigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItemSelected(menuItem)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun handleNavigationItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_report_found -> startActivity(Intent(this, LostItemActivity::class.java))
            R.id.nav_search_lost -> startActivity(Intent(this, LostItemRetrievalActivity::class.java))
            R.id.nav_search_own -> startActivity(Intent(this, LostItemOwnRetrievalActivity::class.java))
            R.id.nav_account -> startActivity(Intent(this, UserProfileActivity::class.java))
            R.id.nav_logout -> logout()
            R.id.nav_exit -> exit()
        }
    }

    private fun exit() {
        AlertDialog.Builder(this)
            .setTitle("Quitter l'application")
            .setMessage("Êtes-vous sûr de vouloir quitter ?")
            .setPositiveButton("Oui") { _, _ ->
                finishAffinity()
                System.exit(0)
            }
            .setNegativeButton("Non", null)
            .show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout?.let {
                    it.openDrawer(GravityCompat.START)
                    true
                } ?: run {
                    onBackPressed()
                    true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
    }

}
