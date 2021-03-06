package no.kristiania.imagesearcherexam

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import no.kristiania.imagesearcherexam.databinding.ActivityResultsBinding
import no.kristiania.imagesearcherexam.databinding.DialogFullscreenBinding
import no.kristiania.imagesearcherexam.roomdb.ResponseApp
import no.kristiania.imagesearcherexam.roomdb.ResponseDAO
import java.net.URL

class ResultsActivity : AppCompatActivity() {
    private var binding: ActivityResultsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val dao = (application as ResponseApp).db.responseDao()

        //Creates List from stored results and sends it to recycler view
        CoroutineScope(Dispatchers.IO).launch {
            dao.fetchAllResponses().collect {
                val resultList: List<ResultItem> =
                    it.map { response ->
                        ResultItem(
                            response.searchedImage.toBitmap(),
                            URL(response.resultOne).toBitmap(),
                            URL(response.resultTwo).toBitmap(),
                            URL(response.resultThree).toBitmap()
                        )
                    }.toList()

                lifecycleScope.launch {
                    setUpList(resultList, dao)
                }
            }
        }

    }

    //Sets up recyclerview with stored image search results
    private fun setUpList(list: List<ResultItem>, dao: ResponseDAO) {
        if (list.isNotEmpty()) {
            val itemAdapter = ResultItemAdapter(list, { imageView, title ->
                fullScreenDialog(imageView, title)
            })
            binding?.rvResults?.layoutManager = LinearLayoutManager(this)
            binding?.rvResults?.adapter = itemAdapter
            binding?.rvResults?.visibility = View.VISIBLE
        } else {
            binding?.rvResults?.visibility = View.GONE
            Toast.makeText(this, "No data stored, try again later", Toast.LENGTH_SHORT).show()
        }
    }

    //Dialog box for fullscreen Image Views
    private fun fullScreenDialog(view: ImageView, title: String) {
        val fullScreenDialog = Dialog(this, R.style.Theme_Dialog)
        fullScreenDialog.setCancelable(true)
        val binding = DialogFullscreenBinding.inflate(layoutInflater)
        fullScreenDialog.setContentView(binding.root)
        binding.ivFullScreenImage.setImageDrawable(view.drawable)
        binding.tvTitle.text = title
        fullScreenDialog.show()
    }
}


//Turns images stored on device from ByteArray to Bitmap (Throws if something is wrong)
private fun ByteArray.toBitmap(): Bitmap =
    kotlin.runCatching { BitmapFactory.decodeByteArray(this, 0, this.size) }.getOrThrow()

//Turns image URL's stored on device from URL to Bitmap (Throws if something is wrong)
private fun URL.toBitmap(): Bitmap =
    kotlin.runCatching { BitmapFactory.decodeStream(openStream()) }.getOrThrow()
