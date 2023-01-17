package tech.fernandooliveira.netflix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.fernandooliveira.netflix.model.Category
import tech.fernandooliveira.netflix.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progressMain: ProgressBar
    private lateinit var adapter: CategoryAdapter
    private val categories = mutableListOf<Category>()

    // m-v-c (model - [view/controller] -> activity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressMain = findViewById(R.id.progress_main)

        adapter = CategoryAdapter(categories) { id ->
            val intent = Intent(this@MainActivity, MovieActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=1086a7bc-65e5-4607-9b1a-d331f5df6b66")
    }

    override fun onPreExecute() {
        progressMain.visibility = View.VISIBLE
    }

    override fun onResult(categories: List<Category>) {
        Log.i("MainActivity", "Categories: $categories")
        this.categories.clear()
        this.categories.addAll(categories)
        adapter.notifyDataSetChanged()

        progressMain.visibility = View.GONE
    }

    override fun onError(message: String) {
        progressMain.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}