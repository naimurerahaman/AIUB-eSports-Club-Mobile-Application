package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemberRegistrationFragment : Fragment() {

    private lateinit var db  : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_member_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val layoutLoading = view.findViewById<LinearLayout>(R.id.layoutMemberLoading)
        val layoutClosed  = view.findViewById<LinearLayout>(R.id.layoutMemberClosed)
        val layoutOpen    = view.findViewById<ScrollView>(R.id.layoutMemberOpen)

        // ===== STEP 1: Check recruitment status first =====
        checkRecruitmentStatus(layoutLoading, layoutClosed, layoutOpen, view)
    }

    // ===== CHECK RECRUITMENT STATUS =====
    private fun checkRecruitmentStatus(
        layoutLoading: LinearLayout,
        layoutClosed : LinearLayout,
        layoutOpen   : ScrollView,
        view         : View
    ) {
        layoutLoading.visibility = View.VISIBLE
        layoutClosed.visibility  = View.GONE
        layoutOpen.visibility    = View.GONE

        db.collection("recruitmentStatus")
            .document("membership")
            .get()
            .addOnSuccessListener { document ->

                val isOpen = document.getBoolean("isOpen") ?: false

                if (isOpen) {
                    // Recruitment is open — now check if user already applied
                    checkAlreadyApplied(layoutLoading, layoutClosed, layoutOpen, view)
                } else {
                    // Recruitment is closed
                    layoutLoading.visibility = View.GONE
                    layoutClosed.visibility  = View.VISIBLE
                }
            }
            .addOnFailureListener {
                layoutLoading.visibility = View.GONE
                layoutClosed.visibility  = View.VISIBLE
            }
    }

    // ===== CHECK IF USER ALREADY APPLIED =====
    // We search Firestore for any document where userId == current user's ID
    // If found → user already applied → show message
    // If not found → user hasn't applied yet → show form
    private fun checkAlreadyApplied(
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

        // Query Firestore for any application with this userId
        // .whereEqualTo("userId", userId) filters documents where userId matches
        // .limit(1) we only need to find one — no need to fetch all
        db.collection("memberApplications")
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                layoutLoading.visibility = View.GONE

                if (!querySnapshot.isEmpty) {
                    // ===== USER ALREADY APPLIED =====
                    // Get the status of their existing application
                    val existingDoc = querySnapshot.documents[0]
                    val status = existingDoc.getString("status") ?: "pending"

                    // Show the closed layout but change the message
                    layoutClosed.visibility = View.VISIBLE
                    showAlreadyAppliedMessage(view, status)

                } else {
                    // ===== USER HAS NOT APPLIED YET =====
                    // Show the form normally
                    layoutOpen.visibility = View.VISIBLE
                    setupForm(view)
                }
            }
            .addOnFailureListener { exception ->
                layoutLoading.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error checking application: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
                // Default to closed on error
                layoutClosed.visibility = View.VISIBLE
            }
    }

    // ===== SHOW ALREADY APPLIED MESSAGE =====
    // Updates the closed layout text to show a relevant message
    private fun showAlreadyAppliedMessage(view: View, status: String) {
        val layoutClosed = view.findViewById<LinearLayout>(R.id.layoutMemberClosed)

        // Find the TextViews inside the closed layout
        // We update them to show the "already applied" message
        val tvTitle   = layoutClosed.getChildAt(1) as? TextView
        val tvMessage = layoutClosed.getChildAt(2) as? TextView

        tvTitle?.text = when (status) {
            "approved" -> "You Are a Member!"
            "pending"  -> "Application Submitted"
            else       -> "Application Reviewed"
        }

        tvMessage?.text = when (status) {
            "approved" ->
                "Congratulations! Your AESC membership application has been approved. Welcome to the club!"
            "pending"  ->
                "You have already submitted a membership application.\n\nYour application is currently under review. We will contact you soon!"
            else       ->
                "Your application has been reviewed by the admin. Please contact the club for more information."
        }
    }

    // ===== SET UP FORM =====
    private fun setupForm(view: View) {

        val etName        = view.findViewById<EditText>(R.id.etMemberName)
        val etStudentId   = view.findViewById<EditText>(R.id.etMemberStudentId)
        val spinnerDept   = view.findViewById<Spinner>(R.id.spinnerDepartment)
        val spinnerRole   = view.findViewById<Spinner>(R.id.spinnerRole)
        val btnSubmit     = view.findViewById<Button>(R.id.btnSubmitMembership)
        val layoutSuccess = view.findViewById<LinearLayout>(R.id.layoutMemberSuccess)

        val cbPC          = view.findViewById<CheckBox>(R.id.cbPlatformPC)
        val cbMobile      = view.findViewById<CheckBox>(R.id.cbPlatformMobile)
        val cbValorant    = view.findViewById<CheckBox>(R.id.cbGameValorant)
        val cbPUBGM       = view.findViewById<CheckBox>(R.id.cbGamePUBGM)
        val cbFreeFire    = view.findViewById<CheckBox>(R.id.cbGameFreeFire)
        val cbEFootball   = view.findViewById<CheckBox>(R.id.cbGameEFootball)
        val cbMLBB        = view.findViewById<CheckBox>(R.id.cbGameMLBB)
        val cbCS          = view.findViewById<CheckBox>(R.id.cbGameCS)
        val cbEAFC        = view.findViewById<CheckBox>(R.id.cbGameEAFC)

        // Department Spinner
        val departments = listOf("Select Department", "FST", "FASS", "FBA", "FE", "FHLS")
        val deptAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            departments
        )
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDept.adapter = deptAdapter

        // Role Spinner
        val roles = listOf(
            "Select Role", "Player", "Organizer", "Management",
            "Production", "Streamer", "Representative", "Caster", "Coordinator"
        )
        val roleAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles
        )
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = roleAdapter

        btnSubmit.setOnClickListener {

            val name      = etName.text.toString().trim()
            val studentId = etStudentId.text.toString().trim()
            val dept      = spinnerDept.selectedItem.toString()
            val role      = spinnerRole.selectedItem.toString()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (studentId.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your Student ID!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dept == "Select Department") {
                Toast.makeText(requireContext(), "Please select your department!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (role == "Select Role") {
                Toast.makeText(requireContext(), "Please select your role!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val platforms = mutableListOf<String>()
            if (cbPC.isChecked)     platforms.add("PC")
            if (cbMobile.isChecked) platforms.add("Mobile")

            if (platforms.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one platform!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val games = mutableListOf<String>()
            if (cbValorant.isChecked)  games.add("Valorant")
            if (cbPUBGM.isChecked)     games.add("PUBGM")
            if (cbFreeFire.isChecked)  games.add("FreeFire")
            if (cbEFootball.isChecked) games.add("eFootball")
            if (cbMLBB.isChecked)      games.add("MLBB")
            if (cbCS.isChecked)        games.add("CS")
            if (cbEAFC.isChecked)      games.add("EAFC")

            if (games.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one game!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSubmit.isEnabled = false
            btnSubmit.text      = "Submitting..."

            val memberData = hashMapOf(
                "name"       to name,
                "studentId"  to studentId,
                "department" to dept,
                "platforms"  to platforms,
                "games"      to games,
                "role"       to role,
                "userId"     to (auth.currentUser?.uid   ?: "unknown"),
                "userEmail"  to (auth.currentUser?.email ?: "unknown"),
                "status"     to "pending",
                "timestamp"  to System.currentTimeMillis()
            )

            db.collection("memberApplications")
                .add(memberData)
                .addOnSuccessListener {
                    btnSubmit.isEnabled      = true
                    btnSubmit.text           = "Submit Application"
                    btnSubmit.visibility     = View.GONE
                    layoutSuccess.visibility = View.VISIBLE

                    Toast.makeText(
                        requireContext(),
                        "Application submitted! ✅",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { exception ->
                    btnSubmit.isEnabled = true
                    btnSubmit.text      = "Submit Application"
                    Toast.makeText(
                        requireContext(),
                        "Error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}