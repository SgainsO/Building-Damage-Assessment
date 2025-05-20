package com.example.drone.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DashboardPicSelectViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val database = AppDatabase.getDatabase(application)

    init {
        resetPinNumber()
    }

    // https://medium.com/@harimoradiya123/getting-started-with-room-database-in-android-using-kotlin-92f84b6a5e6c

    var currentPinNumber = 0

    val text: LiveData<String> = _text

    fun resetPinNumber()
    {
        viewModelScope.launch {
            currentPinNumber = database.selectedDao().GetMaxPin(-1) + 1
        }
    }
    private var LocationData = mutableMapOf("imageChord" to listOf(0f,0f), "chord" to listOf(0f,0f))
    val LocationInfoArray : MutableList<MutableMap<String, List<Float>>> = mutableListOf()

    var pictureName = ""


    fun SaveClicked(x_clicked: Float, y_clicked:Float)
    {
        Log.d("INPUT","Tap On: ($x_clicked, $y_clicked)")
        LocationData["imageChord"] = listOf(x_clicked, y_clicked)

        viewModelScope.launch {
            val newPictureIntoData = PictureInfo(
                currentPinNumber,
                database.picturesDao().getIdForName(pictureName) ?: -1,
                x_clicked,
                y_clicked,
                0f,
                0f
            )
            database.selectedDao().insert(newPictureIntoData)
            Log.d("Thread data check", "Finished")
        }
    }

    fun SaveChords(x: Float, y : Float)
    {
        //Chords refer to the Longitude Latitude
        Log.d("INPUT","Saved Location: ($x, $y)")
        LocationData["chord"] = listOf(x, y)
        LocationInfoArray.add(LocationData)
        LocationData = mutableMapOf("imageChord" to listOf(0f,0f), "chord" to listOf(0f,0f))

        Log.d("before", "make")

        viewModelScope.launch {
            val picId = database.picturesDao().getIdForName(pictureName) ?: -1
            Log.d("THREAD", "picID: $picId pointID: $currentPinNumber")
            val halfLocation = database.selectedDao().GetPinLocation(picId, currentPinNumber - 1)

            halfLocation.let {
                it.chordX = x
                it.chordY = y
                database.selectedDao().update(it)
            }
            currentPinNumber += 1
        }
            //The thing will crash
    //TO DO: Send to a database
    }
    fun editTextReflectTrueName(picID: Int)
    {
        viewModelScope.launch {
            _text.postValue( database.picturesDao().getNameForId(picID) ?: "empty")
        }
    }

}