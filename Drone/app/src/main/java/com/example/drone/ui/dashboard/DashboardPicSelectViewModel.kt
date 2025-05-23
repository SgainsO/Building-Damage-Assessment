package com.example.drone.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import android.widget.ImageView
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger


class DashboardPicSelectViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val database = AppDatabase.getDatabase(application)

    val currentPinNumber = AtomicInteger(0)

    fun resetPinNumber()
    {
        viewModelScope.launch {
            picId = database.picturesDao().getIdForName(pictureName) ?: -1
        }
    }

    init {
    }

    // https://medium.com/@harimoradiya123/getting-started-with-room-database-in-android-using-kotlin-92f84b6a5e6c

    var IdLastInServer: Int? = null

    val text: LiveData<String> = _text

    val pins: MutableLiveData<List<PictureInfo>?> = MutableLiveData()


    var picId: Int = -1
    //To Do, make a function that will allow the frontend to populate this value at stand by

    private var LocationData = mutableMapOf("imageChord" to listOf(0f,0f), "chord" to listOf(0f,0f))
    val LocationInfoArray : MutableList<MutableMap<String, List<Float>>> = mutableListOf()

    var pictureName = ""


    fun SaveClicked(x_clicked: Float, y_clicked:Float)
    {
        Log.d("INPUT","Tap On: ($x_clicked, $y_clicked)")
        LocationData["imageChord"] = listOf(x_clicked, y_clicked)

        viewModelScope.launch {
            Log.d("pointID", "${currentPinNumber.get()}")
            val newPictureIntoData = PictureInfo(
                database.selectedDao().GetMaxPin(-1) + 1,
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
        val pinNum = currentPinNumber.get()
        Log.d("before", "make")

        viewModelScope.launch {
            Log.d("THREAD", "picID: $picId pointID: ${pinNum}")

            val halfLocation = database.selectedDao().GetPinLocation(picId,
                database.selectedDao().GetMaxPin(-1))

            halfLocation.let {
                it.chordX = x
                it.chordY = y
                database.selectedDao().update(it)
            }
            // + 1
        }
        //The thing will crash
        //TO DO: Send to a database
    }

    fun updatePicLivedata() : LiveData<List<PictureInfo>>
    {
        return database.selectedDao().getAllIconLocations(picId)
    }


    fun cancelPinPressed()
    {   //For when the cancel button is being pressed
        viewModelScope.launch {
            database.selectedDao().delete(
                database.selectedDao().GetPinLocation(
                    picId,
                    database.selectedDao().GetMaxPin(-1)
                )
            )

        }
    }

    fun editTextReflectTrueName(piczID: Int)
    {
        viewModelScope.launch {
            _text.postValue( database.picturesDao().getNameForId(piczID) ?: "empty")
        }
    }

}