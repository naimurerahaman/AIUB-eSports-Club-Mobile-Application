package com.aiub.esportsclub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load HomeFragment as the very first screen when app opens
        // savedInstanceState == null means the app is freshly opened (not rotated)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), addToBackStack = false)
            // addToBackStack = false for HomeFragment because we don't want
            // the user to press Back and go to a blank screen
        }
    }

    // ===== THIS IS THE MAIN NAVIGATION FUNCTION =====
    // Every fragment calls this function to navigate to another fragment
    // It replaces whatever is currently shown with the new fragment
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

        // replace() swaps the current fragment with the new one
        // R.id.fragmentContainer is the FrameLayout in activity_main.xml
        transaction.replace(R.id.fragmentContainer, fragment)

        // addToBackStack means: remember this screen in history
        // so when user presses Back, it goes to the previous fragment
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit() // Apply the transaction
    }
}