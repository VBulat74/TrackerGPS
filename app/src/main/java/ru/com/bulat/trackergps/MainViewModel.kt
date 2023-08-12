package ru.com.bulat.trackergps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.com.bulat.trackergps.db.MainDB
import ru.com.bulat.trackergps.db.TrackItem
import ru.com.bulat.trackergps.location.LocationModel

class MainViewModel (db : MainDB) : ViewModel() {

    val dao = db.getDao()

    val locationUpdate = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()

    val tracks = dao.getAllTracks().asLiveData()

    fun insertTrack (trackItem: TrackItem) = viewModelScope.launch {
        dao.insertTrack(trackItem)
    }

    fun deleteTrack(trackItem: TrackItem) = viewModelScope.launch {
        dao.deleteTrack(trackItem)
    }

    class ViewModelFactory (private val db: MainDB) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(db) as T
            }
            throw IllegalArgumentException ("Uncnow ViewModel class!")
        }
    }

}