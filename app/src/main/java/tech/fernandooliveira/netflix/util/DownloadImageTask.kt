package tech.fernandooliveira.netflix.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class DownloadImageTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    interface Callback {
        fun onResult(bitmap: Bitmap)
    }

    fun execute(url: String) {
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
                    val bitmap = BitmapFactory.decodeStream(stream)

                    handler.post {
                        callback.onResult(bitmap)
                    }


                } catch (e: IOException) {
                    Log.e("CategoryTask", "Error on request", e)

                } finally {
                    disconnect()
                    stream?.close()
                }
            }
        }
    }
}
