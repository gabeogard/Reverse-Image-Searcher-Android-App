package no.kristiania.imagesearcherexam

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.room.Room
import no.kristiania.imagesearcherexam.databinding.ActivityMainBinding
import no.kristiania.imagesearcherexam.databinding.DialogInfoBinding
import no.kristiania.imagesearcherexam.roomdb.ResponseApp
import no.kristiania.imagesearcherexam.roomdb.ResponseDatabase

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.searchBtn?.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        binding?.savedResultsBtn?.setOnClickListener {
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }

        binding?.infoFab?.setOnClickListener {
            infoDialog()
        }
    }

    private fun infoDialog() {
        val infoDialog = Dialog(this)
        val binding = DialogInfoBinding.inflate(layoutInflater)
        infoDialog.setContentView(binding.root)
        binding.tvInfo.text = Constants.infoSection()
        infoDialog.setCancelable(true)
        infoDialog.show()
    }
}