package no.kristiania.imagesearcherexam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.kristiania.imagesearcherexam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
}