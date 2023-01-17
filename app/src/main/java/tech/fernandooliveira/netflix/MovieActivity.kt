package tech.fernandooliveira.netflix

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import tech.fernandooliveira.netflix.model.Movie
import tech.fernandooliveira.netflix.model.MovieDetail
import tech.fernandooliveira.netflix.util.MovieTask

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var movieTitle: TextView
    private lateinit var movieDescription: TextView
    private lateinit var movieCasting: TextView
    private lateinit var adapter: MovieAdapter
    private lateinit var progressMovie: ProgressBar
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        movieTitle = findViewById(R.id.movie_title)
        movieDescription = findViewById(R.id.movie_description)
        movieCasting = findViewById(R.id.movie_cast)
        progressMovie = findViewById(R.id.progress_movie)

        val similarOptionsRv: RecyclerView = findViewById(R.id.similar_options_recycler)

        val id =
            intent?.getIntExtra("id", 0) ?: throw java.lang.IllegalStateException("Id not found")

        val url =
            "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=1086a7bc-65e5-4607-9b1a-d331f5df6b66"

        MovieTask(this).execute(url)

        adapter = MovieAdapter(movies, R.layout.similar_movie_item)
        similarOptionsRv.layoutManager = GridLayoutManager(this@MovieActivity, 3)
        similarOptionsRv.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResult(movieDetail: MovieDetail) {
        Log.i("MovieDetail", movieDetail.toString())
        movieTitle.text = movieDetail.movie.title
        movieDescription.text = movieDetail.movie.desc
        movieCasting.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.addAll(movieDetail.similar)
        adapter.notifyDataSetChanged()

        val coverImg: ShapeableImageView = findViewById(R.id.movie_image)
        Picasso.get().load(movieDetail.movie.coverUrl).into(coverImg)

        progressMovie.visibility = ProgressBar.GONE
    }

    override fun onPreExecute() {
        progressMovie.visibility = ProgressBar.VISIBLE
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progressMovie.visibility = ProgressBar.GONE
    }
}