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

@Entity(tableName = "SelectedPins")
data class PictureInfo(
    @PrimaryKey(autoGenerate = true)
    val pinNumber: Int = 0,
    val picNumber: Int,
    val onPicX: Float?,
    val onPicY: Float?,
    val chordX: Float?,
    val chordY: Float?,
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
    fun getIdForName(name:String) : Int
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
}

@Database(entities = [Pictures::class, PictureInfo::class], version = 1)
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
                    "pictures_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

public class databaseAccessor(context : Context)
{
    val database = AppDatabase.getDatabase(context)
}