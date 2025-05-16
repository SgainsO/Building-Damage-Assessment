package com.example.drone.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.constraintlayout.motion.widget.Debug
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val database = databaseAccessor(application)
    // https://medium.com/@harimoradiya123/getting-started-with-room-database-in-android-using-kotlin-92f84b6a5e6c


    private var LocationData = mutableMapOf("imageChord" to listOf(0f,0f), "chord" to listOf(0f,0f))
    val LocationInfoArray : MutableList<MutableMap<String, List<Float>>> = mutableListOf()

    val text: LiveData<String> = _text

    fun SaveClicked(x_clicked: Float, y_clicked:Float)
    {
        Log.d("INPUT","Tap On: ($x_clicked, $y_clicked)")
        LocationData["imageChord"] = listOf(x_clicked, y_clicked)
    }

    fun SaveChords(x: Float, y : Float)
    {
        Log.d("INPUT","Saved Location: ($x, $y)")
        LocationData["chord"] = listOf(x, y)
        LocationInfoArray.add(LocationData)
        LocationData = mutableMapOf("imageChord" to listOf(0f,0f), "chord" to listOf(0f,0f))
        //TO DO: Send to a database
    }



}