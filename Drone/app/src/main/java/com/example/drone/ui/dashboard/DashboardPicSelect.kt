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
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.drone.R
import com.example.drone.databinding.FragmentDashboardSelectBinding
import kotlinx.coroutines.launch

class DashboardPicSelect : Fragment() {

    private var _binding: FragmentDashboardSelectBinding? = null

    private var picID : Int = -1
    private var picRValue: Int = -1
    private val binding get() = _binding!!
    private val errorImage = R.drawable.error
    private var livePins : LiveData<List<PictureInfo>>? = null

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
        dashboardViewModel.editTextReflectTrueName(picID)
        //Set up the dashboard view model

        val root: View = binding.root

        picID = arguments?.getInt("imageId") ?: -1
        picRValue = arguments?.getInt("pictureNumber") ?: errorImage

        //works using the given pic number
        binding.ImageHold.setImageResource(picRValue)

        val pinObserver = Observer<List<PictureInfo>?> { pins ->
            Log.d("CHECK REACHED", "PIN OBSERVER IS WORKING")
            if (pins != null) {
                for (pin in pins!!) {
                    if (pin.onPicX == null || pin.onPicY == null) {
                        Log.d("ERROR NULL", "A Choordinate was null, can not be shown")
                    } else {
                        createNewIcon(
                            pin.onPicX!!.toInt(), pin.onPicY!!.toInt()
                        )
                    }
                }
            }
        }

        dashboardViewModel.updatePicLivedata().observe(viewLifecycleOwner, pinObserver)
        //make the observer here
        //once observer is linked a change can be found


        drawableList.add(binding.ImageHold.drawable)

        binding.HomeBTN.setOnClickListener{
            this.requireView().findNavController().navigate(
                R.id.action_navigation_selector_to_navigation_dashboard)

        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or handle manually
            }
        });

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ImageHold.setOnTouchListener( object : View.OnTouchListener{
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    view.performClick()
                    dashboardViewModel.IdLastInServer = null
                    dashboardViewModel.SaveClicked(motionEvent.x, motionEvent.y)

                    // Create and show alert with cancel listener
                    val alert = DashboardAlert()
                    alert.setOnCancelListener {
                        handleAlertCancelled()
                    }
                    alert.show(parentFragmentManager, "Dash Alert")

                    val TopLeftX = binding.ImageHold.x
                    val TopLeftY =  binding.ImageHold.y

                    val overlay = ContextCompat.getDrawable(requireContext(), R.drawable.locationicon)
                    Log.d("ARROW", "ASSET POSITION: ${TopLeftX}, ${TopLeftY}" +
                            " MOTION EVENT: ${motionEvent.x}, ${motionEvent.y}")
    //                createNewIcon(motionEvent.x.toInt() -15, motionEvent.y.toInt() -30)
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
        dashboardViewModel.IdLastInServer = newIcon.id

        val params = ConstraintLayout.LayoutParams(128,128)
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.marginStart = x  // X offset
        params.topMargin = y    // Y offset

        overlay.addView(newIcon, params)
    }

    // This function will be called when alert is cancelled (back button pressed)
    private fun handleAlertCancelled() {
        Log.d("DashboardAlert", "Alert was cancelled, removing icon")
        deleteNewlyCreatedIcon()
    }

    fun deleteNewlyCreatedIcon(){
        dashboardViewModel.cancelPinPressed()

        // Fixed: Use binding.constraintOverlay.findViewById instead of just findViewById
        dashboardViewModel.IdLastInServer?.let { iconId ->
            val toRemove = binding.constraintOverlay.findViewById<ImageView>(iconId)
            toRemove?.let {
                binding.constraintOverlay.removeView(it)
                Log.d("DashboardAlert", "Removed icon with ID: $iconId")
            }
        }

        // Clear the ID after removal
        dashboardViewModel.IdLastInServer = null
    }

    fun SetImageMarkersOnStart(LDraw : LayerDrawable) : LayerDrawable {
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