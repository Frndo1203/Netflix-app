package tech.fernandooliveira.netflix.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import tech.fernandooliveira.netflix.model.Category
import tech.fernandooliveira.netflix.model.Movie
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onResult(categories: List<Category>)
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
                    if (responseCode > 400) {
                        throw IOException("Error on request")
                    }

                    stream = inputStream
                    val jsonAsString = stream.bufferedReader().use {
                        it.readText()
                    }

                    val categories = toCategories(jsonAsString)
                    Log.i("CategoryTask", "Categories: $categories")

                    handler.post {
                        callback.onResult(categories)
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

    private fun toCategories(json: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(json)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until jsonCategories.length()) {
            jsonCategories.getJSONObject(i).let {

                val id = it.getInt("id")
                val name = it.getString("title")
                val movies = toMovies(it.getJSONArray("movie"))

                categories.add(Category(id, name, movies))
            }
        }


        return categories
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