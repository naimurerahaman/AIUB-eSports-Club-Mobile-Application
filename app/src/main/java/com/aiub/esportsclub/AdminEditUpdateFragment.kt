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

class AdminEditUpdateFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var documentId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_edit_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack     = view.findViewById<Button>(R.id.btnBackEditUpdate)
        val etTitle     = view.findViewById<EditText>(R.id.etEditUpdateTitle)
        val etDesc      = view.findViewById<EditText>(R.id.etEditUpdateDescription)
        val etImageUrl  = view.findViewById<EditText>(R.id.etEditUpdateImageUrl)
        val etLink      = view.findViewById<EditText>(R.id.etEditUpdateLink) // NEW
        val btnSave     = view.findViewById<Button>(R.id.btnSaveEditUpdate)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarEditUpdate)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Receive data from AdminManageUpdatesFragment
        documentId           = arguments?.getString("documentId") ?: ""
        val receivedTitle    = arguments?.getString("title")      ?: ""
        val receivedDesc     = arguments?.getString("desc")       ?: ""
        val receivedImageUrl = arguments?.getString("imageUrl")   ?: ""
        val receivedLink     = arguments?.getString("link")       ?: "" // NEW

        // Pre-fill all fields
        etTitle.setText(receivedTitle)
        etDesc.setText(receivedDesc)
        etImageUrl.setText(receivedImageUrl)
        etLink.setText(receivedLink) // NEW

        btnSave.setOnClickListener {

            val newTitle    = etTitle.text.toString().trim()
            val newDesc     = etDesc.text.toString().trim()
            val newImageUrl = etImageUrl.text.toString().trim()
            val newLink     = etLink.text.toString().trim() // NEW

            if (newTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newDesc.isEmpty()) {
                Toast.makeText(requireContext(), "Description cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled      = false
            btnSave.text           = "Saving..."
            progressBar.visibility = View.VISIBLE

            // Update Firestore — now includes link field
            db.collection("updates")
                .document(documentId)
                .update(mapOf(
                    "title"       to newTitle,
                    "description" to newDesc,
                    "imageUrl"    to newImageUrl,
                    "link"        to newLink  // NEW
                ))
                .addOnSuccessListener {
                    btnSave.isEnabled      = true
                    btnSave.text           = "✅  Save Changes"
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Update saved! ✅",
                        Toast.LENGTH_SHORT
                    ).show()

                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { exception ->
                    btnSave.isEnabled      = true
                    btnSave.text           = "✅  Save Changes"
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        "Failed: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}