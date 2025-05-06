package com.example.drone.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drone.DashboardAlert
import com.example.drone.R
import com.example.drone.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(), DashboardAlert.NoticeDialogListener {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val imageView = view?.findViewById<ImageView>(R.id.ImageHold)

    var xPx = 0;
    var yPx = 0;

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.ImageHold.setOnClickListener{
            xPx = it.x.toInt()
            yPx = it.y.toInt()
            val dialog = DashboardAlert()
            dialog.show(parentFragmentManager, "DashboardAlert")
        }

    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val eLat = dialog.view?.findViewById<EditText?>(R.id.editLatitude);
        val eLong = dialog.view?.findViewById<EditText?>(R.id.editLongitude)
        if(eLat != null && eLong != null)
        {
            dashboardViewModel.SaveCoordinates(xPx, yPx,
                eLat.text.toString().toDouble(), eLong.text.toString().toDouble())
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        // User taps the dialog's negative button.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}