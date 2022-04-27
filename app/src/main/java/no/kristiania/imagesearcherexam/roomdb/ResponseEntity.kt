package no.kristiania.imagesearcherexam.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import no.kristiania.imagesearcherexam.api.JsonResponseModel
import java.sql.Types.BLOB

@Entity(tableName = "result-table")
data class ResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "Original Image", typeAffinity = ColumnInfo.BLOB)
    val searchedImage: ByteArray,
    @ColumnInfo(name = "result-one")
    val resultOne: String? = "",
    @ColumnInfo(name = "result-two")
    val resultTwo: String? = "",
    @ColumnInfo(name = "result-three")
    val resultThree: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseEntity

        if (!searchedImage.contentEquals(other.searchedImage)) return false

        return true
    }

    override fun hashCode(): Int {
        return searchedImage.contentHashCode()
    }
}