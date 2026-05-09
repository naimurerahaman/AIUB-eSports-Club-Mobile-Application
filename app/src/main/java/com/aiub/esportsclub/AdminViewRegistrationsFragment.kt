package com.aiub.esportsclub

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminViewRegistrationsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_view_registrations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackViewReg)
        val progressBar  = view.findViewById<ProgressBar>(R.id.progressViewReg)
        val tvNoReg      = view.findViewById<TextView>(R.id.tvNoRegistrations)
        val tvCount      = view.findViewById<TextView>(R.id.tvRegCount)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewReg)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadRegistrations(progressBar, tvNoReg, tvCount, recyclerView)
    }

    private fun loadRegistrations(
        progressBar : ProgressBar,
        tvNoReg     : TextView,
        tvCount     : TextView,
        recyclerView: RecyclerView
    ) {
        progressBar.visibility = View.VISIBLE

        db.collection("registrations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.visibility = View.GONE

                // Show total count in header badge
                tvCount.text = querySnapshot.size().toString()

                if (querySnapshot.isEmpty) {
                    tvNoReg.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                // Convert documents to list of maps
                val regList = mutableListOf<MutableMap<String, Any>>()
                for (document in querySnapshot.documents) {
                    val data = document.data?.toMutableMap() ?: continue
                    data["documentId"] = document.id
                    regList.add(data)
                }

                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter    = createAdapter(regList)
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun createAdapter(
        list: MutableList<MutableMap<String, Any>>
    ): RecyclerView.Adapter<*> {

        return object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            inner class RegVH(v: View) : RecyclerView.ViewHolder(v) {
                val tvName       : TextView = v.findViewById(R.id.tvRegName)
                val tvStudentId  : TextView = v.findViewById(R.id.tvRegStudentId)
                val tvGender     : TextView = v.findViewById(R.id.tvRegGender)
                val tvPlatforms  : TextView = v.findViewById(R.id.tvRegPlatforms)
                val tvGames      : TextView = v.findViewById(R.id.tvRegGames)
                val tvFormat     : TextView = v.findViewById(R.id.tvRegFormat)
                val tvSuggestions: TextView = v.findViewById(R.id.tvRegSuggestions)
                val btnDelete    : Button   = v.findViewById(R.id.btnDeleteReg)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                RegVH(LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_admin_registration, parent, false))

            override fun getItemCount() = list.size

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val vh   = holder as RegVH
                val data = list[position]

                val name        = data["name"]?.toString()        ?: "Unknown"
                val studentId   = data["studentId"]?.toString()   ?: "Unknown"
                val gender      = data["gender"]?.toString()      ?: "Unknown"
                val platforms   = (data["platforms"] as? List<*>)?.joinToString(", ") ?: "None"
                val games       = (data["games"] as? List<*>)?.joinToString(", ")     ?: "None"
                val formats     = (data["formats"] as? List<*>)?.joinToString(", ")   ?: "None"
                val suggestions = data["suggestions"]?.toString() ?: ""
                val docId       = data["documentId"]?.toString()  ?: ""

                vh.tvName.text        = "👤 $name"
                vh.tvStudentId.text   = "ID: $studentId"
                vh.tvGender.text      = "Gender: $gender"
                vh.tvPlatforms.text   = "Platforms: $platforms"
                vh.tvGames.text       = "Games: $games"
                vh.tvFormat.text      = "Format: $formats"
                vh.tvSuggestions.text = if (suggestions.isNotEmpty())
                    "Suggestions: $suggestions" else ""

                // ===== DELETE BUTTON =====
                vh.btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Registration")
                        .setMessage("Delete registration for $name?")
                        .setPositiveButton("Delete") { dialog, _ ->
                            db.collection("registrations")
                                .document(docId)
                                .delete()
                                .addOnSuccessListener {
                                    list.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, list.size)
                                    Toast.makeText(
                                        requireContext(),
                                        "Deleted ✅",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                        .show()
                }
            }
        }
    }
}