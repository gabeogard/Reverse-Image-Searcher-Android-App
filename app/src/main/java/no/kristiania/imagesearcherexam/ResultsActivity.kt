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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title= "Image Searcher"

        val dao = (application as ResponseApp).db.responseDao()

        CoroutineScope(Dispatchers.IO).launch {
            dao.fetchAllResponses().collect {
                val resultList: List<ResultItem> =
                    it.map { response ->
                        ResultItem(response.searchedImage.toBitmap(),
                            URL(response.resultOne).toBitmap(),
                            URL(response.resultTwo).toBitmap(),
                            URL(response.resultThree).toBitmap())
                    }.toList()

                for (i in resultList){
                    Log.d("LINK", i.resultOne.toString())
                }
                lifecycleScope.launch {
                    setUpList(resultList, dao)
                }
            }
        }

    }

    private fun setUpList(list: List<ResultItem>, dao: ResponseDAO) {
        if (list.isNotEmpty()) {
            val itemAdapter = ResultItemAdapter(list) { imageView, title ->
                fullScreenDialog(imageView, title)
            }
            binding?.rvResults?.layoutManager = LinearLayoutManager(this)
            binding?.rvResults?.adapter = itemAdapter
            binding?.rvResults?.visibility = View.VISIBLE
        } else {
            binding?.rvResults?.visibility = View.GONE
            Toast.makeText(this, "No data stored, try again later", Toast.LENGTH_SHORT).show()
        }
    }

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

private fun ByteArray.toBitmap(): Bitmap =
    kotlin.runCatching { BitmapFactory.decodeByteArray(this, 0, this.size) }.getOrThrow()

private fun URL.toBitmap(): Bitmap =
    kotlin.runCatching { BitmapFactory.decodeStream(openStream()) }.getOrThrow()
