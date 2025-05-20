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
import androidx.lifecycle.lifecycleScope
import com.example.drone.R
import com.example.drone.databinding.FragmentDashboardSelectBinding
import kotlinx.coroutines.launch

class DashboardPicSelect : Fragment() {

    private var _binding: FragmentDashboardSelectBinding? = null

    private var picID : Int = -1
    private var picRValue: Int = -1
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val errorImage = R.drawable.error

    private lateinit var dashboardViewModel: DashboardPicSelectViewModel


    val drawableList : MutableList<Drawable?> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        dashboardViewModel =
            ViewModelProvider(this).get(DashboardPicSelectViewModel::class.java)
        dashboardViewModel.pictureName = "ToTest"
        _binding = FragmentDashboardSelectBinding.inflate(inflater, container, false)
        val root: View = binding.root

        picID = arguments?.getInt("imageId") ?: -1
        picRValue = arguments?.getInt("pictureNumber") ?: errorImage

        dashboardViewModel.editTextReflectTrueName(picID)


        binding.ImageHold.setImageResource(picRValue)
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

                    val overlay = ContextCompat.getDrawable(requireContext(), R.drawable.locationicon)
                    Log.d("ARROW", "ASSET POSITION: ${TopLeftX}, ${TopLeftY}" +
                            " MOTION EVENT: ${motionEvent.x}, ${motionEvent.y}")
                    createNewIcon(motionEvent.x.toInt() -15, motionEvent.y.toInt() -30) //0,0 top left, 900 950 bottom right
                    /*
                     val overlayDrawable = InsetDrawable(overlay, 600, 450, 600, 450)
                     //Motion event: 215, 200: top left corner
                     //Motion event 1200, 213: top right corner
                     //Motion event  215, 1200 Bottom Left corner
                     //Motion event  1200, 215 Bottom right corner
                     */
                }



                return true
            }

        })
    }

    fun createNewIcon (x : Int, y: Int ) {
        val overlay = binding.constraintOverlay

        val newIcon = ImageView(requireContext())
        newIcon.setImageResource(R.drawable.locationicon)
        newIcon.id = View.generateViewId()

        val params = ConstraintLayout.LayoutParams(128,128)
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.marginStart = x  // X offset
        params.topMargin = y    // Y offset

        overlay.addView(newIcon, params)
    }

    fun SetImageMarkers(LDraw : LayerDrawable) : LayerDrawable {
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
