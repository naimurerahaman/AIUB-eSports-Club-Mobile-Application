package com.aiub.esportsclub

import android.app.AlertDialog
import android.graphics.Color
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

class AdminMemberApplicationsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_member_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackMemberApps)
        val progressBar  = view.findViewById<ProgressBar>(R.id.progressMemberApps)
        val tvNoApps     = view.findViewById<TextView>(R.id.tvNoApplications)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMemberApps)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadApplications(progressBar, tvNoApps, recyclerView)
    }

    private fun loadApplications(
        progressBar : ProgressBar,
        tvNoApps    : TextView,
        recyclerView: RecyclerView
    ) {
        progressBar.visibility = View.VISIBLE

        db.collection("memberApplications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvNoApps.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                // Convert documents to list of maps
                val appList = mutableListOf<MutableMap<String, Any>>()
                for (document in querySnapshot.documents) {
                    val data = document.data?.toMutableMap() ?: continue
                    data["documentId"] = document.id
                    appList.add(data)
                }

                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter    = createAdapter(appList)
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

            inner class AppVH(v: View) : RecyclerView.ViewHolder(v) {
                val tvName    : TextView = v.findViewById(R.id.tvAppName)
                val tvStudentId: TextView = v.findViewById(R.id.tvAppStudentId)
                val tvDept    : TextView = v.findViewById(R.id.tvAppDept)
                val tvGames   : TextView = v.findViewById(R.id.tvAppGames)
                val tvRole    : TextView = v.findViewById(R.id.tvAppRole)
                val tvStatus  : TextView = v.findViewById(R.id.tvAppStatus)
                val btnApprove: Button   = v.findViewById(R.id.btnApproveApp)
                val btnDelete : Button   = v.findViewById(R.id.btnDeleteApp)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                AppVH(LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_member_application, parent, false))

            override fun getItemCount() = list.size

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val vh   = holder as AppVH
                val data = list[position]

                val name      = data["name"]?.toString()       ?: "Unknown"
                val studentId = data["studentId"]?.toString()  ?: "Unknown"
                val dept      = data["department"]?.toString() ?: "Unknown"
                val games     = (data["games"] as? List<*>)?.joinToString(", ") ?: "None"
                val role      = data["role"]?.toString()       ?: "Unknown"
                val status    = data["status"]?.toString()     ?: "pending"
                val docId     = data["documentId"]?.toString() ?: ""

                vh.tvName.text     = "👤 $name"
                vh.tvStudentId.text = "ID: $studentId"
                vh.tvDept.text     = "Dept: $dept"
                vh.tvGames.text    = "Games: $games"
                vh.tvRole.text     = "Role: $role"
                vh.tvStatus.text   = status.uppercase()

                // Color status badge
                when (status) {
                    "approved" -> vh.tvStatus.setBackgroundColor(Color.parseColor("#1E8449"))
                    "pending"  -> vh.tvStatus.setBackgroundColor(Color.parseColor("#0F3460"))
                    else       -> vh.tvStatus.setBackgroundColor(Color.parseColor("#C0392B"))
                }

                // ===== APPROVE BUTTON =====
                vh.btnApprove.setOnClickListener {
                    db.collection("memberApplications")
                        .document(docId)
                        .update("status", "approved")
                        .addOnSuccessListener {
                            data["status"] = "approved"
                            notifyItemChanged(position)
                            Toast.makeText(requireContext(), "Approved ✅", Toast.LENGTH_SHORT).show()
                        }
                }

                // ===== DELETE BUTTON =====
                vh.btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Application")
                        .setMessage("Delete application for $name?")
                        .setPositiveButton("Delete") { dialog, _ ->
                            db.collection("memberApplications")
                                .document(docId)
                                .delete()
                                .addOnSuccessListener {
                                    list.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, list.size)
                                    Toast.makeText(requireContext(), "Deleted ✅", Toast.LENGTH_SHORT).show()
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