package no.kristiania.imagesearcherexam.roomdb

import android.app.Application
import androidx.room.RoomDatabase
import no.kristiania.imagesearcherexam.roomdb.ResponseDatabase.Companion.getInstance

class ResponseApp: Application() {
    val db by lazy {
        getInstance(this)
    }
}