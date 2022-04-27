package no.kristiania.imagesearcherexam

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.*
import no.kristiania.imagesearcherexam.api.JsonResponseModel
import no.kristiania.imagesearcherexam.databinding.ActivitySearchBinding
import no.kristiania.imagesearcherexam.roomdb.ResponseApp
import no.kristiania.imagesearcherexam.roomdb.ResponseDAO
import no.kristiania.imagesearcherexam.roomdb.ResponseEntity
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.NullPointerException

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    private var customProgressDialog: Dialog? = null
    private var currentSearchedImage: String? = null
    private var uploadedPic = false
    private var responseList: List<JsonResponseModel>? = null
    private var responseEntry: ResponseEntity? = null

    //Launches gallery and sets image to invisible View
    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                currentSearchedImage = result?.data.toString()
                val uploadImage: ImageView = findViewById(R.id.imgSearchHolder)
                uploadImage.setImageURI(result.data?.data)
                binding?.imgSearchHolder?.visibility = View.VISIBLE
                uploadedPic = true
                binding?.uploadBtn?.isEnabled = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Image Searcher"


        val db = (application as ResponseApp).db

        val dao = db.responseDao()

        /**
         * -Checks for and or asks for permission to read external storage on device
         * -Opens gallery
         * **/

        binding?.chooseBtn?.setOnClickListener {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1
                )
                Toast.makeText(this, "You need permission", Toast.LENGTH_LONG).show()
                openGallery()
            } else {
                openGallery()
            }
        }

        binding?.uploadBtn?.setOnClickListener {
            if (uploadedPic) {
                showProgressDialog()
                val resultUrl: Deferred<String> = GlobalScope.async { uploadImage() }
                GlobalScope.launch(Dispatchers.Main) {
                    resultUrl.await().run {
                        if (this.isNotEmpty()){
                            imageSearch(this)
                        }else{
                            cancelProgressDialog()
                            Toast.makeText(this@SearchActivity,
                                "Failed to upload - please try again with a different picture",
                                Toast.LENGTH_LONG).show()
                        }}
                    it.isEnabled = true
                }
                it.isEnabled = false
            }
        }

        binding?.saveResultsBtn?.setOnClickListener {
            if (responseEntry != null) saveSearchResults(dao)
            it.visibility = View.GONE
        }

        binding?.viewResultsBtn?.setOnClickListener {
            intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveSearchResults(dao: ResponseDAO) {
        lifecycleScope.launch {
            responseEntry?.run { dao.insert(this) }
        }
    }


    //Uploads selected image to server and returns img url
    private fun uploadImage(): String {
        val uploadImage: ImageView = findViewById(R.id.imgSearchHolder)
        val f = createImageFromBitmap(getBitmapFromView(uploadImage))
        val uploadFile = File(f)
        val returned: String = try {
            AndroidNetworking.upload("http://api-edu.gtl.ai/api/v1/imagesearch/upload")
                .addMultipartFile("image", uploadFile)
                .addMultipartParameter("Content-Type", "image/png")
                .addMultipartParameter("Content-Disposition", "form-data")
                .setPriority(Priority.HIGH)
                .build()
                .executeForString().result.toString()
        }catch (e: NullPointerException){
            ""
        }
        return returned
    }

    //Sends returned image url from file upload to API that returns similar images
    private fun imageSearch(currentPicUrl: String) {
        val uploadImage: ImageView = findViewById(R.id.imgSearchHolder)
        val f = File(createImageFromBitmap(getBitmapFromView(uploadImage)))
        AndroidNetworking.get("http://api-edu.gtl.ai/api/v1/imagesearch/bing")
            .addQueryParameter("url", currentPicUrl)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray?) {
                    Log.d("Response: ", currentPicUrl)
                    val mapper = jacksonObjectMapper()
                    responseList = mapper.readValue(response.toString())
                    val resultList: ArrayList<JsonResponseModel> = ArrayList()
                    if (responseList!!.isNotEmpty()) {
                        responseList?.forEach {
                            resultList.add(it)
                            Log.d("Top result", it.image_link)
                        }
                        responseEntry = ResponseEntity(
                            searchedImage = f.readBytes(),
                            resultOne = resultList[0].image_link,
                            resultTwo = resultList[1].image_link,
                            resultThree = resultList[2].image_link
                        )
                        binding?.saveResultsBtn?.visibility = View.VISIBLE
                        cancelProgressDialog()
                    } else {
                        Toast.makeText(
                            this@SearchActivity,
                            "No similar images found - Server might be facing some" +
                                    " issues. Please try again!",
                            Toast.LENGTH_LONG
                        ).show()
                        cancelProgressDialog()
                    }
                    binding?.viewResultsBtn?.visibility = View.VISIBLE

                }

                override fun onError(anError: ANError?) {
                    Log.e("Error", anError.toString())
                    cancelProgressDialog()
                }
            })
    }

    private fun createImageFromBitmap(mBitmap: Bitmap): String {
        val result1: String
        val bytes = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 20, bytes)
        } else {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 20, bytes)
        }
        val f = File(
            externalCacheDir?.absoluteFile.toString()
                    + File.separator
                    + "PP"
                    + ".png"
        )
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()

        result1 = f.absolutePath
        return result1
    }

    private fun getBitmapFromView(img: ImageView): Bitmap {
        val returnedBitmap =
            Bitmap.createBitmap(
                img.drawable.intrinsicWidth,
                img.drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(returnedBitmap)
        img.draw(canvas)
        return returnedBitmap
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        openGalleryLauncher.launch(intent)
    }

    private fun showProgressDialog() {
        customProgressDialog = Dialog(this)
        customProgressDialog?.setContentView(R.layout.custom_progress_dialog)
        customProgressDialog?.setCancelable(false)
        customProgressDialog?.show()
    }

    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }
}