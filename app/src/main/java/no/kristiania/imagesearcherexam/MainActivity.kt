package no.kristiania.imagesearcherexam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_results.*
import no.kristiania.imagesearcherexam.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    private lateinit var pageAdapter: RecylerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initRecyclerView()
        addDataSet()


        binding?.searchBtn?.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }
    private fun addDataSet() {
        val data = DataSource.createDataSet()
        pageAdapter.submitList(data)
    }

    private fun initRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this@MainActivity)
        pageAdapter = RecylerAdapter()
        recycler_view.adapter = pageAdapter
    }
}