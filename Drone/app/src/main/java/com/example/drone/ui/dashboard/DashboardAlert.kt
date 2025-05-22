package com.example.drone.ui.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.drone.R

class DashboardAlert : DialogFragment() {

    private lateinit var dashboardViewModel: DashboardPicSelectViewModel
    private var onCancelCallback: (() -> Unit)? = null

    fun setOnCancelListener(callback: () -> Unit) {
        onCancelCallback = callback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.layout_enter_chords, null)

            dashboardViewModel = ViewModelProvider(this).get(DashboardPicSelectViewModel::class.java)

            builder.setView(dialogView)
                .setPositiveButton("Enter",
                    DialogInterface.OnClickListener { dialog, id ->

                        val lat = dialogView?.findViewById<EditText>(R.id.editLatitude)
                        val long = dialogView?.findViewById<EditText>(R.id.editLongitude)

                        Log.d("CHECK", "${lat?.text}, ${long?.text}")

                        if (lat != null && long != null) {
                            dashboardViewModel.SaveChords(
                                lat?.text.toString().toFloat(),
                                long?.text.toString().toFloat())
                        }
                    })
                .setNegativeButton("Cancel") { dialog, _ ->
                    onCancelCallback?.invoke()
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        // This is called when back button is pressed
        onCancelCallback?.invoke()
    }
}