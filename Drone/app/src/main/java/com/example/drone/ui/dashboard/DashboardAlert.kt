package com.example.drone

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.drone.ui.dashboard.DashboardViewModel

class DashboardAlert : DialogFragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_enter_chords, null)

            dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

            builder.setView(dialogView)
                // Add action buttons.
                .setPositiveButton("Enter",
                    DialogInterface.OnClickListener { dialog, id ->

                        val lat = dialogView?.findViewById<EditText>(R.id.editLatitude)
                        val long = dialogView?.findViewById<EditText>(R.id.editLongitude)

                        Log.d("CHECK", "${lat?.text}, ${long?.text}")

                        if (lat != null && long != null) {
                            dashboardViewModel.SaveChords(
                                lat?.text.toString().toFloat(),
                                long?.text.toString().toFloat()
                            )
                        }
                    })
                .setNegativeButton("Exit",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d("Negative", "Negative registered")
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}
