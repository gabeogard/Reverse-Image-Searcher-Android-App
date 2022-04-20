package no.kristiania.imagesearcherexam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import no.kristiania.imagesearcherexam.databinding.ActivityResultsBinding
import no.kristiania.imagesearcherexam.roomdb.ResponseApp
import no.kristiania.imagesearcherexam.roomdb.ResponseDAO
import java.io.IOException
import java.net.URL

class ResultsActivity : AppCompatActivity() {
    private var binding : ActivityResultsBinding? = null
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val dao = (application as ResponseApp).db.responseDao()

        val url = "https://th.bing.com/th/id/OIP.vRPe7z_28WfbewcA5u4aNQHaHa?pid=ImgDet&w=211&h=211&c=7"
        val urlImage = URL(url)
        val result : Deferred<Bitmap?> = GlobalScope.async {
            urlImage.toBitmap()
        }

        val resultList : Deferred<ArrayList<ResultItem>?> = GlobalScope.async {
            dao.getList()
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding?.resultIv?.setImageBitmap(result.await())
        }


    }


}

private suspend fun ResponseDAO.getList(): ArrayList<ResultItem>? {
    val list : ArrayList<ResultItem>? = null
    fetchAllResponses().collect {
        for (i in it){
            val urlOne = URL(i.resultOne)
            val urlTwo = URL(i.resultTwo)
            val urlThree = URL(i.resultThree)
            val resultOne : Bitmap? = urlOne.toBitmap()
            val resultTwo : Bitmap? = urlTwo.toBitmap()
            val resultThree : Bitmap? = urlThree.toBitmap()
            val resultItem = ResultItem(i.searchedImage, resultOne, resultTwo, resultThree)
            list?.add(resultItem)
        }
    }
    return list
}

private fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    }catch (e: IOException){
        null
    }
}
