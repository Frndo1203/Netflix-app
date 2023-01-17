package tech.fernandooliveira.netflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import tech.fernandooliveira.netflix.model.Movie
import tech.fernandooliveira.netflix.model.MovieDetail
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onResult(movieDetail: MovieDetail)
        fun onPreExecute()
        fun onError(message: String)
    }

    fun execute(url: String) {
        callback.onPreExecute()


        executor.execute {
            val requestURL = URL(url)

            with(requestURL.openConnection() as HttpsURLConnection) {
                requestMethod = "GET"
                connectTimeout = 2000
                readTimeout = 2000
                doInput = true
                doOutput = false

                var stream: InputStream? = null

                try {

                    val responseCode = responseCode

                    when {
                        responseCode == 400 -> {
                            stream = errorStream
                            val jsonAsString = BufferedInputStream(stream).use {
                                it.readBytes().toString(Charsets.UTF_8)
                            }

                            val json = JSONObject(jsonAsString)
                            val message = json.getString("message")
                            throw IOException(message)
                        }
                        responseCode > 400 -> {
                            throw IOException("Error on request")
                        }

                    }

                    stream = inputStream
                    val jsonAsString = stream.bufferedReader().use {
                        it.readText()
                    }

                    val movieDetail = toMovieDetail(jsonAsString)
                    Log.i("CategoryTask", "Categories: $movieDetail")

                    handler.post {
                        callback.onResult(movieDetail)
                    }

                } catch (e: IOException) {
                    Log.e("CategoryTask", "Error on request", e)

                    handler.post {
                        callback.onError(e.message ?: "Error on request")
                    }
                } finally {
                    disconnect()
                    stream?.close()
                }
            }
        }
        executor.shutdown()
    }

    private fun toMovieDetail(jsonAsString: String): MovieDetail {
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similars = mutableListOf<Movie>()
        for (i in 0 until jsonMovies.length()) {
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            similars.add(Movie(similarId, similarCoverUrl))
        }

        return MovieDetail(Movie(id, coverUrl, title, desc, cast), similars)
    }

    private fun toMovies(jsonArray: JSONArray): List<Movie> {
        val movies = mutableListOf<Movie>()

        for (i in 0 until jsonArray.length()) {
            jsonArray.getJSONObject(i).let {
                val id = it.getInt("id")
                val coverUrl = it.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }
        }

        return movies
    }
}