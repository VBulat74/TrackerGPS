package ru.com.bulat.trackergps

import android.app.Application
import ru.com.bulat.trackergps.db.MainDB

class MainApp : Application() {
    val database by lazy {
        MainDB.getDataBase(this)
    }
}