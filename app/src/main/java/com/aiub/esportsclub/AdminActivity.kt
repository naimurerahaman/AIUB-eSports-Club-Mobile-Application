package com.aiub.esportsclub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Load AdminDashboardFragment as the first admin screen
        if (savedInstanceState == null) {
            loadFragment(AdminDashboardFragment(), addToBackStack = false)
        }
    }

    // Same loadFragment function but uses adminFragmentContainer
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.adminFragmentContainer, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}