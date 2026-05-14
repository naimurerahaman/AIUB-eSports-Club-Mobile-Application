package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class AdminEditEventFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var documentId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_edit_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackEdit)
        val etName       = view.findViewById<EditText>(R.id.etEditEventName)
        val etGame       = view.findViewById<EditText>(R.id.etEditGameName)
        val etDate       = view.findViewById<EditText>(R.id.etEditEventDate)
        val etPrize      = view.findViewById<EditText>(R.id.etEditEventPrize)
        val etDesc       = view.findViewById<EditText>(R.id.etEditEventDescription)
        val etBannerUrl  = view.findViewById<EditText>(R.id.etEditBannerUrl)
        val btnPreview   = view.findViewById<Button>(R.id.btnPreviewEditBanner)
        val ivPreview    = view.findViewById<ImageView>(R.id.ivEditBannerPreview)
        val btnUpdate    = view.findViewById<Button>(R.id.btnUpdateEvent)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Read data passed from AdminManageEventsFragment via Bundle
        documentId = arguments?.getString("documentId") ?: ""

        // Pre-fill all fields with existing data
        etName.setText(arguments?.getString("name")           ?: "")
        etGame.setText(arguments?.getString("game")           ?: "")
        etDate.setText(arguments?.getString("date")           ?: "")
        etPrize.setText(arguments?.getString("prize")         ?: "")
        etDesc.setText(arguments?.getString("description")    ?: "")
        etBannerUrl.setText(arguments?.getString("bannerImageUrl") ?: "")

        // Show existing banner preview if URL already exists
        val existingBanner = arguments?.getString("bannerImageUrl") ?: ""
        if (existingBanner.isNotEmpty()) {
            ivPreview.visibility = View.VISIBLE
            Glide.with(this)
                .load(existingBanner)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(ivPreview)
        }

        // Preview button — loads the entered URL into the ImageView
        btnPreview.setOnClickListener {
            val url = etBannerUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a URL first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ivPreview.visibility = View.VISIBLE
            Glide.with(this)
                .load(url)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .centerCrop()
                .into(ivPreview)
        }

        // Update button — saves all fields including banner URL to Firestore
        btnUpdate.setOnClickListener {
            val newName      = etName.text.toString().trim()
            val newGame      = etGame.text.toString().trim()
            val newDate      = etDate.text.toString().trim()
            val newPrize     = etPrize.text.toString().trim()
            val newDesc      = etDesc.text.toString().trim()
            val newBannerUrl = etBannerUrl.text.toString().trim()

            if (newName.isEmpty() || newGame.isEmpty() || newDate.isEmpty() ||
                newPrize.isEmpty() || newDesc.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnUpdate.isEnabled = false
            btnUpdate.text      = "Updating..."

            db.collection("events")
                .document(documentId)
                .update(mapOf(
                    "name"           to newName,
                    "game"           to newGame,
                    "date"           to newDate,
                    "prize"          to newPrize,
                    "description"    to newDesc,
                    "bannerImageUrl" to newBannerUrl  // saves banner URL
                ))
                .addOnSuccessListener {
                    btnUpdate.isEnabled = true
                    btnUpdate.text      = "✅  Update Event"
                    Toast.makeText(requireContext(), "Event updated! ✅", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { exception ->
                    btnUpdate.isEnabled = true
                    btnUpdate.text      = "✅  Update Event"
                    Toast.makeText(
                        requireContext(),
                        "Update failed: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }
}