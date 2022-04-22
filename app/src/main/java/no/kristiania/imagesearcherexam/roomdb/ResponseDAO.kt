package no.kristiania.imagesearcherexam.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResponseDAO {

    @Insert
    suspend fun insert(responseEntity: ResponseEntity)

    @Delete
    suspend fun delete(responseEntity: ResponseEntity)

    @Query("SELECT * FROM `result-table`")
    fun fetchAllResponses(): Flow<List<ResponseEntity>>

    @Query("SELECT * FROM `result-table` where id=:id")
    fun fetchAllResponsesById(id: Int): Flow<ResponseEntity>


}
