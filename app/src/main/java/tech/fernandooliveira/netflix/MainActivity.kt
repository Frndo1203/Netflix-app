package tech.fernandooliveira.netflix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tech.fernandooliveira.netflix.model.Category
import tech.fernandooliveira.netflix.model.Movie

class MainActivity : AppCompatActivity() {

    // m-v-c (model - [view/controller] -> activity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = mutableListOf<Category>()
        for (j in 0 until 5) {
            val movies = mutableListOf<Movie>()
            for (i in 0 until 15) {
                val movie = Movie(R.drawable.movie)
                movies.add(movie)
            }
            val category = Category("Category $j", movies)
            categories.add(category)
        }


        val adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }
}