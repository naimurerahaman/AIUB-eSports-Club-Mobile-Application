package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.EmailAuthProvider

class ProfileFragment : Fragment() {

    // ===== DECLARE VARIABLES =====
    // "lateinit" means we will assign these inside onViewCreated
    private lateinit var db   : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    // This will track if the user already has a saved profile
    // true  = profile exists → show Update button
    // false = no profile yet → show Save button
    private var profileExists = false

    // ===== STEP 1: onCreateView =====
    // This function builds the Fragment's layout from XML
    // "inflater.inflate" means: read the XML file and build the views from it
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    // ===== STEP 2: onViewCreated =====
    // This runs AFTER the layout is ready
    // We find views and write all our logic here
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== CONNECT TO FIREBASE =====
        db   = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // ===== FIND ALL VIEWS =====
        // We use view.findViewById because we are inside a Fragment
        // In an Activity we just use findViewById — the difference is "view."
        val btnBack          = view.findViewById<Button>(R.id.btnBackProfile)
        val tvAvatar         = view.findViewById<TextView>(R.id.tvProfileAvatar)
        val tvEmail          = view.findViewById<TextView>(R.id.tvProfileEmail)
        val etName           = view.findViewById<EditText>(R.id.etProfileName)
        val etDepartment     = view.findViewById<EditText>(R.id.etProfileDepartment)
        val etAge            = view.findViewById<EditText>(R.id.etProfileAge)
        val etSemester       = view.findViewById<EditText>(R.id.etProfileSemester)
        val cbValorant       = view.findViewById<CheckBox>(R.id.cbValorant)
        val cbPUBG           = view.findViewById<CheckBox>(R.id.cbPUBG)
        val cbFIFA           = view.findViewById<CheckBox>(R.id.cbFIFA)
        val cbFreeFire       = view.findViewById<CheckBox>(R.id.cbFreeFire)
        val cbCS2            = view.findViewById<CheckBox>(R.id.cbCS2)
        val btnSave          = view.findViewById<Button>(R.id.btnSaveProfile)
        val btnUpdate        = view.findViewById<Button>(R.id.btnUpdateProfile)
        val progressBar      = view.findViewById<ProgressBar>(R.id.progressBarProfile)

        // ===== SHOW USER EMAIL AND AVATAR =====
        // auth.currentUser is the currently logged-in user
        // currentUser?.email gives their email address
        val currentUser = auth.currentUser
        val userEmail   = currentUser?.email ?: "No email"

        // Show email on screen
        tvEmail.text = userEmail

        // Show first letter of email as avatar
        // e.g. if email is "john@aiub.com", avatar shows "J"
        tvAvatar.text = userEmail.first().uppercaseChar().toString()

        // ===== BACK BUTTON =====
        btnBack.setOnClickListener {
            // popBackStack() = go back to the previous Fragment
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ===== LOAD EXISTING PROFILE =====
        // When the Fragment opens, we check if this user already saved a profile
        // We do this by searching Firestore for a document with their user ID
        loadProfile(
            view        = view,
            progressBar = progressBar,
            etName      = etName,
            etDept      = etDepartment,
            etAge       = etAge,
            etSemester  = etSemester,
            cbValorant  = cbValorant,
            cbPUBG      = cbPUBG,
            cbFIFA      = cbFIFA,
            cbFreeFire  = cbFreeFire,
            cbCS2       = cbCS2,
            btnSave     = btnSave,
            btnUpdate   = btnUpdate
        )

        // ===== SAVE BUTTON — saves a NEW profile =====
        btnSave.setOnClickListener {
            saveOrUpdateProfile(
                isUpdate    = false,
                progressBar = progressBar,
                btnSave     = btnSave,
                btnUpdate   = btnUpdate,
                etName      = etName,
                etDept      = etDepartment,
                etAge       = etAge,
                etSemester  = etSemester,
                cbValorant  = cbValorant,
                cbPUBG      = cbPUBG,
                cbFIFA      = cbFIFA,
                cbFreeFire  = cbFreeFire,
                cbCS2       = cbCS2
            )
        }

        // ===== UPDATE BUTTON — updates an EXISTING profile =====
        btnUpdate.setOnClickListener {
            saveOrUpdateProfile(
                isUpdate    = true,
                progressBar = progressBar,
                btnSave     = btnSave,
                btnUpdate   = btnUpdate,
                etName      = etName,
                etDept      = etDepartment,
                etAge       = etAge,
                etSemester  = etSemester,
                cbValorant  = cbValorant,
                cbPUBG      = cbPUBG,
                cbFIFA      = cbFIFA,
                cbFreeFire  = cbFreeFire,
                cbCS2       = cbCS2
            )
        }
        // ===== FIND PASSWORD CHANGE VIEWS =====
        val etCurrentPassword    = view.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword        = view.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmNewPassword = view.findViewById<EditText>(R.id.etConfirmNewPassword)
        val btnChangePassword    = view.findViewById<Button>(R.id.btnChangePassword)

// ===== CHANGE PASSWORD BUTTON =====
        btnChangePassword.setOnClickListener {

            val currentPassword = etCurrentPassword.text.toString().trim()
            val newPassword     = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmNewPassword.text.toString().trim()

            // ===== VALIDATION =====
            if (currentPassword.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please enter your current password",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please enter a new password",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                Toast.makeText(requireContext(),
                    "New password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                // Passwords don't match — very important check
                Toast.makeText(requireContext(),
                    "New passwords do not match!",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentPassword == newPassword) {
                Toast.makeText(requireContext(),
                    "New password must be different from current password",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== GET CURRENT USER =====
            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(requireContext(),
                    "User not logged in!",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== STEP 1: RE-AUTHENTICATE THE USER =====
            // Firebase requires the user to prove their identity
            // before changing a sensitive thing like password
            // We do this by creating a "credential" with their
            // current email and current password
            //
            // Think of it like a bank —
            // before changing your PIN, they ask you to confirm your old PIN first
            val email      = user.email ?: ""
            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            btnChangePassword.isEnabled = false
            btnChangePassword.text = "Verifying..."

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    // ✅ Re-authentication successful
                    // Current password was correct — now we can change it

                    btnChangePassword.text = "Changing..."

                    // ===== STEP 2: UPDATE PASSWORD =====
                    // updatePassword() sends the new password to Firebase
                    // Firebase saves it securely (hashed)
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            // ✅ Password changed successfully

                            btnChangePassword.isEnabled = true
                            btnChangePassword.text = "🔐  Change Password"

                            // Clear the password fields
                            etCurrentPassword.text.clear()
                            etNewPassword.text.clear()
                            etConfirmNewPassword.text.clear()

                            Toast.makeText(
                                requireContext(),
                                "Password changed successfully! ✅",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { exception ->
                            btnChangePassword.isEnabled = true
                            btnChangePassword.text = "🔐  Change Password"

                            Toast.makeText(
                                requireContext(),
                                "Failed to change password: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                .addOnFailureListener { exception ->
                    // ❌ Re-authentication failed
                    // This means the current password they typed is WRONG
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = "🔐  Change Password"

                    Toast.makeText(
                        requireContext(),
                        "Current password is incorrect!",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    // ===================================================================
    // LOAD PROFILE FUNCTION
    // ===================================================================
    // This function reads existing profile data from Firestore
    // and fills the form fields automatically
    private fun loadProfile(
        view       : View,
        progressBar: ProgressBar,
        etName     : EditText,
        etDept     : EditText,
        etAge      : EditText,
        etSemester : EditText,
        cbValorant : CheckBox,
        cbPUBG     : CheckBox,
        cbFIFA     : CheckBox,
        cbFreeFire : CheckBox,
        cbCS2      : CheckBox,
        btnSave    : Button,
        btnUpdate  : Button
    ) {
        // Show loading spinner while fetching data
        progressBar.visibility = View.VISIBLE

        // Get the current user's unique ID from Firebase Auth
        // Every Firebase user gets a unique ID (uid) automatically
        // We use this ID as the document ID in Firestore
        // This ensures every user has their OWN profile document
        val userId = auth.currentUser?.uid

        // If userId is null, the user is not logged in
        if (userId == null) {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // ===== READ FROM FIRESTORE =====
        // "profiles" is our collection name
        // .document(userId) finds the document with this user's ID
        // .get() fetches the document
        //
        // Firestore structure:
        // profiles (collection)
        //   └── userId123 (document — one per user)
        //         ├── name: "Arafat"
        //         ├── department: "CSE"
        //         └── games: ["PUBG", "Valorant"]
        db.collection("profiles")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                // Hide spinner after data loads
                progressBar.visibility = View.GONE

                // document.exists() checks if this user has saved a profile before
                if (document.exists()) {
                    // ✅ Profile found — fill the fields with saved data
                    profileExists = true

                    // document.getString("name") reads the "name" field
                    // ?: "" means if the field is missing, use empty string
                    etName.setText(document.getString("name")       ?: "")
                    etDept.setText(document.getString("department") ?: "")
                    etAge.setText(document.getString("age")         ?: "")
                    etSemester.setText(document.getString("semester") ?: "")

                    // ===== LOAD GAMES (stored as a List in Firestore) =====
                    // We stored games as a list: ["PUBG", "Valorant"]
                    // getStringList reads it back as a List<String>
                    // ?: emptyList() means if null, use empty list
                    val games = document.get("games") as? List<*> ?: emptyList<String>()

                    // Check each game — if it's in the list, tick the checkbox
                    // .contains() checks if the list has this value
                    cbValorant.isChecked = games.contains("Valorant")
                    cbPUBG.isChecked     = games.contains("PUBG")
                    cbFIFA.isChecked     = games.contains("FIFA")
                    cbFreeFire.isChecked = games.contains("Free Fire")
                    cbCS2.isChecked      = games.contains("CS2")

                    // Hide Save, show Update — because profile already exists
                    btnSave.visibility   = View.GONE
                    btnUpdate.visibility = View.VISIBLE

                } else {
                    // ❌ No profile found — show Save button
                    profileExists = false
                    btnSave.visibility   = View.VISIBLE
                    btnUpdate.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Failed to load profile: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // ===================================================================
    // SAVE OR UPDATE PROFILE FUNCTION
    // ===================================================================
    // "isUpdate" = true  → use .set() to overwrite existing document
    // "isUpdate" = false → use .set() to create new document
    // We use .set() for BOTH because it creates if not exists
    // and overwrites if exists — perfect for profiles!
    //
    // DIFFERENCE BETWEEN .add(), .set(), .update():
    // .add()    → creates a NEW document with a RANDOM ID (we don't control the ID)
    // .set()    → creates OR completely replaces a document at a SPECIFIC ID
    // .update() → only changes SPECIFIC fields, leaves others unchanged
    //
    // For profiles we use .set() because:
    // - We want the document ID to be the userId (so each user has exactly one profile)
    // - We want to replace all fields when updating
    private fun saveOrUpdateProfile(
        isUpdate   : Boolean,
        progressBar: ProgressBar,
        btnSave    : Button,
        btnUpdate  : Button,
        etName     : EditText,
        etDept     : EditText,
        etAge      : EditText,
        etSemester : EditText,
        cbValorant : CheckBox,
        cbPUBG     : CheckBox,
        cbFIFA     : CheckBox,
        cbFreeFire : CheckBox,
        cbCS2      : CheckBox
    ) {
        // Read all field values
        val name       = etName.text.toString().trim()
        val department = etDept.text.toString().trim()
        val age        = etAge.text.toString().trim()
        val semester   = etSemester.text.toString().trim()

        // ===== VALIDATION =====
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your name!", Toast.LENGTH_SHORT).show()
            return
        }
        if (department.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your department!", Toast.LENGTH_SHORT).show()
            return
        }
        if (age.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your age!", Toast.LENGTH_SHORT).show()
            return
        }
        if (semester.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your semester!", Toast.LENGTH_SHORT).show()
            return
        }

        // ===== COLLECT SELECTED GAMES =====
        // We build a list of only the checked games
        // mutableListOf() creates an empty list we can add to
        val selectedGames = mutableListOf<String>()

        // isChecked is true if the checkbox is ticked, false if not
        if (cbValorant.isChecked) selectedGames.add("Valorant")
        if (cbPUBG.isChecked)     selectedGames.add("PUBG")
        if (cbFIFA.isChecked)     selectedGames.add("FIFA")
        if (cbFreeFire.isChecked) selectedGames.add("Free Fire")
        if (cbCS2.isChecked)      selectedGames.add("CS2")

        // Get the current user's unique ID
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // ===== BUILD DATA OBJECT =====
        val profileData = hashMapOf(
            "name"       to name,
            "department" to department,
            "age"        to age,
            "semester"   to semester,
            "games"      to selectedGames,   // stored as a list
            "userId"     to userId,
            "timestamp"  to System.currentTimeMillis()
        )

        // Show loading state
        progressBar.visibility = View.VISIBLE
        if (isUpdate) {
            btnUpdate.isEnabled = false
            btnUpdate.text = "Updating..."
        } else {
            btnSave.isEnabled = false
            btnSave.text = "Saving..."
        }

        // ===== SAVE TO FIRESTORE USING .set() =====
        // .document(userId) — use the user's Firebase UID as the document ID
        // This guarantees ONE profile per user — no duplicates possible
        // .set(profileData) — creates the document if it doesn't exist
        //                     OR completely replaces it if it does exist
        db.collection("profiles")
            .document(userId)
            .set(profileData)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE

                if (isUpdate) {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Profile"
                    Toast.makeText(requireContext(), "Profile updated! ✅", Toast.LENGTH_SHORT).show()
                } else {
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Profile"
                    Toast.makeText(requireContext(), "Profile saved! ✅", Toast.LENGTH_SHORT).show()

                    // Switch from Save button to Update button
                    // because now a profile exists
                    btnSave.visibility   = View.GONE
                    btnUpdate.visibility = View.VISIBLE
                    profileExists = true
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                if (isUpdate) {
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "✅  Update Profile"
                } else {
                    btnSave.isEnabled = true
                    btnSave.text = "💾  Save Profile"
                }
                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}