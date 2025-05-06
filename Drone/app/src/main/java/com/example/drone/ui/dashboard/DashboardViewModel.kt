package com.example.drone.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun SaveCoordinates (x_clicked: Int, y_clicked:Int, long:Double, lat:Double)
    {
        println("Tap On: ($x_clicked, $y_clicked)")
        println("Entered Long: ($long, $lat)")
    }
}