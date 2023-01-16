package tech.fernandooliveira.netflix

import android.annotation.SuppressLint
import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import tech.fernandooliveira.netflix.model.Movie

class MovieActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movieTitle: TextView = findViewById(R.id.movie_title)
        val movieDescription: TextView = findViewById(R.id.movie_description)
        val movieCasting: TextView = findViewById(R.id.movie_cast)
        val similarOptionsRv: RecyclerView = findViewById(R.id.similar_options_recycler)

        movieTitle.text = getString(R.string.title_tool)
        movieDescription.text = getString(R.string.description_tool)
        movieCasting.text = getString(R.string.cast, "Christian Bale, Michael Caine, Liam Neeson")


        val movies = mutableListOf<Movie>()
        for (i in 0 until 15) {
            val movie = Movie(R.drawable.movie)
            movies.add(movie)
        }

        val adapter = MovieAdapter(movies, R.layout.similar_movie_item)
        similarOptionsRv.layoutManager = GridLayoutManager(this@MovieActivity, 3)
        similarOptionsRv.adapter = adapter


        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        val layerDrawable: LayerDrawable =
            ContextCompat.getDrawable(this, R.drawable.shadows) as LayerDrawable
        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4)
        val coverImg: ShapeableImageView = findViewById(R.id.movie_image)

        layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
        coverImg.setImageDrawable(layerDrawable)
    }
}