package tech.fernandooliveira.netflix.model

data class MovieDetail(
    val movie: Movie, val similar: List<Movie>
)
