package no.kristiania.imagesearcherexam.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import no.kristiania.imagesearcherexam.api.JsonResponseModel

@Entity(tableName = "result-table")
data class ResponseEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "Original Image")
    val searchedImage: String = "",
    @ColumnInfo(name = "result-one")
    val resultOne: String? = "",
    @ColumnInfo(name = "result-two")
    val resultTwo: String? = "",
    @ColumnInfo(name = "result-three")
    val resultThree: String? = ""
)