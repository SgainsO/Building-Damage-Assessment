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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.drone.DashboardAlert
import com.example.drone.R
import com.example.drone.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

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

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        drawableList.add(binding.ImageHold.drawable)

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ImageHold.setOnTouchListener( object : View.OnTouchListener{
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    view.performClick()
                    dashboardViewModel.SaveClicked(motionEvent.x, motionEvent.y)
                    val alert = DashboardAlert()
                    alert.show(parentFragmentManager, "Dash Alert")

                    val TopLeftX = binding.ImageHold.x
                    val TopLeftY =  binding.ImageHold.y
                    //https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/suspend-cancellable-coroutine.html

                    //Need to implement this: https://stackoverflow.com/questions/8909835/android-how-do-i-get-the-x-y-coordinates-within-an-image-imageview

                    val overlay = ContextCompat.getDrawable(requireContext(), R.drawable.check)
                    val overlayDrawable = overlay?.mutate()
                    Log.d("ARROW", "ASSET POSITION: ${TopLeftX}, ${TopLeftY}" +
                            " MOTION EVENT: ${motionEvent.x}, ${motionEvent.y}")

                    overlayDrawable?.setBounds(0, 0, 1, 1)

                    drawableList.add(overlayDrawable)
                    val layers = LayerDrawable(arrayOf(binding.ImageHold.drawable, overlayDrawable))
                    //        layers.setLayerInset(0, 512, 512, 0, 0)
                    layers.setLayerInset(1, 0,
                        0, 0, 0)

                    binding.ImageHold.setImageDrawable(layers)
                }



                return true
            }

        })
    }


    fun SetImageMarkers(LDraw : LayerDrawable) :LayerDrawable{
        val lArr = dashboardViewModel.LocationInfoArray
        for (i in 0 until drawableList.size - 1)
        {
            val ImageChords = lArr[i]["imageChord"]!!
            LDraw.setLayerInset(i + 1,
                ImageChords[0].toInt() +256,
                ImageChords[1].toInt() + 256,
                0,
                0)
        }
        return LDraw
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
