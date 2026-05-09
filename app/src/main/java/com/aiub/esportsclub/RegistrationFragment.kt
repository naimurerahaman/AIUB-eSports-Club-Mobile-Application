package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationFragment : Fragment() {

    private lateinit var db  : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val layoutLoading = view.findViewById<LinearLayout>(R.id.layoutRegLoading)
        val layoutClosed  = view.findViewById<LinearLayout>(R.id.layoutRegClosed)
        val layoutOpen    = view.findViewById<ScrollView>(R.id.layoutRegOpen)

        // STEP 1: Check if registration is open or closed
        checkRegistrationStatus(layoutLoading, layoutClosed, layoutOpen, view)
    }

    // ===== STEP 1: CHECK REGISTRATION STATUS =====
    private fun checkRegistrationStatus(
        layoutLoading: LinearLayout,
        layoutClosed : LinearLayout,
        layoutOpen   : ScrollView,
        view         : View
    ) {
        layoutLoading.visibility = View.VISIBLE
        layoutClosed.visibility  = View.GONE
        layoutOpen.visibility    = View.GONE

        db.collection("settings")
            .document("registration")
            .get()
            .addOnSuccessListener { document ->

                val isOpen = document.getBoolean("isOpen") ?: false

                if (isOpen) {
                    // Registration is open — now check if user already registered
                    checkAlreadyRegistered(layoutLoading, layoutClosed, layoutOpen, view)
                } else {
                    layoutLoading.visibility = View.GONE
                    layoutClosed.visibility  = View.VISIBLE
                }
            }
            .addOnFailureListener {
                layoutLoading.visibility = View.GONE
                layoutClosed.visibility  = View.VISIBLE
            }
    }

    // ===== STEP 2: CHECK IF USER ALREADY REGISTERED =====
    // We search the registrations collection for a document
    // where userId matches the currently logged-in user
    // If found → user already registered → show message
    // If not found → user hasn't registered → show form
    private fun checkAlreadyRegistered(
        layoutLoading: LinearLayout,
        layoutClosed : LinearLayout,
        layoutOpen   : ScrollView,
        view         : View
    ) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            layoutLoading.visibility = View.GONE
            layoutClosed.visibility  = View.VISIBLE
            return
        }

        // .whereEqualTo("userId", userId) searches for documents
        // where the userId field equals the current user's ID
        // .limit(1) we only need to know if ONE exists — faster
        db.collection("registrations")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                layoutLoading.visibility = View.GONE

                if (!querySnapshot.isEmpty) {
                    // ===== ALREADY REGISTERED =====
                    // Show closed layout with custom "already registered" message
                    layoutClosed.visibility = View.VISIBLE
                    showAlreadyRegisteredMessage(view)

                } else {
                    // ===== NOT REGISTERED YET =====
                    // Show the registration form
                    layoutOpen.visibility = View.VISIBLE
                    setupForm(view)
                }
            }
            .addOnFailureListener { exception ->
                layoutLoading.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error checking registration: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                layoutClosed.visibility = View.VISIBLE
            }
    }

    // ===== SHOW ALREADY REGISTERED MESSAGE =====
    // Updates the closed layout to show a relevant message
    private fun showAlreadyRegisteredMessage(view: View) {
        val layoutClosed = view.findViewById<LinearLayout>(R.id.layoutRegClosed)

        // Update the title and message TextViews inside layoutRegClosed
        val tvTitle   = layoutClosed.getChildAt(1) as? TextView
        val tvMessage = layoutClosed.getChildAt(2) as? TextView

        tvTitle?.text   = "Already Registered!"
        tvMessage?.text = "You have already submitted a tournament registration.\n\nWe will contact you with further details. Good luck!"
    }

    // ===== SET UP FORM =====
    private fun setupForm(view: View) {

        val etName          = view.findViewById<EditText>(R.id.etRegName)
        val etStudentId     = view.findViewById<EditText>(R.id.etRegStudentId)
        val rgGender        = view.findViewById<RadioGroup>(R.id.rgGender)
        val etSuggestions   = view.findViewById<EditText>(R.id.etSuggestions)
        val btnSubmit       = view.findViewById<Button>(R.id.btnSubmitRegistration)
        val layoutSuccess   = view.findViewById<LinearLayout>(R.id.layoutRegSuccess)

        // Platform CheckBoxes
        val cbPlatformPC      = view.findViewById<CheckBox>(R.id.cbPlatformPC)
        val cbPlatformMobile  = view.findViewById<CheckBox>(R.id.cbPlatformMobile)
        val cbPlatformConsole = view.findViewById<CheckBox>(R.id.cbPlatformConsole)

        // Game CheckBoxes
        val cbValorant        = view.findViewById<CheckBox>(R.id.cbValorant)
        val cbPUBG            = view.findViewById<CheckBox>(R.id.cbPUBG)
        val cbMLBB            = view.findViewById<CheckBox>(R.id.cbMLBB)
        val cbEAFC            = view.findViewById<CheckBox>(R.id.cbEAFC)
        val cbCS2             = view.findViewById<CheckBox>(R.id.cbCS2)
        val cbEFootball       = view.findViewById<CheckBox>(R.id.cbEFootball)
        val cbStreetFighter   = view.findViewById<CheckBox>(R.id.cbStreetFighter)
        val cbOther           = view.findViewById<CheckBox>(R.id.cbOther)

        // Format CheckBoxes
        val cbFormatSolo  = view.findViewById<CheckBox>(R.id.cbFormatSolo)
        val cbFormatDuo   = view.findViewById<CheckBox>(R.id.cbFormatDuo)
        val cbFormatSquad = view.findViewById<CheckBox>(R.id.cbFormatSquad)

        // Back button
        val btnBack = view.findViewById<Button>(R.id.btnBackRegistration)
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSubmit.setOnClickListener {

            val name      = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (studentId.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your Student ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val genderId = rgGender.checkedRadioButtonId
            if (genderId == -1) {
                Toast.makeText(requireContext(), "Please select your gender!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val gender = when (genderId) {
                R.id.rbMale   -> "Male"
                R.id.rbFemale -> "Female"
                else          -> ""
            }

            val selectedPlatforms = mutableListOf<String>()
            if (cbPlatformPC.isChecked)      selectedPlatforms.add("PC")
            if (cbPlatformMobile.isChecked)  selectedPlatforms.add("Mobile")
            if (cbPlatformConsole.isChecked) selectedPlatforms.add("Console")

            if (selectedPlatforms.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one platform!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedGames = mutableListOf<String>()
            if (cbValorant.isChecked)      selectedGames.add("Valorant")
            if (cbPUBG.isChecked)          selectedGames.add("PUBG")
            if (cbMLBB.isChecked)          selectedGames.add("MLBB")
            if (cbEAFC.isChecked)          selectedGames.add("EA FC")
            if (cbCS2.isChecked)           selectedGames.add("CS2")
            if (cbEFootball.isChecked)     selectedGames.add("eFootball")
            if (cbStreetFighter.isChecked) selectedGames.add("Street Fighter")
            if (cbOther.isChecked)         selectedGames.add("Other")

            if (selectedGames.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one game!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedFormats = mutableListOf<String>()
            if (cbFormatSolo.isChecked)  selectedFormats.add("Solo")
            if (cbFormatDuo.isChecked)   selectedFormats.add("Duo")
            if (cbFormatSquad.isChecked) selectedFormats.add("Squad")

            if (selectedFormats.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val suggestions = etSuggestions.text.toString().trim()

            val registrationData = hashMapOf(
                "name"        to name,
                "studentId"   to studentId,
                "gender"      to gender,
                "platforms"   to selectedPlatforms,
                "games"       to selectedGames,
                "formats"     to selectedFormats,
                "suggestions" to suggestions,
                "userId"      to (auth.currentUser?.uid   ?: "unknown"),
                "userEmail"   to (auth.currentUser?.email ?: "unknown"),
                "timestamp"   to System.currentTimeMillis()
            )

            btnSubmit.isEnabled = false
            btnSubmit.text      = "Submitting..."

            db.collection("registrations")
                .add(registrationData)
                .addOnSuccessListener {
                    btnSubmit.isEnabled      = true
                    btnSubmit.text           = "Submit Registration"
                    btnSubmit.visibility     = View.GONE
                    layoutSuccess.visibility = View.VISIBLE

                    Toast.makeText(
                        requireContext(),
                        "Registration submitted! ✅",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { exception ->
                    btnSubmit.isEnabled = true
                    btnSubmit.text      = "Submit Registration"
                    Toast.makeText(
                        requireContext(),
                        "Error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}