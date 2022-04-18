package no.kristiania.imagesearcherexam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import no.kristiania.imagesearcherexam.databinding.ActivityResultsBinding
import java.io.IOException
import java.net.URL

class ResultsActivity : AppCompatActivity() {
    private var binding : ActivityResultsBinding? = null
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        val url = "https://th.bing.com/th/id/OIP.vRPe7z_28WfbewcA5u4aNQHaHa?pid=ImgDet&w=211&h=211&c=7"
        val urlImage = URL(url)
        val result : Deferred<Bitmap?> = GlobalScope.async {
            urlImage.toBitmap()
        }
        GlobalScope.launch(Dispatchers.Main) {
            binding?.resultIv?.setImageBitmap(result.await())
        }
    }


}

private fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    }catch (e: IOException){
        null
    }
}
