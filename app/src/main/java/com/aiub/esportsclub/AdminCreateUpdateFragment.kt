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

            // Validation
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a description!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Build the data object
            val updateData = hashMapOf(
                "title"       to title,
                "description" to desc,
                "imageUrl"    to imageUrl, // empty string if not provided
                "timestamp"   to System.currentTimeMillis()
            )

            btnPost.isEnabled = false
            btnPost.text = "Posting..."
            progressBar.visibility = View.VISIBLE

            // Save to Firestore "updates" collection
            db.collection("updates")
                .add(updateData)
                .addOnSuccessListener {
                    btnPost.isEnabled = true
                    btnPost.text = "📢  Post Update"
                    progressBar.visibility = View.GONE

                    Toast.makeText(requireContext(), "Update posted! ✅", Toast.LENGTH_SHORT).show()

                    // Clear fields for next post
                    etTitle.text.clear()
                    etDesc.text.clear()
                    etImageUrl.text.clear()
                }
                .addOnFailureListener { exception ->
                    btnPost.isEnabled = true
                    btnPost.text = "📢  Post Update"
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}