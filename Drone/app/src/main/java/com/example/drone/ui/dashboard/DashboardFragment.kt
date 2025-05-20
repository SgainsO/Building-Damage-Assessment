package com.example.drone.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drone.R
import com.example.drone.databinding.FragmentDashboardPicturesBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardPicturesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel


    val drawableList : MutableList<Drawable?> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardPicturesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val dataset = arrayOf(0) // will be empty for trial purposes
        val adapter = RecycleAdapter(this, dataset)
        val recycleView: RecyclerView = binding.ImageViews

        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter = adapter

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
