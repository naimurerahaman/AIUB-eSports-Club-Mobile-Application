package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout   : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var toolbar        : Toolbar
    private lateinit var auth           : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth           = FirebaseAuth.getInstance()
        drawerLayout   = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar        = findViewById(R.id.toolbar)

        // ===== STEP 1: SET TOOLBAR =====
        // This replaces the default action bar with our custom toolbar
        setSupportActionBar(toolbar)

        // ===== STEP 2: SET UP HAMBURGER TOGGLE =====
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // ===== STEP 3: SIDEBAR ITEM CLICK LISTENER =====
        navigationView.setNavigationItemSelectedListener(this)

        // ===== STEP 4: SHOW USER INFO IN HEADER =====
        updateDrawerHeader()

        // ===== STEP 5: LOAD HOME FRAGMENT =====
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), addToBackStack = false)
            // Highlight the first item in sidebar as selected
            navigationView.setCheckedItem(R.id.nav_profile)
        }

        // ===== STEP 6: BACK BUTTON HANDLER =====
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }

    // ===== SHOW USER EMAIL IN SIDEBAR HEADER =====
    private fun updateDrawerHeader() {
        val headerView    = navigationView.getHeaderView(0)
        val tvName        = headerView.findViewById<TextView>(R.id.tvNavUserName)
        val tvEmail       = headerView.findViewById<TextView>(R.id.tvNavUserEmail)
        val tvAvatar      = headerView.findViewById<TextView>(R.id.tvNavAvatar)
        val currentUser   = auth.currentUser

        if (currentUser != null) {
            val email   = currentUser.email ?: "No email"
            tvEmail.text  = email
            tvAvatar.text = email.first().uppercaseChar().toString()
            val name      = currentUser.displayName
            tvName.text   = if (!name.isNullOrEmpty()) name
            else email.substringBefore("@")
        }
    }

    // ===== HANDLE SIDEBAR ITEM CLICKS =====
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                loadFragment(ProfileFragment())
                toolbar.title = "My Profile"
            }
            R.id.nav_events -> {
                loadFragment(EventsFragment())
                toolbar.title = "Events"
            }
            R.id.nav_register -> {
                loadFragment(RegistrationFragment())
                toolbar.title = "Register"
            }
            R.id.nav_players -> {
                loadFragment(PlayersFragment())
                toolbar.title = "Players"
            }
            R.id.nav_registrations -> {
                loadFragment(RegistrationsListFragment())
                toolbar.title = "Registrations"
            }
            R.id.nav_logout -> {
                auth.signOut()
                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // ===== LOAD FRAGMENT =====
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}