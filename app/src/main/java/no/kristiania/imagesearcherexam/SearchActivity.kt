package no.kristiania.imagesearcherexam

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.imagesearcherexam.databinding.ActivitySearchBinding
import java.io.File
import java.net.URI

class SearchActivity : AppCompatActivity() {
    private var binding: ActivitySearchBinding? = null
    private var customProgressDialog : Dialog? = null
    private var currentSearchedImage : String? = null
    private var uploadedPic : Boolean? = null
    private var currentUrl : String? = null

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode == RESULT_OK && result.data != null){
                currentSearchedImage = result?.data.toString()
                uploadedPic = true
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.searchBtn?.setOnClickListener {
            val checkSelfPermission = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                Toast.makeText(this, "You need permission", Toast.LENGTH_LONG).show()
                openGallery()
                if (uploadedPic!!){
                    uploadImage()
                }
            } else{
                openGallery()
                if (uploadedPic!!){
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage() {
        val uploadFile = File(currentSearchedImage!!)
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            AndroidNetworking.upload("http://api-edu.gtl.ai/api/v1/imagesearch/upload")
                .addMultipartFile("image", uploadFile)
                .addMultipartParameter("Content-Type", "image/jpeg")
                .addMultipartParameter("Content-Disposition", "form-data")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object : StringRequestListener{
                    override fun onResponse(response: String?) {
                        currentUrl = response
                        Log.d("Response: ", response!!)
                        cancelProgressDialog()
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Something went wrong:", anError.toString())
                    }

                })
        }
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
        if(customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }
}