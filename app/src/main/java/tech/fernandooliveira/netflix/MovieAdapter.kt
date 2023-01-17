package tech.fernandooliveira.netflix

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import tech.fernandooliveira.netflix.model.Movie
import tech.fernandooliveira.netflix.util.DownloadImageTask

// Horizontal List
class MovieAdapter(
    private val movies: List<Movie>,
    @LayoutRes val layoutId: Int,
    private val onItemClickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            val imageCover: ShapeableImageView = itemView.findViewById(R.id.img_cover)
            imageCover.setOnClickListener {
                onItemClickListener?.invoke(movie.id)
            }

            DownloadImageTask(object : DownloadImageTask.Callback {
                override fun onResult(bitmap: Bitmap) {
                    imageCover.setImageBitmap(bitmap)
                }
            }).execute(movie.coverUrl)
        }
    }
}