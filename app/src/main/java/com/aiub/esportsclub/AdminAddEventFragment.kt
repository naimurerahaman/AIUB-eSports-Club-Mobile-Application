package com.aiub.esportsclub

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AdminAddEventFragment : Fragment() {

    private lateinit var db     : FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // This stores the URI (file path) of the image the admin selected
    // URI = Uniform Resource Identifier = basically the location of the file
    private var selectedImageUri: Uri? = null

    // This is the URL of the uploaded image on Firebase Storage
    // We save this URL in Firestore so users can load the image
    private var uploadedBannerUrl: String = ""

    // ===== IMAGE PICKER =====
    // registerForActivityResult is the modern way to pick files
    // It opens the phone gallery and returns the selected image URI
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // This code runs after user selects an image
        if (uri != null) {
            selectedImageUri = uri

            // Show preview of selected image using Glide
            val ivPreview = view?.findViewById<ImageView>(R.id.ivBannerPreview)
            if (ivPreview != null) {
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(ivPreview)
            }

            val tvStatus = view?.findViewById<TextView>(R.id.tvUploadStatus)
            tvStatus?.text = "Image selected. Will upload when you save."
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect to Firebase services
        db      = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Find all views
        val btnBack          = view.findViewById<Button>(R.id.btnBackAddEvent)
        val ivBannerPreview  = view.findViewById<ImageView>(R.id.ivBannerPreview)
        val btnSelectBanner  = view.findViewById<Button>(R.id.btnSelectBanner)
        val progressUpload   = view.findViewById<ProgressBar>(R.id.progressUpload)
        val tvUploadStatus   = view.findViewById<TextView>(R.id.tvUploadStatus)
        val etName           = view.findViewById<EditText>(R.id.etEventName)
        val etDesc           = view.findViewById<EditText>(R.id.etEventDescription)
        val etDate           = view.findViewById<EditText>(R.id.etEventDate)
        val btnPickDate      = view.findViewById<Button>(R.id.btnPickDate)
        val etPrize          = view.findViewById<EditText>(R.id.etEventPrize)
        val etGame           = view.findViewById<EditText>(R.id.etGameName)
        val etRegLink        = view.findViewById<EditText>(R.id.etRegistrationLink)
        val etEventLink      = view.findViewById<EditText>(R.id.etEventLink)
        val btnSave          = view.findViewById<Button>(R.id.btnSaveEvent)
        val progressSave     = view.findViewById<ProgressBar>(R.id.progressSaveEvent)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // ===== SELECT IMAGE BUTTON =====
        btnSelectBanner.setOnClickListener {
            // Open gallery — "image/*" means any image type
            imagePickerLauncher.launch("image/*")
        }

        // ===== DATE PICKER BUTTON =====
        btnPickDate.setOnClickListener {
            showDatePicker(etDate)
        }

        // Allow tapping the date field to open picker too
        etDate.setOnClickListener {
            showDatePicker(etDate)
        }

        // ===== SAVE BUTTON =====
        btnSave.setOnClickListener {

            val name    = etName.text.toString().trim()
            val desc    = etDesc.text.toString().trim()
            val date    = etDate.text.toString().trim()
            val prize   = etPrize.text.toString().trim()
            val game    = etGame.text.toString().trim()
            val regLink = etRegLink.text.toString().trim()
            val evLink  = etEventLink.text.toString().trim()

            // ===== VALIDATION =====
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

            // Show loading state
            btnSave.isEnabled       = false
            btnSave.text            = "Saving..."
            progressSave.visibility = View.VISIBLE

            // ===== DECIDE: UPLOAD IMAGE OR SAVE DIRECTLY =====
            if (selectedImageUri != null) {
                // Admin selected an image → upload it first then save
                uploadImageAndSave(
                    imageUri    = selectedImageUri!!,
                    name        = name,
                    desc        = desc,
                    date        = date,
                    prize       = prize,
                    game        = game,
                    regLink     = regLink,
                    evLink      = evLink,
                    progressUpload  = progressUpload,
                    tvUploadStatus  = tvUploadStatus,
                    progressSave    = progressSave,
                    btnSave         = btnSave,
                    etName          = etName,
                    etDesc          = etDesc,
                    etDate          = etDate,
                    etPrize         = etPrize,
                    etGame          = etGame,
                    etRegLink       = etRegLink,
                    etEvLink        = etEventLink,
                    ivPreview       = ivBannerPreview
                )
            } else {
                // No image selected → save event without banner
                saveEventToFirestore(
                    name        = name,
                    desc        = desc,
                    date        = date,
                    prize       = prize,
                    game        = game,
                    regLink     = regLink,
                    evLink      = evLink,
                    bannerUrl   = "",   // empty = no banner
                    progressSave = progressSave,
                    btnSave      = btnSave,
                    etName       = etName,
                    etDesc       = etDesc,
                    etDate       = etDate,
                    etPrize      = etPrize,
                    etGame       = etGame,
                    etRegLink    = etRegLink,
                    etEvLink     = etEventLink,
                    ivPreview    = ivBannerPreview
                )
            }
        }
    }

    // ===== SHOW DATE PICKER =====
    private fun showDatePicker(etDate: EditText) {
        // Get today's date as default
        val calendar = Calendar.getInstance()
        val year     = calendar.get(Calendar.YEAR)
        val month    = calendar.get(Calendar.MONTH)
        val day      = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog shows a calendar popup
        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format: "July 15, 2025"
                val cal = Calendar.getInstance()
                cal.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                etDate.setText(dateFormat.format(cal.time))
            },
            year, month, day
        ).show()
    }

    // ===== UPLOAD IMAGE TO FIREBASE STORAGE =====
    private fun uploadImageAndSave(
        imageUri      : Uri,
        name          : String,
        desc          : String,
        date          : String,
        prize         : String,
        game          : String,
        regLink       : String,
        evLink        : String,
        progressUpload: ProgressBar,
        tvUploadStatus: TextView,
        progressSave  : ProgressBar,
        btnSave       : Button,
        etName        : EditText,
        etDesc        : EditText,
        etDate        : EditText,
        etPrize       : EditText,
        etGame        : EditText,
        etRegLink     : EditText,
        etEvLink      : EditText,
        ivPreview     : ImageView
    ) {
        // Show upload progress bar
        progressUpload.visibility = View.VISIBLE
        tvUploadStatus.text       = "Uploading banner image..."

        // ===== CREATE STORAGE REFERENCE =====
        // Firebase Storage organizes files in folders (like Google Drive)
        // "event_banners/" is the folder
        // UUID.randomUUID() generates a unique filename so files don't overwrite each other
        // ".jpg" is the file extension
        val fileName    = "event_banners/${UUID.randomUUID()}.jpg"
        val storageRef  = storage.reference.child(fileName)

        // ===== UPLOAD THE FILE =====
        // putFile() uploads the image file from the URI
        // URI is like the file path on the phone
        val uploadTask = storageRef.putFile(imageUri)

        // addOnProgressListener tracks upload progress (0% to 100%)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressUpload.progress = progress.toInt()
            tvUploadStatus.text     = "Uploading: ${progress.toInt()}%"
        }

        uploadTask
            .addOnSuccessListener {
                // ===== IMAGE UPLOADED — NOW GET ITS DOWNLOAD URL =====
                // After uploading, we get a permanent URL for the image
                // This URL can be used by anyone to view the image
                storageRef.downloadUrl
                    .addOnSuccessListener { downloadUri ->
                        val bannerUrl = downloadUri.toString()

                        progressUpload.visibility = View.GONE
                        tvUploadStatus.text       = "Image uploaded successfully!"

                        // Now save the event with the banner URL
                        saveEventToFirestore(
                            name        = name,
                            desc        = desc,
                            date        = date,
                            prize       = prize,
                            game        = game,
                            regLink     = regLink,
                            evLink      = evLink,
                            bannerUrl   = bannerUrl,
                            progressSave = progressSave,
                            btnSave      = btnSave,
                            etName       = etName,
                            etDesc       = etDesc,
                            etDate       = etDate,
                            etPrize      = etPrize,
                            etGame       = etGame,
                            etRegLink    = etRegLink,
                            etEvLink     = etEvLink,
                            ivPreview    = ivPreview
                        )
                    }
                    .addOnFailureListener { exception ->
                        progressUpload.visibility = View.GONE
                        btnSave.isEnabled         = true
                        btnSave.text              = "Save Event"
                        progressSave.visibility   = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Failed to get image URL: ${exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { exception ->
                progressUpload.visibility = View.GONE
                btnSave.isEnabled         = true
                btnSave.text              = "Save Event"
                progressSave.visibility   = View.GONE
                tvUploadStatus.text       = "Upload failed"
                Toast.makeText(
                    requireContext(),
                    "Image upload failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // ===== SAVE EVENT DATA TO FIRESTORE =====
    private fun saveEventToFirestore(
        name        : String,
        desc        : String,
        date        : String,
        prize       : String,
        game        : String,
        regLink     : String,
        evLink      : String,
        bannerUrl   : String,
        progressSave: ProgressBar,
        btnSave     : Button,
        etName      : EditText,
        etDesc      : EditText,
        etDate      : EditText,
        etPrize     : EditText,
        etGame      : EditText,
        etRegLink   : EditText,
        etEvLink    : EditText,
        ivPreview   : ImageView
    ) {
        val eventData = hashMapOf(
            "name"             to name,
            "description"      to desc,
            "date"             to date,
            "prize"            to prize,
            "game"             to game,
            "registrationLink" to regLink,
            "eventLink"        to evLink,
            "bannerImageUrl"   to bannerUrl,
            "icon"             to "🎮",
            "timestamp"        to System.currentTimeMillis()
        )

        db.collection("events")
            .add(eventData)
            .addOnSuccessListener {
                btnSave.isEnabled       = true
                btnSave.text            = "Save Event"
                progressSave.visibility = View.GONE

                Toast.makeText(
                    requireContext(),
                    "Event saved successfully! ✅",
                    Toast.LENGTH_SHORT
                ).show()

                // Clear all fields
                etName.text.clear()
                etDesc.text.clear()
                etDate.text.clear()
                etPrize.text.clear()
                etGame.text.clear()
                etRegLink.text.clear()
                etEvLink.text.clear()
                ivPreview.setImageResource(android.R.drawable.ic_menu_gallery)
                selectedImageUri  = null
                uploadedBannerUrl = ""

                val tvStatus = view?.findViewById<TextView>(R.id.tvUploadStatus)
                tvStatus?.text = ""
            }
            .addOnFailureListener { exception ->
                btnSave.isEnabled       = true
                btnSave.text            = "Save Event"
                progressSave.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Save failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}