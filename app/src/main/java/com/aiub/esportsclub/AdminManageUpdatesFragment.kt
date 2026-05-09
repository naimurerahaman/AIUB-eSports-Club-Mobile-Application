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

class AdminManageUpdatesFragment : Fragment() {

    private lateinit var db         : FirebaseFirestore
    private val updateList           = mutableListOf<Update>()
    private lateinit var adapter    : RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_manage_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val btnBack      = view.findViewById<Button>(R.id.btnBackManageUpdates)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerManageUpdates)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = createAdapter()
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        updateList.clear()
        adapter.notifyDataSetChanged()
        loadUpdates()
    }

    private fun loadUpdates() {
        val progressBar  = view?.findViewById<ProgressBar>(R.id.progressManageUpdates)
        val tvNoUpdates  = view?.findViewById<TextView>(R.id.tvNoUpdates)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerManageUpdates)

        progressBar?.visibility  = View.VISIBLE
        recyclerView?.visibility = View.GONE
        tvNoUpdates?.visibility  = View.GONE

        db.collection("updates")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                progressBar?.visibility = View.GONE

                if (querySnapshot.isEmpty) {
                    tvNoUpdates?.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    val update = document.toObject(Update::class.java)
                    if (update != null) {
                        updateList.add(update.copy(documentId = document.id))
                    }
                }

                recyclerView?.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                progressBar?.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun createAdapter(): RecyclerView.Adapter<*> {

        return object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            inner class UpdateVH(v: View) : RecyclerView.ViewHolder(v) {
                val tvTitle  : TextView = v.findViewById(R.id.tvAdminUpdateTitle)
                val tvDesc   : TextView = v.findViewById(R.id.tvAdminUpdateDesc)
                val btnEdit  : Button   = v.findViewById(R.id.btnEditUpdate)
                val btnDelete: Button   = v.findViewById(R.id.btnDeleteUpdate)
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val v = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_admin_update, parent, false)
                return UpdateVH(v)
            }

            override fun getItemCount() = updateList.size

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val vh     = holder as UpdateVH
                val update = updateList[position]

                vh.tvTitle.text = update.title
                vh.tvDesc.text  = update.description

                // ===== EDIT BUTTON =====
                vh.btnEdit.setOnClickListener {
                    val editFragment = AdminEditUpdateFragment()

                    // Pass ALL data including the new link field
                    val bundle = Bundle()
                    bundle.putString("documentId", update.documentId)
                    bundle.putString("title",      update.title)
                    bundle.putString("desc",       update.description)
                    bundle.putString("imageUrl",   update.imageUrl)
                    bundle.putString("link",       update.link) // NEW

                    editFragment.arguments = bundle

                    (requireActivity() as AdminActivity)
                        .loadFragment(editFragment)
                }

                // ===== DELETE BUTTON =====
                vh.btnDelete.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Update")
                        .setMessage("Delete '${update.title}'? This cannot be undone.")
                        .setPositiveButton("Delete") { dialog, _ ->
                            db.collection("updates")
                                .document(update.documentId)
                                .delete()
                                .addOnSuccessListener {
                                    updateList.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, updateList.size)
                                    Toast.makeText(
                                        requireContext(),
                                        "Deleted ✅",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Delete failed: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }
}