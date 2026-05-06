package com.aiub.esportsclub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

// A Fragment extends Fragment class (not AppCompatActivity like before)
class HomeFragment : Fragment() {

    // ===== onCreateView =====
    // This is like onCreate() in an Activity
    // But instead of setContentView(), we INFLATE the layout and RETURN it
    // "inflate" means: build the layout from XML and return it as a View object
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate our fragment layout and return it
        // This tells Android: "use fragment_home.xml as this fragment's design"
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // ===== onViewCreated =====
    // This runs AFTER onCreateView — the layout is ready
    // We find views and set click listeners here
    // "view" parameter is the inflated layout returned by onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find buttons using view.findViewById (not just findViewById like in Activity)
        val btnEvents             = view.findViewById<Button>(R.id.btnEvents)
        val btnRegister           = view.findViewById<Button>(R.id.btnRegister)
        val btnPlayers            = view.findViewById<Button>(R.id.btnPlayers)
        val btnViewRegistrations  = view.findViewById<Button>(R.id.btnViewRegistrations)
        val btnLogout             = view.findViewById<Button>(R.id.btnLogout)

        // ===== NAVIGATE TO EVENTS FRAGMENT =====
        btnEvents.setOnClickListener {
            // Instead of Intent, we call loadFragment() on the host Activity
            // requireActivity() gives us the Activity this fragment is inside (MainActivity)
            // We cast it to MainActivity so we can call our custom loadFragment() function
            (requireActivity() as MainActivity).loadFragment(EventsFragment())
        }

        // ===== NAVIGATE TO REGISTRATION FRAGMENT =====
        btnRegister.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(RegistrationFragment())
        }

        // ===== NAVIGATE TO PLAYERS FRAGMENT =====
        btnPlayers.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(PlayersFragment())
        }

        // ===== NAVIGATE TO REGISTRATIONS LIST =====
        btnViewRegistrations.setOnClickListener {
            (requireActivity() as MainActivity).loadFragment(RegistrationsListFragment())
        }

        // ===== LOGOUT =====
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()

            // After logout, go back to LoginActivity (still an Activity)
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}