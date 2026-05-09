package com.aiub.esportsclub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationsListFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registrations_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackRegList)
        val progressBar  = view.findViewById<ProgressBar>(R.id.progressBarList)
        val tvEmpty      = view.findViewById<TextView>(R.id.tvEmptyMessage)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewRegistrations)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadRegistrations(progressBar, tvEmpty, recyclerView)
    }

    private fun loadRegistrations(
        progressBar : ProgressBar,
        tvEmpty     : TextView,
        recyclerView: RecyclerView
    ) {
        progressBar.visibility = View.VISIBLE

        db.collection("registrations")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvEmpty.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                val list = mutableListOf<Map<String, Any>>()

                for (document in querySnapshot.documents) {
                    val data = document.data ?: continue
                    // Add document ID to data map for delete functionality
                    val dataWithId = data.toMutableMap()
                    dataWithId["documentId"] = document.id
                    list.add(dataWithId)
                }

                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter    = createAdapter(list)
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
        list: MutableList<Map<String, Any>>
    ): RecyclerView.Adapter<*> {

        return object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            inner class RegVH(v: View) : RecyclerView.ViewHolder(v) {
                val tvName    : TextView = v.findViewById(R.id.tvRegName)
                val tvStudentId: TextView = v.findViewById(R.id.tvRegStudentId)
                val tvTeam    : TextView = v.findViewById(R.id.tvRegTeam)
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val v = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_registration, parent, false)
                return RegVH(v)
            }

            override fun getItemCount() = list.size

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val vh   = holder as RegVH
                val data = list[position]

                val name      = data["name"]?.toString()      ?: "Unknown"
                val studentId = data["studentId"]?.toString() ?: "Unknown"
                val games     = (data["games"] as? List<*>)?.joinToString(", ") ?: "None"
                val format    = data["format"]?.toString()    ?: "Unknown"
                val docId     = data["documentId"]?.toString() ?: ""

                vh.tvName.text     = "👤 $name"
                vh.tvStudentId.text = "🎓 ID: $studentId"
                vh.tvTeam.text     = "🎮 Games: $games | Format: $format"

                // Long press to delete
                vh.itemView.setOnLongClickListener {
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
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                        .show()
                    true
                }
            }
        }
    }
}