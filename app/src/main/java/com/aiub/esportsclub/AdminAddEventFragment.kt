package com.aiub.esportsclub

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminAddEventFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack         = view.findViewById<Button>(R.id.btnBackAddEvent)
        val etBannerUrl     = view.findViewById<EditText>(R.id.etBannerUrl)
        val btnPreview      = view.findViewById<Button>(R.id.btnPreviewBanner)
        val ivPreview       = view.findViewById<ImageView>(R.id.ivBannerPreview)
        val etName          = view.findViewById<EditText>(R.id.etEventName)
        val etDesc          = view.findViewById<EditText>(R.id.etEventDescription)
        val etDate          = view.findViewById<EditText>(R.id.etEventDate)
        val btnPickDate     = view.findViewById<Button>(R.id.btnPickDate)
        val etPrize         = view.findViewById<EditText>(R.id.etEventPrize)
        val etGame          = view.findViewById<EditText>(R.id.etGameName)
        val etRegLink       = view.findViewById<EditText>(R.id.etRegistrationLink)
        val etEventLink     = view.findViewById<EditText>(R.id.etEventLink)
        val btnSave         = view.findViewById<Button>(R.id.btnSaveEvent)
        val progressSave    = view.findViewById<ProgressBar>(R.id.progressSaveEvent)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ===== PREVIEW IMAGE =====
        // When admin pastes a URL and taps Preview,
        // Glide loads the image from that URL into the ImageView
        btnPreview.setOnClickListener {
            val url = etBannerUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an image URL first", Toast.LENGTH_SHORT).show()
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

        // ===== DATE PICKER =====
        btnPickDate.setOnClickListener { showDatePicker(etDate) }
        etDate.setOnClickListener { showDatePicker(etDate) }

        // ===== SAVE BUTTON =====
        btnSave.setOnClickListener {

            val bannerUrl = etBannerUrl.text.toString().trim()
            val name      = etName.text.toString().trim()
            val desc      = etDesc.text.toString().trim()
            val date      = etDate.text.toString().trim()
            val prize     = etPrize.text.toString().trim()
            val game      = etGame.text.toString().trim()
            val regLink   = etRegLink.text.toString().trim()
            val evLink    = etEventLink.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter event title!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desc.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter description!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (date.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a date!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (prize.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter prize!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (game.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter game name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (regLink.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter registration link!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled       = false
            btnSave.text            = "Saving..."
            progressSave.visibility = View.VISIBLE

            // Build event data — bannerUrl is just a URL string, no upload needed
            val eventData = hashMapOf(
                "name"             to name,
                "description"      to desc,
                "date"             to date,
                "prize"            to prize,
                "game"             to game,
                "registrationLink" to regLink,
                "eventLink"        to evLink,
                "bannerImageUrl"   to bannerUrl, // just the URL string
                "icon"             to "🎮",
                "timestamp"        to System.currentTimeMillis()
            )

            db.collection("events")
                .add(eventData)
                .addOnSuccessListener {
                    btnSave.isEnabled       = true
                    btnSave.text            = "Save Event"
                    progressSave.visibility = View.GONE

                    Toast.makeText(requireContext(), "Event saved! ✅", Toast.LENGTH_SHORT).show()

                    // Clear all fields
                    etBannerUrl.text.clear()
                    etName.text.clear()
                    etDesc.text.clear()
                    etDate.text.clear()
                    etPrize.text.clear()
                    etGame.text.clear()
                    etRegLink.text.clear()
                    etEventLink.text.clear()
                    ivPreview.visibility = View.GONE
                }
                .addOnFailureListener { exception ->
                    btnSave.isEnabled       = true
                    btnSave.text            = "Save Event"
                    progressSave.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showDatePicker(etDate: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                val fmt = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                etDate.setText(fmt.format(cal.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}