package ru.com.bulat.trackergps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.com.bulat.trackergps.location.LocationModel

class MaimViewModel : ViewModel() {

    val locationUpdate = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()

}