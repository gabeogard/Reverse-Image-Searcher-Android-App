package no.kristiania.imagesearcherexam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import no.kristiania.imagesearcherexam.databinding.ActivityResultsBinding
import no.kristiania.imagesearcherexam.roomdb.ResponseApp
import no.kristiania.imagesearcherexam.roomdb.ResponseDAO
import no.kristiania.imagesearcherexam.roomdb.ResponseEntity
import java.io.IOException
import java.net.URL

class ResultsActivity : AppCompatActivity() {
    private var binding: ActivityResultsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val dao = (application as ResponseApp).db.responseDao()


        val resultList: Deferred<List<ResultItem>> = GlobalScope.async {
            dao.getList()
        }


        GlobalScope.launch(Dispatchers.Main) {
            val list: List<ResultItem> = resultList.await()
            Log.d("Empty", list.toString())
            setUpList(list)
        }


    }

    private fun setUpList(list: List<ResultItem>) {
        if (list.isNotEmpty()) {
            val itemAdapter = ResultItemAdapter(list)
            binding?.rvResults?.adapter = itemAdapter
            binding?.rvResults?.layoutManager = LinearLayoutManager(this)
            binding?.rvResults?.visibility = View.VISIBLE
        } else {
            binding?.rvResults?.visibility = View.GONE
            Toast.makeText(this, "No data stored, try again later", Toast.LENGTH_SHORT).show()
        }
    }


}

private suspend fun ResponseDAO.getList(): List<ResultItem> =
    fetchAllResponses().flatMapConcat { responseList ->
        responseList.asFlow().map { response ->
            if (!response.resultOne.isNullOrEmpty()){
                val urlOne = URL(response.resultOne)
                val urlTwo = URL(response.resultTwo)
                val urlThree = URL(response.resultThree)
                val resultOne: Bitmap? = urlOne.toBitmap()
                val resultTwo: Bitmap? = urlTwo.toBitmap()
                val resultThree: Bitmap? = urlThree.toBitmap()
                ResultItem(response.searchedImage,resultOne,resultTwo,resultThree)
            }else{
                ResultItem(response.searchedImage, null, null, null)
            }
        }
    }.toList()


private fun URL.toBitmap(): Bitmap? = kotlin.runCatching { BitmapFactory.decodeStream(openStream()) }.getOrNull()
