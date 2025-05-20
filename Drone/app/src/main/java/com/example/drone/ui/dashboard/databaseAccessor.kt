package com.example.drone.ui.dashboard
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity(tableName = "Pictures")
data class Pictures(
    @PrimaryKey(autoGenerate = true)
    val picNumber: Int = 0, // changed to match reference from PictureInfo
    val picName: String
)

@Entity(tableName = "SelectedPins", primaryKeys = ["pinNumber", "picNumber"])
data class PictureInfo(
    var pinNumber: Int,
    var picNumber: Int,
    var onPicX: Float?,
    var onPicY: Float?,
    var chordX: Float?,
    var chordY: Float?,
)

@Dao
interface PicturesDao {
    @Insert
    suspend fun insert(p: Pictures)

    @Update
    suspend fun update(p: Pictures)

    @Delete
    suspend fun delete(p: Pictures)

    @Query("SELECT * FROM Pictures")
    fun getAll(): LiveData<List<Pictures>>

    @Query("SELECT picNumber FROM Pictures WHERE picName = :name")
    suspend  fun getIdForName(name:String) : Int?

    @Query("SELECT picName FROM Pictures WHERE picNumber = :number")
    suspend fun getNameForId(number: Int) : String?
}

@Dao
interface SelectedDao {
    @Insert
    suspend fun insert(p: PictureInfo)

    @Update
    suspend fun update(p: PictureInfo)

    @Delete
    suspend fun delete(p: PictureInfo)

    @Query("""
        SELECT SelectedPins.* FROM SelectedPins 
        INNER JOIN Pictures ON SelectedPins.picNumber = Pictures.picNumber 
        WHERE Pictures.picNumber = :picNum
    """)
    fun getAllIconLocations(picNum: Int): LiveData<List<PictureInfo>>
    @Query("""
        SELECT * FROM SelectedPins WHERE picNumber = :picNum AND pinNumber = :pinNum
    """)
    suspend fun GetPinLocation(picNum: Int, pinNum: Int): PictureInfo

    @Query("""
        SELECT MAX(pinNumber) FROM SelectedPins WHERE picNumber = :picNum
    """)
    suspend fun GetMaxPin(picNum: Int) : Int

}

@Database(entities = [Pictures::class, PictureInfo::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun picturesDao(): PicturesDao
    abstract fun selectedDao(): SelectedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drone-content"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
