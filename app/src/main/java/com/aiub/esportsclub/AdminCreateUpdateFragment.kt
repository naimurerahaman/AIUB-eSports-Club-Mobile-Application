package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class AdminCreateUpdateFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_create_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack     = view.findViewById<Button>(R.id.btnBackCreateUpdate)
        val etTitle     = view.findViewById<EditText>(R.id.etUpdateTitle)
        val etDesc      = view.findViewById<EditText>(R.id.etUpdateDescription)
        val etImageUrl  = view.findViewById<EditText>(R.id.etUpdateImageUrl)
        val btnPost     = view.findViewById<Button>(R.id.btnPostUpdate)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarCreateUpdate)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnPost.setOnClickListener {

            val title    = etTitle.text.toString().trim()
            val desc     = etDesc.text.toString().trim()
            val imageUrl = etImageUrl.text.toString().trim()

            // ===== VALIDATION =====
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a description!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===== BUILD THE UPDATE DATA =====
            val updateData = hashMapOf(
                "title"       to title,
                "description" to desc,
                "imageUrl"    to imageUrl,
                "timestamp"   to System.currentTimeMillis()
            )

            // Show loading state
            btnPost.isEnabled      = false
            btnPost.text           = "Posting..."
            progressBar.visibility = View.VISIBLE

            // ===== STEP 1: SAVE UPDATE TO FIRESTORE =====
            // This saves the post to the "updates" collection
            // which shows in the Home Screen news feed
            db.collection("updates")
                .add(updateData)
                .addOnSuccessListener {

                    // ===== STEP 2: SAVE NOTIFICATION TRIGGER =====
                    // After saving the update, we also save it to
                    // the "notifications" collection
                    // This acts as a record of all notifications sent
                    // and can be used later for notification history
                    val notificationData = hashMapOf(
                        "title"     to title,
                        "body"      to desc,
                        "timestamp" to System.currentTimeMillis(),
                        "sentBy"    to "admin"
                    )

                    db.collection("notifications")
                        .add(notificationData)
                        .addOnSuccessListener {
                            android.util.Log.d(
                                "NOTIFICATION",
                                "Notification trigger saved successfully"
                            )
                        }
                        .addOnFailureListener { exception ->
                            // If notification save fails, we don't crash the app
                            // The update was already saved — this is just extra
                            android.util.Log.e(
                                "NOTIFICATION",
                                "Failed to save notification: ${exception.message}"
                            )
                        }

                    // ===== RESET UI =====
                    btnPost.isEnabled      = true
                    btnPost.text           = "📢  Post Update"
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Update posted successfully! ✅",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Clear all fields so admin can add another post
                    etTitle.text.clear()
                    etDesc.text.clear()
                    etImageUrl.text.clear()
                }
                .addOnFailureListener { exception ->

                    // Something went wrong — reset UI and show error
                    btnPost.isEnabled      = true
                    btnPost.text           = "📢  Post Update"
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Error: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}