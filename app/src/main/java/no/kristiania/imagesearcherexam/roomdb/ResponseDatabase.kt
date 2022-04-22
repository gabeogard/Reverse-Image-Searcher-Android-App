package no.kristiania.imagesearcherexam.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ResponseEntity::class], version = 4)
abstract class ResponseDatabase : RoomDatabase() {
    abstract fun responseDao(): ResponseDAO

    companion object {
        @Volatile
        private var INSTANCE: ResponseDatabase? = null

        fun getInstance(context: Context): ResponseDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ResponseDatabase::class.java,
                        "response-db"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}